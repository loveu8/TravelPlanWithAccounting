"use client";

import * as Tabs from "@radix-ui/react-tabs";
import { IPillTabProps } from "../tab.types";
import { useCallback, useState } from "react";

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
            className={`
              cursor-pointer p-[6px]
              ${activeTab === item.value ? "bg-white shadow" : "bg-transparent"}
              ${idx !== items.length - 1 ? "border-r border-gray-100" : ""}
            `}
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
