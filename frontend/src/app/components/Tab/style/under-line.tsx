"use client";

import { Tabs } from "@radix-ui/themes";
import { IUnderlineTabProps } from "../tab.types";
import { useCallback, useState } from "react";
import styles from "../tab.module.css";

export default function UnderlineTab({
  items,
  defaultValue,
  onValueChange,
  underlineColor = "blue",
  size = "2",
  underlineHeight,
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

  return (
    <Tabs.Root
      value={activeTab}
      className={styles.tabRoot}
      onValueChange={handleValueChange}
      style={
        underlineHeight
          ? ({
              "--underline-height": `${underlineHeight}px`,
            } as React.CSSProperties)
          : undefined
      }
      {...props}
    >
      <Tabs.List color={underlineColor} size={size}>
        {items.map((item) => (
          <Tabs.Trigger
            key={item.value}
            value={item.value}
            className="cursor-pointer"
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
