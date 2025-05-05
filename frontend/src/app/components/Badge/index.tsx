"use client";

import React from "react";
import { Badge as RadixBadge } from "@radix-ui/themes/components/badge";
import { CrossCircledIcon } from "@radix-ui/react-icons";
import IBadgeProps from "./badge.types";

export default function Badge({
  text,
  bgColor = "gray",
  icon,
  handleRemoveClick,
}: IBadgeProps) {
  const isTransparent = bgColor === "transparent";
  const badgeColor = isTransparent ? "gray" : bgColor;
  const badgeSize = !handleRemoveClick && !icon ? "2" : "3";

  return (
    <RadixBadge
      size={badgeSize}
      color={badgeColor}
      variant="soft"
      className={`${isTransparent ? "bg-transparent" : ""}   ${icon ? "text-base" : ""}`}
    >
      {icon}
      {handleRemoveClick && (
        <CrossCircledIcon
          fontSize="16px"
          className="cursor-pointer"
          onClick={handleRemoveClick}
        />
      )}
      {text}
    </RadixBadge>
  );
}
