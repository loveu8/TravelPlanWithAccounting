import { cookies } from "next/headers";
import { NextResponse } from "next/server";
import { createBackendClient } from "@/app/api/_utils/http";

export async function POST() {
  const jar = await cookies();
  const rt = jar.get("refresh_token")?.value;
  if (!rt) return NextResponse.json({ success: true }); // idempotent

  const be = await createBackendClient();
  const res = await be.post("/api/auth/logout", { refreshToken: rt });

  // Clear cookies regardless of backend outcome
  const isProd = process.env.NODE_ENV === "production";
  const resp = NextResponse.json({ success: res.status === 200 });
  resp.cookies.set("refresh_token", "", {
    httpOnly: true,
    secure: isProd,
    sameSite: "lax",
    path: "/",
    maxAge: 0,
  });
  resp.cookies.set("access_token", "", {
    httpOnly: true,
    secure: isProd,
    sameSite: "lax",
    path: "/",
    maxAge: 0,
  });
  return resp;
}
