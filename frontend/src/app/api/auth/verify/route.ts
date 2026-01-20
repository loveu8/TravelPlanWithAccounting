import { NextResponse } from "next/server";
import { cookies } from "next/headers";
import { createBackendClient } from "@/app/api/_utils/http";
import { AxiosResponse, isAxiosError } from "axios";
import type { SessionData } from "@/app/lib/types";

// Verify current session via ACCESS token from cookies; returns payload for UI
export async function POST() {
  try {
    const be = await createBackendClient();
    const jar = await cookies();
    const at = jar.get("access_token")?.value;
    if (!at) {
      return NextResponse.json({ valid: false }, { status: 401 });
    }
    const { data } = await be.post<unknown, AxiosResponse<SessionData>>(
      "/api/auth/verify-token",
      {
        tokenType: "ACCESS",
        clientId: "web",
        token: at,
      },
    );
    return NextResponse.json(data, { status: 200 });
  } catch (error) {
    if (isAxiosError(error)) {
      console.error("Error response:", error.response?.data);
    } else {
      console.error("Unexpected error:", error);
    }
    return NextResponse.json({ valid: false }, { status: 401 });
  }
}
