"use client";

import React from "react";
import { Button as RadixButton } from "@radix-ui/themes/components/button";
import IButtonProps from "./button.types";

export default function Button({
  text,
  size = "3",
  isMain = true,
  icon,
  isDisabled = false,
  handleClick = () => {},
}: IButtonProps) {
  const btnColor = isMain ? "blue" : "gray";
  const btnVariant = isMain ? "solid" : "surface";

  return (
    <RadixButton
      size={size}
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
