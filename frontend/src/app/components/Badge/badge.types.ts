import { BadgeProps } from "@radix-ui/themes/components/badge";

export default interface IBadgeProps extends BadgeProps {
  text: string; //徽章文字
  bgColor?: "blue" | "gray" | "transparent"; //徽章顏色
  icon?: React.ReactNode; //icon圖示，請設定width和height為20px
  handleRemoveClick?: React.MouseEventHandler<SVGElement>; //刪除圖示點擊事件
}
