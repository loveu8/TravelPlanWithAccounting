import { NextRequest, NextResponse } from "next/server";
import { createBackendClient } from "@/app/api/_utils/http";
import { AxiosResponse, isAxiosError } from "axios";
import { format } from "date-fns";
import type { UpsertTravelMainResponse } from "@/app/lib/types";

export async function POST(req: NextRequest) {
  try {
    const { startDate, endDate, ...rest } = (await req.json()) as Record<
      string,
      unknown
    >;
    const body = {
      startDate: format(new Date(startDate as string), "yyyy-MM-dd"),
      endDate: format(new Date(endDate as string), "yyyy-MM-dd"),
      ...rest,
    };

    const be = await createBackendClient({ attachAuth: true });
    const { data } = await be.post<
      unknown,
      AxiosResponse<UpsertTravelMainResponse>
    >("/api/travels/upsertTravelMain", body);

    return NextResponse.json(data);
  } catch (error) {
    if (isAxiosError(error)) {
      console.error("Error response:", error.response?.data);
    } else {
      console.error("Unexpected error:", error);
    }
    return new NextResponse("Invalid request", { status: 400 });
  }
}
