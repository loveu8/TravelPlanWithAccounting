"use client";

import i18next from "./i18next";
import { useParams } from "next/navigation";
import { useEffect } from "react";
import { useTranslation } from "react-i18next";

/**
 * React hook for client-side i18n translation.
 * - 自動根據 /[lng] 路徑切換語言
 * - 回傳 useTranslation 的 t function
 * @param ns - namespace 名稱或陣列（如 "common"）
 * @param options - 可選，keyPrefix 可指定 key 前綴
 * @returns useTranslation 回傳值
 */
export function useT(ns: string | string[], options?: { keyPrefix?: string }) {
  const params = useParams();
  const lng = typeof params?.lng === "string" ? params.lng : undefined;

  if (!lng) {
    throw new Error("useT is only available inside /app/[lng]");
  }

  // 切換語言（只在 client 執行）
  useEffect(() => {
    console.log("client i18next changeLanguage", lng);
    if (i18next.resolvedLanguage !== lng) {
      i18next.changeLanguage(lng);
    }
  }, [lng]);

  return useTranslation(ns, options);
}
