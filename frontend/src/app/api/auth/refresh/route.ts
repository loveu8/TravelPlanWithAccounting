import { cookies } from "next/headers";
import { NextRequest, NextResponse } from "next/server";
import { createBackendClient } from "@/app/api/_utils/http";
import { AxiosResponse, isAxiosError } from "axios";

export async function POST(req: NextRequest) {
  try {
    const jar = await cookies();
    const rt = jar.get("refresh_token")?.value;
    if (!rt) return new NextResponse("No refresh token", { status: 401 });

    const be = await createBackendClient();
    const { data } = await be.post<
      unknown,
      AxiosResponse<{ data: { accessToken: string; expiresIn: number } }>
    >("/api/auth/refresh", {
      refreshToken: rt,
      clientId: "web",
      ua: req.headers.get("user-agent") || "",
    });

    const { accessToken, expiresIn } = data.data;
    if (!accessToken || !expiresIn) {
      return new NextResponse("Invalid token response", { status: 401 });
    }

    const isProd = process.env.NODE_ENV === "production";
    const resp = NextResponse.json({
      // accessToken: accessToken,
      // expiresIn: expiresIn,
    });
    // Optionally mirror access token into short-lived HttpOnly cookie
    resp.cookies.set("access_token", accessToken, {
      httpOnly: true,
      secure: isProd,
      sameSite: "lax",
      path: "/",
      maxAge: expiresIn,
    });
    return resp;
  } catch (error) {
    if (isAxiosError(error)) {
      console.error("Error response:", error.response?.data);
    } else {
      console.error("Unexpected error:", error);
    }
    return new NextResponse("Failed to refresh token", { status: 401 });
  }
}
