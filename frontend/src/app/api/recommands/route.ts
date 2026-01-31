import { NextRequest, NextResponse } from "next/server";
import { createBackendClient } from "@/app/api/_utils/http";
import { AxiosResponse, isAxiosError } from "axios";
import { AllLocationsResponse } from "@/app/lib/types";

export async function POST(req: NextRequest) {
  try {
    const { country } = await req.json();
    const be = await createBackendClient();
    const { data } = await be.get<unknown, AxiosResponse<AllLocationsResponse>>(
      `/api/recommands/${country}`,
    );
    return NextResponse.json(data.data);
  } catch (error) {
    if (isAxiosError(error)) {
      console.error("Error response:", error.response?.data);
    } else {
      console.error("Unexpected error:", error);
    }
    return new NextResponse("Invalid request", { status: 400 });
  }
}
