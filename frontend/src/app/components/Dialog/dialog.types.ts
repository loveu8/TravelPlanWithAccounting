import React from "react";
import { ContentProps, TitleProps } from "@radix-ui/themes/components/dialog";

export type SizeConfigType = {
  pSizeTW: string; // Padding 的大小 (Tailwind CSS)
  gapSize: string; // Gap 的大小
  pSize: string; // Padding 的大小
  titleSize: "3" | "6"; // 標題的大小
  titleWeight: "bold" | "regular"; // 標題的字重
  btnSize: "2" | "3"; // Button 的大小
};

export interface IDialogContentProps extends ContentProps {
  headerWithClose?: boolean; // 是否需要標題關閉按鈕
  className?: string; // 額外的 CSS 類別名稱
  children: React.ReactNode; // Dialog 內容的子元素
}

export interface IDialogHeaderProps extends TitleProps {
  title: string; // Dialog 的標題文字
}

export interface IDialogBodyProps {
  className?: string; // 額外的 CSS 類別名稱
  children: React.ReactNode; // Dialog 的內容
}

export interface IDialogFooterProps {
  withCloseBtn?: boolean; // 是否需要底部關閉按鈕
  justify?: "between" | "end" | "center"; // 確認按鈕對齊方式 (對應 CSS justify-content)
  children: React.ReactNode; // DialogFooter 的內容，按鈕大小請設定為 "2" 或 "3"
}
