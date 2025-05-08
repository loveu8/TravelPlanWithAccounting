"use client";

import { ChevronLeftIcon, ChevronRightIcon } from "@radix-ui/react-icons";
import { DayPicker } from "react-day-picker";
import { cn } from "@/app/lib/utils";
import { LOCALE_MAP } from "./consts";
import type { CalendarProps } from "./date-picker.types";
import "react-day-picker/style.css";
import "./calendar.css";

function Calendar({
  className,
  classNames,
  showOutsideDays = true,
  localeType = "en-US",
  ...props
}: CalendarProps) {
  return (
    <DayPicker
      locale={LOCALE_MAP[localeType]}
      showOutsideDays={showOutsideDays}
      className={cn("p-3", className)}
      classNames={{
        root: "rdp-root size-fit",
        selected: "bg-blue-100 text-blue-900 rounded-sm",
        // day: "hover:bg-blue-500 hover:text-white rounded-sm",
        ...classNames,
      }}
      components={{
        Chevron: (props) => {
          if (props.orientation === "left") {
            return (
              <ChevronLeftIcon
                className={cn("h-4 w-4", className)}
                {...props}
              />
            );
          }
          return <ChevronRightIcon className={cn("h-4 w-4", className)} />;
        },
      }}
      {...props}
    />
  );
}
Calendar.displayName = "Calendar";

export default Calendar;
