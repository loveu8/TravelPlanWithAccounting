"use client";

import { useCallback, useState } from "react";
import { Tabs } from "@radix-ui/themes";
import { IUnderlineTabProps } from "../tab.types";
import { cn } from "@/app/lib/utils";
import styles from "../tab.module.css";

export default function UnderlineTab({
  items,
  defaultValue,
  onValueChange,
  underlineColor = "blue",
  size = "2",
  underlineHeight,
  className = "",
  ...props
}: IUnderlineTabProps) {
  const [activeTab, setActiveTab] = useState(defaultValue || items[0]?.value);

  const handleValueChange = useCallback(
    (value: string) => {
      setActiveTab(value);
      onValueChange?.(value);
    },
    [onValueChange],
  );

  const getCustomStyles = () => {
    if (!underlineHeight) return undefined;
    return {
      "--underline-height": `${underlineHeight}px`,
    } as React.CSSProperties;
  };

  return (
    <Tabs.Root
      value={activeTab}
      className={`${styles.tabRoot} w-full`}
      onValueChange={handleValueChange}
      style={getCustomStyles()}
      {...props}
    >
      <Tabs.List className="flex w-full" color={underlineColor} size={size}>
        {items.map((item) => (
          <Tabs.Trigger
            key={item.value}
            value={item.value}
            className={cn("cursor-pointer text-center px-4", className)}
          >
            {item.label}
          </Tabs.Trigger>
        ))}
      </Tabs.List>
      {items.map((item) => (
        <Tabs.Content key={item.value} value={item.value}>
          {item.content}
        </Tabs.Content>
      ))}
    </Tabs.Root>
  );
}
