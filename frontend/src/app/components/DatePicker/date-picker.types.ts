import type { DayPickerProps } from "react-day-picker";
import type ITextFieldProps from "@/app/components/TextField/text-field.types";

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
   * 輸入框的大小設定
   */
  size?: ITextFieldProps["size"];
  /**
   * 是否為必填欄位
   */
  required?: boolean;
  /**
   * 多語系支援
   */
  lng?: string;
  /**
   * 覆寫日曆元件屬性
   * @todo 這裡 exclude mode 因為目前沒有支援多型
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
