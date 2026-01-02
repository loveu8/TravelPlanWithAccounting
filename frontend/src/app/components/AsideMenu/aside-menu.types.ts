import { ITabItem } from "@/app/components/Tab/tab.types";

export interface Day {
  id: string;
  date: string;
  itemsCount?: number;
}

export interface AsideMenuContainerProps {
  travelMainId: string;
}

export interface AsideMenuProps {
  navigationItems: ITabItem[];
  activeNavigation: string;
  days: Day[];
  activeDay: string;
  travelMainId: string;
  onNavigationChange: (itemId: string) => void;
  onDaySelect: (day: Day) => void;
}
