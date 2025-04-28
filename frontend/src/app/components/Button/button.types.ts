import { ButtonProps } from "@radix-ui/themes/components/button";

export default interface IButtonProps extends ButtonProps {
  text: string; //按鈕文字
  isMain?: boolean; //是否為主要按鈕
  icon?: React.ReactNode; //按鈕圖示
  isDisabled?: boolean; //是否禁用按鈕
  handleClick?: () => void; //按鈕點擊事件
}
