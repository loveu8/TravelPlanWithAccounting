"use client";

import i18next from "./i18next";
import { useParams } from "next/navigation";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";

const runsOnServerSide = typeof window === "undefined";

export function useT(ns: string | string[], options?: { keyPrefix?: string }) {
  const lng = useParams()?.lng;

  if (typeof lng !== "string") {
    throw new Error("useT is only available inside /app/[lng]");
  }

  const [activeLng, setActiveLng] = useState(() => i18next.resolvedLanguage);

  useEffect(() => {
    if (runsOnServerSide) {
      // 在伺服器端執行語言切換
      if (i18next.resolvedLanguage !== lng) {
        i18next.changeLanguage(lng);
      }
    } else {
      // 在客戶端執行語言切換
      if (activeLng !== i18next.resolvedLanguage) {
        setActiveLng(i18next.resolvedLanguage);
      }
    }
  }, [lng, activeLng]);

  useEffect(() => {
    if (!runsOnServerSide && lng && i18next.resolvedLanguage !== lng) {
      i18next.changeLanguage(lng);
    }
  }, [lng]);

  return useTranslation(ns, options);
}
