// 預設語言（找不到時 fallback 用）
export const fallbackLng = "en";

// 支援的語言列表（第一個為預設語言）
export const languages = [fallbackLng, "zh"];

// 預設 namespace（對應語言檔案名稱）
export const defaultNS = "common";

// 存放語言的 cookie 名稱
export const cookieName = "i18next";

// SSR 與 middleware 傳遞語言用的自訂 header 名稱
export const headerName = "x-i18next-current-language";
