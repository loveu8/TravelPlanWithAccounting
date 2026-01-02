import { Day } from "@/app/components/AsideMenu/aside-menu.types";

export interface DateSidebarProps {
  days: Day[];
  activeDay: string;
  onDaySelect: (dayId: string) => void;
  onAddDay: () => void;
}
