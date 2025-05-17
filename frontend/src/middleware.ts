import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";
import acceptLanguage from "accept-language";
import {
  fallbackLng,
  languages,
  cookieName,
  headerName,
} from "./app/i18n/settings";

acceptLanguage.languages(languages);

/**
 * middleware for i18n:
 * - 偵測語言（cookie > Accept-Language > fallback）
 * - 自動補語言前綴
 * - 設定自訂 header 讓 SSR 能取得語言
 * - 處理 referer，將語言寫入 cookie
 * @param {NextRequest} req - Next.js 請求物件
 * @returns {NextResponse} - 處理後的回應
 */
export function middleware(req: NextRequest): NextResponse {
  // 排除 icon 與 chrome 路徑
  if (
    req.nextUrl.pathname.includes("icon") ||
    req.nextUrl.pathname.includes("chrome")
  ) {
    return NextResponse.next();
  }

  let lng;
  // 1. 從 cookie 取得語言
  if (req.cookies.has(cookieName)) {
    const cookie = req.cookies.get(cookieName);
    lng = acceptLanguage.get(cookie?.value ?? "");
  }
  // 2. 從 Accept-Language header 取得語言
  if (!lng) lng = acceptLanguage.get(req.headers.get("Accept-Language"));
  // 3. fallback
  if (!lng) lng = fallbackLng;

  // 檢查路徑是否已經有語言前綴
  const lngInPath = languages.find((loc) =>
    req.nextUrl.pathname.startsWith(`/${loc}`),
  );

  console.log("middleware detect lng:", lng, "lngInPath:", lngInPath);

  // 設定自訂 header，讓 SSR 能取得語言
  const headers = new Headers(req.headers);
  headers.set(headerName, lngInPath || lng);

  // 若路徑沒有語言前綴，自動 redirect
  if (!lngInPath && !req.nextUrl.pathname.startsWith("/_next")) {
    return NextResponse.redirect(
      new URL(`/${lng}${req.nextUrl.pathname}${req.nextUrl.search}`, req.url),
    );
  }

  // 若有 referer，從 referer 路徑取得語言並寫入 cookie
  if (req.headers.has("referer")) {
    const refererUrl = new URL(req.headers.get("referer")!);
    const lngInReferer = languages.find((l) =>
      refererUrl.pathname.startsWith(`/${l}`),
    );
    const response = NextResponse.next({ headers });
    if (lngInReferer) response.cookies.set(cookieName, lngInReferer);
    return response;
  }

  // 預設回傳，帶上語言 header
  return NextResponse.next({ headers });
}

/**
 * 設定 matcher：
 * 指定哪些路徑會被 middleware 處理
 * 以下路徑會被排除：
 * - /api
 * - /_next/static
 * - /_next/image
 * - /assets
 * - /favicon.ico
 * - /sw.js
 * - /site.webmanifest
 * 其餘所有路徑都會進入 middleware。
 */
export const config = {
  matcher: [
    "/((?!api|_next/static|_next/image|assets|favicon.ico|sw.js|site.webmanifest).*)",
  ],
};
