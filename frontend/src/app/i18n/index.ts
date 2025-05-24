import i18next from "./i18next";
import { headerName, fallbackLng } from "./settings";
import { headers } from "next/headers";

/**
 * SSR/SSG 專用的 i18n helper，根據語言與 namespace 回傳 t function
 * 1. 依序從 header、參數、i18next 狀態、fallback 決定語言
 * 2. 切換 i18next 語言（若需要）
 * 3. 載入 namespace（若尚未載入）
 * 4. 回傳 getFixedT，可直接用於翻譯
 *
 * @param lng - 語言代碼（如 "en"、"zh"）
 * @param ns - namespace 名稱或陣列（如 "common"）
 * @param options - 可選，keyPrefix 可指定 key 前綴
 * @returns { t, i18n } - t 為翻譯函式，i18n 為 i18next instance
 */
export async function getT(
  lng: string,
  ns: string | string[],
  options?: { keyPrefix?: string },
) {
  // 取得 request headers
  const headerList = await headers();
  // 決定要用的語言，優先順序：header > 傳入參數 > i18next 狀態 > fallback
  const detectedLng =
    headerList.get(headerName) ??
    lng ??
    i18next.resolvedLanguage ??
    fallbackLng;

  // 若語言不同則切換
  if (detectedLng && i18next.resolvedLanguage !== detectedLng) {
    await i18next.changeLanguage(detectedLng);
  }
  // 若 namespace 尚未載入則載入
  if (ns && !i18next.hasLoadedNamespace(ns)) {
    await i18next.loadNamespaces(ns);
  }
  console.log("SSR getT detectedLng:", detectedLng);

  // 回傳 t function 與 i18next instance
  return {
    t: i18next.getFixedT(
      detectedLng,
      Array.isArray(ns) ? ns[0] : ns,
      options?.keyPrefix,
    ),
    i18n: i18next,
  };
}
