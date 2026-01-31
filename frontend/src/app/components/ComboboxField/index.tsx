"use client";
// 3rd party libraries
import { useState } from "react";
import * as Popover from "@radix-ui/react-popover";
import { ChevronDownIcon } from "@radix-ui/react-icons";
import { Text, Flex, ScrollArea } from "@radix-ui/themes";
import { Command } from "cmdk";
// hooks + methods
import { useT } from "@/app/i18n/client";
import { cn } from "@/app/lib/utils";
// components
import Badge from "@/app/components/Badge";
// types + constants
import IComboboxFieldProps from "./combobox-field.types";

export default function ComboboxField<T>({
  size = "2",
  label,
  value,
  placeholder = "Select...",
  errMsg,
  options,
  onChange,
  getOptionLabel,
  getOptionValue,
  optionFilter,
}: IComboboxFieldProps<T>) {
  const { t } = useT("common");
  const [open, setOpen] = useState(false);
  const [expanded, setExpanded] = useState(false);
  const [filterText, setFilterText] = useState("");

  const handlePopoverToggle = (isOpen: boolean) => {
    setOpen(isOpen);
    if (!isOpen) {
      setFilterText("");
    }
  };

  const handleBadgeClick = (e: React.MouseEvent, key: string) => {
    e.stopPropagation();
    removeSelection(key);
  };

  const handleToggleExpand = (e: React.MouseEvent) => {
    e.stopPropagation();
    setExpanded((prev) => !prev);
  };

  const addSelection = (key: string) => {
    const updatedValues = value.includes(key) ? value : [...value, key];
    onChange(updatedValues);
  };

  const removeSelection = (key: string) => {
    const updatedValues = value.filter((v) => v !== key);
    onChange(updatedValues);
  };

  const maxShownItems = 4;
  const visibleItems = expanded ? value : value.slice(0, maxShownItems);
  const hiddenCount = value.length - visibleItems.length;

  return (
    <div className="w-full">
      {label && (
        <Text asChild size={size}>
          <label onClick={() => setOpen(true)}>{label}</label>
        </Text>
      )}
      <Popover.Root open={open} onOpenChange={handlePopoverToggle}>
        <Popover.Trigger asChild>
          <Flex
            justify="between"
            align="center"
            px="2"
            className={cn(
              "h-auto min-h-8 w-full border border-gray-7 rounded-sm cursor-pointer py-1",
              errMsg && "border-red-500",
            )}
          >
            <div className="flex flex-wrap items-center gap-1">
              {value.length > 0 ? (
                <>
                  {visibleItems.map((key) => {
                    const option = options.find(
                      (opt) => getOptionValue(opt) === key,
                    );
                    return option ? (
                      <Badge
                        key={key}
                        text={getOptionLabel(option)}
                        handleRemoveClick={(e) => handleBadgeClick(e, key)}
                      />
                    ) : null;
                  })}
                  {hiddenCount > 0 || expanded ? (
                    <div
                      onClick={handleToggleExpand}
                      className="rounded-sm bg-gray-3 px-1"
                    >
                      {expanded ? "Show Less" : `+${hiddenCount} more`}
                    </div>
                  ) : null}
                </>
              ) : (
                <Text as="span" color="gray" size={size}>
                  {placeholder}
                </Text>
              )}
            </div>
            <ChevronDownIcon className="opacity-50" />
          </Flex>
        </Popover.Trigger>
        {errMsg && (
          <Text size="2" color="red" as="p">
            {errMsg}
          </Text>
        )}
        <Popover.Content
          sideOffset={5}
          className="bg-white border border-gray-7 rounded-sm relative z-50"
          style={{ width: "var(--radix-popover-trigger-width)" }}
        >
          <Command shouldFilter={!optionFilter}>
            <Command.Input
              placeholder="Search..."
              className="px-2 py-1 outline-0 w-full border-b border-gray-6 text-sm focus:ring-2 focus:ring-blue-7 focus:rounded-xs"
              onValueChange={(text) => setFilterText(text)}
            />
            <Command.List>
              <Command.Empty className=" place-content-center px-2 py-3 ">
                {t("no-data")}
              </Command.Empty>
              <Command.Group>
                <ScrollArea
                  type="auto"
                  scrollbars="vertical"
                  className="z-2 max-h-46"
                >
                  {(optionFilter
                    ? optionFilter(options, filterText)
                    : options
                  ).map((option) => {
                    const val = getOptionValue(option);
                    const label = getOptionLabel(option);
                    const isSelected = value.includes(val);
                    return (
                      <Command.Item
                        key={val}
                        value={val}
                        onSelect={
                          isSelected ? undefined : () => addSelection(val)
                        }
                        className={cn(
                          "px-2 py-1 hover:bg-gray-2",
                          isSelected ? "opacity-50" : "cursor-pointer",
                        )}
                      >
                        {label}
                      </Command.Item>
                    );
                  })}
                </ScrollArea>
              </Command.Group>
            </Command.List>
          </Command>
        </Popover.Content>
      </Popover.Root>
    </div>
  );
}
