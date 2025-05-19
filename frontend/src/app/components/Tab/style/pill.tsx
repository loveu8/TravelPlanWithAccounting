"use client";

import { useCallback, useState } from "react";
import * as Tabs from "@radix-ui/react-tabs";
import { IPillTabProps } from "../tab.types";
import { cn } from "@/app/lib/utils";
import styles from "../tab.module.css";

export default function PillTab({
  items,
  defaultValue,
  onValueChange,
  backgroundColor = "bg-gray-100",
  ...props
}: IPillTabProps) {
  const [activeTab, setActiveTab] = useState(defaultValue || items[0]?.value);
  const handleValueChange = useCallback(
    (value: string) => {
      setActiveTab(value);
      onValueChange?.(value);
    },
    [onValueChange],
  );

  return (
    <Tabs.Root
      value={activeTab}
      className="h-[32px]"
      onValueChange={handleValueChange}
      {...props}
    >
      <Tabs.List className={`${backgroundColor} rounded-[4px] w-full`}>
        {items.map((item, idx) => (
          <Tabs.Trigger
            key={item.value}
            value={item.value}
            className={cn(
              "cursor-pointer p-[6px]",
              activeTab === item.value
                ? styles["active-pill"]
                : cn(
                    "bg-transparent",
                    idx < items.length - 1 &&
                      activeTab !== items[idx + 1].value &&
                      "border-r border-gray-100",
                  ),
            )}
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
