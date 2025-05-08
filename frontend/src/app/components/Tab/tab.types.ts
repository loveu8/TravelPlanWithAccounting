import { Tabs } from "@radix-ui/themes";
import { RadixColorName } from "@/app/components/types";
import type { Responsive } from "@radix-ui/themes/src/props/prop-def.js";

export interface ITabItem {
  value: string;
  label: string;
  content?: React.ReactNode | string;
}

type TabSize = "1" | "2";

export default interface ITabProps
  extends Omit<React.ComponentProps<typeof Tabs.Root>, "children"> {
  items: ITabItem[]; // 標籤列表
  defaultValue?: string; // 預設選項
  onValueChange?: (value: string) => void; // 項目變更事件
  color?: keyof typeof RadixColorName; // active 底線顏色
  indicatorHeight?: number; // active 底線高度
  size?: Responsive<TabSize>; // 標籤大小
}
