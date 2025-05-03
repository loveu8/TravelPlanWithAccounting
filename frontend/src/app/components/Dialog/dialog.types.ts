import React from "react";

export default interface IDialogProps {
  isOpen: boolean; // 控制對話框開關狀態
  title: string; // 對話框標題
  closeButtons?: {
    header?: boolean; // 是否需要標題關閉按鈕
    footer?: boolean; // 是否需要底部關閉按鈕
  };
  footerBtnText?: string; // 確認按鈕文字
  footerBtnJustify?: "between" | "end" | "center"; // 確認按鈕對齊方式 (對應 CSS justify-content)
  handleToggleClick: (open: boolean) => void; // 開關狀態改變時回調
  handleFooterBtnClick?: () => void; // 確認按鈕點擊事件
  customBtn?: (size: "2" | "3") => React.ReactNode; // 自定義按鈕，buttonSize 為按鈕大小
  children: React.ReactNode; //對話框內容
}

export interface IDialogHeaderWithCloseProps {
  title: string; // 對話框標題
}

export type SizeConfigType = {
  gapSize: string; // Gap 的大小
  pSize: string; // Padding 的大小
  btnSize: "2" | "3"; // Button 的大小
};
