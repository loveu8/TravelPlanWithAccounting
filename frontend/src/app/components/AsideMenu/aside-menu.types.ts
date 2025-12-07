import { ITabItem } from "@/app/components/Tab/tab.types";

export interface Day {
  id: string;
  date: string;
  itemsCount?: number;
}

export interface AsideMenuProps {
  navigationItems: ITabItem[];
  activeNavigation: string;
  days: Day[];
  activeDay: string;
  onNavigationChange: (itemId: string) => void;
  onDaySelect: (dayId: string) => void;
  onAddDay: () => void;
}

export interface DateSidebarProps {
  days: Day[];
  activeDay: string;
  onDaySelect: (dayId: string) => void;
  onAddDay: () => void;
}
