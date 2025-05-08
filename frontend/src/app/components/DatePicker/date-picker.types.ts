import type { DayPicker } from "react-day-picker";

export type SupportLocaleType = "en-US" | "zh-TW";

export type CalendarProps = React.ComponentProps<typeof DayPicker> & {
  localeType: SupportLocaleType;
};

export interface IDatePickerProps {
  id?: string;
  label?: string;
  localeType?: SupportLocaleType;
}
