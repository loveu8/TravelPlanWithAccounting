import { TabsProps } from "@radix-ui/themes/components/tabs";
import type { Responsive } from "@radix-ui/themes/src/props/prop-def.js";

export interface ITabItem {
  value: string;
  label: string;
  content?: React.ReactNode;
}

type TabSize = "1" | "2" | "3";

export default interface ITabProps extends Omit<TabsProps, "children"> {
  items: ITabItem[]; // 標籤列表
  defaultValue?: string; // 預設選項
  onValueChange?: (value: string) => void; // 項目變更事件
  color?: string; // active 底線顏色
  indicatorHeight?: number; // active 底線高度
  size?: Responsive<TabSize>; // 標籤大小
}
