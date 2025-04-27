"use client";

import React from "react";
import {
  Button as RadixButton,
  ButtonProps,
} from "@radix-ui/themes/components/button";

interface IButtonProps extends ButtonProps {
  text: string; //按鈕文字
  isMain?: boolean; //是否為主要按鈕
  icon?: React.ReactNode; //按鈕圖示
  isDisabled?: boolean; //是否禁用按鈕
  handleClick?: () => void; //按鈕點擊事件
}

export default function Button({
  text,
  isMain = true,
  icon,
  isDisabled = false,
  handleClick = () => {},
}: IButtonProps) {
  const btnColor = isMain ? "blue" : "gray";
  const btnVariant = isMain ? "solid" : "outline";

  return (
    <RadixButton
      color={btnColor}
      variant={btnVariant}
      className={isDisabled ? "" : "cursor-pointer"}
      disabled={isDisabled}
      onClick={() => {
        if (isDisabled) return;
        handleClick();
      }}
    >
      {icon && icon}
      {text}
    </RadixButton>
  );
}
