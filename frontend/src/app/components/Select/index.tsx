"use client";

import {
  Root,
  Trigger,
  Content,
  Item,
  Group,
  Label,
  Separator,
} from "@radix-ui/themes/components/select";
import { ISelectProps, ISelectItemProps } from "./select.types";
import { RadixColorName } from "@/app/components/types";
import styles from "./select.module.css";
import { cn } from "@/app/lib/utils";

function Select({
  items,
  children,
  label = "",
  placeholder = "",
  className = "",
  size = "3",
  variant = "surface",
  triggerBg = "transparent",
  contentBg = RadixColorName.gray,
  placeholderColor = "black",
}: ISelectProps) {
  return (
    <Root size={size}>
      <Trigger
        placeholder={placeholder}
        className={cn(styles.trigger, `bg-${triggerBg}`, className)}
        variant={variant}
        style={
          {
            "--placeholder-color": placeholderColor,
          } as React.CSSProperties
        }
      />
      <Content color={contentBg}>
        <Group>
          {label && <Label>{label}</Label>}
          {items.map((item: ISelectItemProps) => (
            <Item key={item.value} value={item.value}>
              {item.label}
            </Item>
          ))}
          {children}
        </Group>
      </Content>
    </Root>
  );
}

export {
  Select,
  Root as SelectRoot,
  Trigger as SelectTrigger,
  Content as SelectContent,
  Item as SelectItem,
  Group as SelectGroup,
  Label as SelectLabel,
  Separator as SelectSeparator,
};
