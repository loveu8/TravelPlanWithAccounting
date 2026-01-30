import { NextRequest, NextResponse } from "next/server";
import { createBackendClient } from "@/app/api/_utils/http";
import { AxiosResponse, isAxiosError } from "axios";
import { format } from "date-fns";
import type { CreateTravelMainResponse } from "@/app/lib/types";

export async function POST(req: NextRequest) {
  try {
    const body = (await req.json()) as Record<string, unknown>;
    const formatData = Object.entries(body).reduce<Record<string, unknown>>(
      (acc, [key, value]) => {
        const isDateValue = value instanceof Date;
        acc[key] = isDateValue ? format(value as Date, "yyyy-MM-dd") : value;
        return acc;
      },
      {},
    );

    const be = await createBackendClient({ attachAuth: true });
    const { data } = await be.post<
      unknown,
      AxiosResponse<CreateTravelMainResponse>
    >("/api/travels/createTravelMain", formatData);
    return NextResponse.json(data);
  } catch (error) {
    if (isAxiosError(error)) {
      console.error("Error response:", error.response?.data);
    } else {
      console.error("Unexpected error:", error);
    }
    return new NextResponse(error, { status: 400 });
  }
}
