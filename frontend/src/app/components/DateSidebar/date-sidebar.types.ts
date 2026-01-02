import { Day } from "@/app/components/AsideMenu/aside-menu.types";

export interface DateSidebarProps {
  days: Day[];
  activeDay: string;
  travelMainId: string;
  onDaySelect: (day: Day) => void;
}
