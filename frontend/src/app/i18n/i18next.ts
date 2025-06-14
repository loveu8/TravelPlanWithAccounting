import i18next from "i18next";
import LanguageDetector from "i18next-browser-languagedetector";
import resourcesToBackend from "i18next-resources-to-backend";
import { initReactI18next } from "react-i18next/initReactI18next";
import { fallbackLng, languages, defaultNS } from "./settings";

const runsOnServerSide = typeof window === "undefined";

/**
 * 初始化 i18next：
 * - 使用 react-i18next 綁定
 * - 使用 i18next-browser-languagedetector 偵測語言
 * - 使用 resourcesToBackend 動態載入語言檔案
 * - 設定支援語言、預設語言、namespace、偵測順序
 * - SSR 時預先載入所有語言
 */
if (!i18next.isInitialized) {
  i18next
    .use(initReactI18next) // 綁定 react-i18next
    .use(LanguageDetector) // 語言偵測（path, htmlTag, cookie, navigator）
    .use(
      resourcesToBackend(
        // 動態載入語言檔案
        (language: string, namespace: string) =>
          import(`./locales/${language}/${namespace}.json`),
      ),
    )
    .init({
      supportedLngs: languages, // 支援語言
      fallbackLng, // 預設語言
      lng: undefined, // 讓偵測器決定語言
      fallbackNS: defaultNS, // 預設 namespace
      defaultNS,
      detection: {
        order: ["path", "htmlTag", "cookie", "navigator"], // 偵測語言順序
      },
      preload: runsOnServerSide ? languages : [], // SSR 時預先載入所有語言
    });
}

export default i18next;
