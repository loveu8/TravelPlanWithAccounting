import { useId, useState } from "react";
import { CalendarIcon } from "@radix-ui/react-icons";
import { Popover } from "@radix-ui/themes";
import { format, isValid, parse } from "date-fns";
import Calendar from "./calendar";
import TextField, { TextFieldSlot } from "@/app/components/TextField";
import { FORMAT_TOKEN_MAP } from "./consts";

import type { IDatePickerProps } from "./date-picker.types";

function DatePicker({
  localeType = "en-US",
  label,
  id,
  name,
  value,
  onChange,
  calendarOptions = {},
}: IDatePickerProps) {
  const inputId = useId();

  // Hold the month in state to control the calendar when the input changes
  const [month, setMonth] = useState(value || new Date());

  // Hold the selected date in state (uncontrolled mode)
  const [internalSelectedDate, setInternalSelectedDate] = useState<
    Date | undefined
  >(undefined);

  // Hold the input value in state (uncontrolled mode)
  const [internalInputValue, setInternalInputValue] = useState("");

  const [popoverOpen, setPopoverOpen] = useState(false);

  const formatToken = FORMAT_TOKEN_MAP[localeType];

  // Determine if the component is controlled
  const isControlled = value !== undefined;

  const selectedDate = isControlled ? value : internalSelectedDate;
  let inputValue = internalInputValue;
  if (isControlled) {
    inputValue = value ? format(value, formatToken) : "";
  }

  const handleDayPickerSelect = (date: Date | undefined) => {
    if (!date) {
      if (!isControlled) {
        setInternalInputValue("");
        setInternalSelectedDate(undefined);
      }
      onChange?.(undefined);
    } else {
      if (!isControlled) {
        setInternalSelectedDate(date);
        setMonth(date);
        setInternalInputValue(format(date, formatToken));
      }
      onChange?.(date);
      setPopoverOpen(false);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = e.target.value;
    if (!isControlled) {
      setInternalInputValue(newValue); // keep the input value in sync
    }

    const parsedDate = parse(newValue, formatToken, new Date());

    if (isValid(parsedDate)) {
      if (!isControlled) {
        setInternalSelectedDate(parsedDate);
        setMonth(parsedDate);
      }
      onChange?.(parsedDate);
    } else {
      if (!isControlled) {
        setInternalSelectedDate(undefined);
      }
      onChange?.(undefined);
    }
  };

  return (
    <Popover.Root open={popoverOpen} onOpenChange={setPopoverOpen}>
      <Popover.Trigger>
        <TextField
          label={label}
          id={id || inputId}
          type="text"
          name={name}
          value={inputValue}
          placeholder={formatToken}
          onChange={handleInputChange}
          className="relative"
        >
          <Popover.Anchor className="absolute left-0 bottom-0 h-[100%]" />
          <TextFieldSlot side="left">
            <CalendarIcon />
          </TextFieldSlot>
        </TextField>
      </Popover.Trigger>
      <Popover.Content size="1" sideOffset={5}>
        <Calendar
          month={month}
          onMonthChange={setMonth}
          mode="single"
          selected={selectedDate}
          onSelect={handleDayPickerSelect}
          localeType={localeType}
          {...calendarOptions}
        />
      </Popover.Content>
    </Popover.Root>
  );
}

export default DatePicker;
export { Calendar };
