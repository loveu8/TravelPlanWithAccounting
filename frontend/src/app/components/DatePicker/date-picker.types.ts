import type { DayPickerProps } from "react-day-picker";

export type SupportLocaleType = "en-US" | "zh-TW";

export type CalendarProps = DayPickerProps & {
  localeType: SupportLocaleType;
};

interface IDatePickerBaseProps {
  /**
   * Input 欄位可傳入的 id
   */
  id?: string;
  /**
   * Input 欄位可傳入的 name
   */
  name?: string;
  /**
   * 輸入框上方的標籤
   */
  label?: string;
  /**
   * 多語系支援
   */
  localeType?: SupportLocaleType;
  /**
   * 覆寫日曆元件屬性 (不包含 mode 因為還沒想好 type 怎麼支援)
   */
  calendarOptions?: Omit<DayPickerProps, "mode">;
}

export type IDatePickerProps = IDatePickerBaseProps &
  (
    | {
        value?: undefined; // uncontrolled 模式
        onChange?: (date: Date | undefined) => void;
      }
    | {
        value: Date; // controlled 模式
        onChange: (date: Date | undefined) => void;
      }
  );
