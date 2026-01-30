"use client";

import React from "react";
import { Badge as RadixBadge } from "@radix-ui/themes/components/badge";
import { CrossCircledIcon } from "@radix-ui/react-icons";
import IBadgeProps from "./badge.types";
import { cn } from "@/app/lib/utils";

export default function Badge({
  text,
  bgColor = "gray",
  size = "2",
  icon,
  handleRemoveClick,
  ...props
}: IBadgeProps) {
  const isTransparent = bgColor === "transparent";
  const badgeColor = isTransparent ? "gray" : bgColor;

  return (
    <RadixBadge
      size={size}
      color={badgeColor}
      variant="soft"
      className={cn(
        isTransparent && "bg-transparent",
        icon && "text-base",
        props.className,
      )}
    >
      {icon}
      {handleRemoveClick && (
        <button type="button" className="mr-0.5" onClick={handleRemoveClick}>
          <CrossCircledIcon fontSize="12px" className="cursor-pointer" />
        </button>
      )}
      {text}
    </RadixBadge>
  );
}
