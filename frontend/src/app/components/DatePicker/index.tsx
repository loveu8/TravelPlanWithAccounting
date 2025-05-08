import { useId, useState } from "react";
import { CalendarIcon } from "@radix-ui/react-icons";
import { Popover } from "@radix-ui/themes";
import { format, isValid, parse } from "date-fns";
import Calendar from "./calendar";
import TextField, { TextFieldSlot } from "@/app/components/TextField";
import { FORMAT_TOKEN_MAP } from "./consts";

import type { IDatePickerProps } from "./date-picker.types";

function DatePicker({ localeType = "en-US", label, id }: IDatePickerProps) {
  const inputId = useId();

  // Hold the month in state to control the calendar when the input changes
  const [month, setMonth] = useState(new Date());

  // Hold the selected date in state
  const [selectedDate, setSelectedDate] = useState<Date | undefined>(undefined);

  // Hold the input value in state
  const [inputValue, setInputValue] = useState("");

  const formatToken = FORMAT_TOKEN_MAP[localeType];

  const handleDayPickerSelect = (date: Date | undefined) => {
    if (!date) {
      setInputValue("");
      setSelectedDate(undefined);
    } else {
      setSelectedDate(date);
      setMonth(date);
      setInputValue(format(date, formatToken));
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(e.target.value); // keep the input value in sync

    const parsedDate = parse(e.target.value, formatToken, new Date());

    if (isValid(parsedDate)) {
      setSelectedDate(parsedDate);
      setMonth(parsedDate);
    } else {
      setSelectedDate(undefined);
    }
  };

  return (
    <Popover.Root>
      <Popover.Trigger>
        <TextField
          label={label}
          id={id || inputId}
          type="text"
          value={inputValue}
          placeholder={formatToken}
          onChange={handleInputChange}
          className="relative"
        >
          <Popover.Anchor className="absolute left-0 bottom-0" />
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
        />
      </Popover.Content>
    </Popover.Root>
  );
}

export default DatePicker;
export { Calendar };
