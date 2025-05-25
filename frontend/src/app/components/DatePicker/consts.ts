import { enUS, zhTW } from "react-day-picker/locale";
import type { Locale } from "react-day-picker";

import type { SupportLocaleType } from "./date-picker.types";

export const LOCALE_MAP: Record<SupportLocaleType, Locale> = {
  "en-US": enUS,
  "zh-TW": zhTW,
};

export const FORMAT_TOKEN_MAP: Record<SupportLocaleType, string> = {
  "en-US": "MM/dd/yyyy",
  "zh-TW": "yyyy/MM/dd",
};
