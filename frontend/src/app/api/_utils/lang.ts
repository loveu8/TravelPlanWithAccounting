import { cookies } from "next/headers";

const I18N_COOKIE = "i18next";

export function normalizeLang(lng?: string): string | undefined {
  if (!lng) return undefined;
  const l = lng.toLowerCase();
  if (l.startsWith("zh")) return "zh-TW";
  if (l.startsWith("en")) return "en-US";
  return lng;
}

export async function getLangFromCookies(
  defaultLang = "zh-TW",
): Promise<string> {
  const jar = await cookies();
  const raw = jar.get(I18N_COOKIE)?.value;
  return normalizeLang(raw) || defaultLang;
}
