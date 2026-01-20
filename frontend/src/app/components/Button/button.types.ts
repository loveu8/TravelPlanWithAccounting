import { ButtonProps } from "@radix-ui/themes/components/button";

export default interface IButtonProps extends ButtonProps {
  text?: string; // 按鈕文字
  size?: "2" | "3"; // 按鈕大小，預設為3，2為小型按鈕
  isMain?: boolean; // 是否為主要按鈕
  icon?: React.ReactNode; // 按鈕圖示
  isDisabled?: boolean; // 是否禁用按鈕
  children?: React.ReactNode; // 插槽內容
  handleClick?: (
    event?: React.MouseEvent<HTMLButtonElement>, // 可選的事件參數
  ) => void; // 按鈕點擊事件
}
