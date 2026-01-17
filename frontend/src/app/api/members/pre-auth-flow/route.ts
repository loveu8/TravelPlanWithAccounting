import { NextRequest, NextResponse } from "next/server";
import { createBackendClient } from "@/app/api/_utils/http";
import { AxiosResponse, isAxiosError } from "axios";
import type { PreAuthFlowResponse } from "@/app/lib/types";

export async function POST(req: NextRequest) {
  try {
    const body = await req.json();
    const be = await createBackendClient();
    const { data } = await be.post<unknown, AxiosResponse<PreAuthFlowResponse>>(
      "/api/members/pre-auth-flow",
      body,
    );
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
