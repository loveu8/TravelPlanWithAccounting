import { DateSidebarProps } from "./date-sidebar.types";
import { Day } from "@/app/components/AsideMenu/aside-menu.types";
import Button from "@/app/components/Button";
import { PlusCircledIcon } from "@radix-ui/react-icons";

export default function DateSidebar({
  days,
  activeDay,
  travelMainId,
  onDaySelect,
}: DateSidebarProps) {
  const onAddDay = async (travelMainId: string) => {
    try {
      // TODO: API: addTravelDate
      console.log("onAddDay", travelMainId);
    } catch (error) {
      // TODO:
      console.error(error);
    }
  };
  return (
    <div className="w-[70px] p-[14px_10px] flex flex-col bg-[#F9F9F9]">
      <div>
        {days.map((day: Day, index: number) => (
          <Button
            key={day.id}
            className={`mb-2 w-full h-[50px] bg-transparent p-[8px_4px] transition-all duration-200 text-[#374151] flex flex-col whitespace-nowrap rounded-md ${
              activeDay === day.id ? "bg-[#3E63DD] text-white" : ""
            }`}
            handleClick={() => onDaySelect(day)}
          >
            <span className="font-bold text-[11px] leading-tight">
              Day {index + 1}
            </span>
            <span className="font-bold text-[11px] leading-tight">
              {day.date}
            </span>
          </Button>
        ))}
      </div>

      <Button
        className="border-dashed border-gray-300 !bg-transparent text-black size-[50px]"
        handleClick={() => onAddDay(travelMainId)}
      >
        <PlusCircledIcon width={16} height={16} />
      </Button>
    </div>
  );
}
