import { NextRequest, NextResponse } from "next/server";
import { createBackendClient } from "@/app/api/_utils/http";
import { AxiosResponse, isAxiosError } from "axios";
import type { AuthResponse } from "@/app/lib/types";

// Call backend auth-flow, then set HttpOnly cookies for access_token and refresh_token
export async function POST(req: NextRequest) {
  try {
    const body = await req.json();
    const be = await createBackendClient();
    const res = await be.post<unknown, AxiosResponse<AuthResponse>>(
      "/api/members/auth-flow",
      {
        ...body,
        clientId: "web",
      },
    );

    const { data } = res.data;

    const resp = NextResponse.json({
      id: data.id,
      role: data.role,
      // accessToken: data.cookies.access_token.code,
    });

    const isProd = process.env.NODE_ENV === "production";
    // Set refresh_token as HttpOnly Secure SameSite=Lax
    resp.cookies.set("refresh_token", data.cookies.refresh_token.code, {
      httpOnly: true,
      secure: isProd,
      sameSite: "lax",
      path: "/",
      maxAge: data.cookies.refresh_token.time, // seconds
    });
    // Optionally also set access_token cookie for SSR convenience (short-lived)
    resp.cookies.set("access_token", data.cookies.access_token.code, {
      httpOnly: true,
      secure: isProd,
      sameSite: "lax",
      path: "/",
      maxAge: data.cookies.access_token.time, // seconds
    });

    return resp;
  } catch (error) {
    if (isAxiosError(error)) {
      console.error("Error response:", error.response?.data);
    } else {
      console.error("Unexpected error:", error);
    }
    return new NextResponse("Invalid request", { status: 400 });
  }
}
