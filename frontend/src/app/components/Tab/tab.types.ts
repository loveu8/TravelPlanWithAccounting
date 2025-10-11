import { Tabs } from "@radix-ui/themes";
import { RadixColorName } from "@/app/components/types";
import type { Responsive } from "@radix-ui/themes/src/props/prop-def.js";

export interface ITabItem {
  value: string;
  label: string;
  content?: React.ReactNode | string;
}

type TabSize = "1" | "2";

/*
  tabs have two different UI:
  underline: https://www.radix-ui.com/themes/docs/components/tabs
  pill: https://ui.shadcn.com/docs/components/tabs
*/

/**
 * 共用 props
 * @property {ITabItem[]} items         - 標籤列表
 * @property {Responsive<TabSize>} size - 標籤大小
 * @property {string} defaultValue      - 預設 active 選項
 * @property {string} backgroundColor   - 標籤背景顏色
 * @property {Function} onValueChange   - active 項目變更事件
 */
interface ICommonProps
  extends Omit<React.ComponentProps<typeof Tabs.Root>, "children"> {
  items: ITabItem[];
  size?: Responsive<TabSize>;
  defaultValue?: string;
  backgroundColor?: string;
  onValueChange?: (value: string) => void;
}

/**
 * 底線樣式 props
 * @property {keyof typeof RadixColorName} underlineColor - active 底線顏色
 * @property {number} underlineHeight                     - active 底線高度
 * @property {string} className                           - 自定義樣式類名
 */
export interface IUnderlineTabProps extends ICommonProps {
  underlineColor?: keyof typeof RadixColorName;
  underlineHeight?: number;
  className?: string;
}

export type IPillTabProps = Omit<ICommonProps, "size">;
