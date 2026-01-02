"use client";

import React from "react";
import { Button as RadixButton } from "@radix-ui/themes/components/button";
import IButtonProps from "./button.types";
import { cn } from "@/app/lib/utils";

export default function Button({
  text,
  size = "3",
  isMain = true,
  icon,
  isDisabled = false,
  handleClick,
  children,
  ...props
}: IButtonProps) {
  const btnColor = isMain ? "blue" : "gray";
  const btnVariant = isMain ? "solid" : "surface";

  return (
    <RadixButton
      size={size}
      color={btnColor}
      variant={btnVariant}
      className={cn(!isDisabled && "cursor-pointer", props.className)}
      disabled={isDisabled}
      onClick={(e) => {
        if (props.onClick) props.onClick(e); // 保留 Radix 的內部邏輯
        if (handleClick) handleClick(e); // 執行自訂邏輯
      }}
    >
      {children || (
        <>
          {icon}
          {text}
        </>
      )}
    </RadixButton>
  );
}
