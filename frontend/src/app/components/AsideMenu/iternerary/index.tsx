import React from "react";
import { AsideMenuProps } from "../aside-menu.types";
import { UnderlineTab } from "@/app/components/Tab";
import Card from "./ItineraryCard";
import DateSidebar from "@/app/components/DateSidebar";

export default function AsideMenu({
  navigationItems,
  activeNavigation,
  onNavigationChange,
  days,
  activeDay,
  onDaySelect,
  onAddDay,
}: AsideMenuProps) {
  return (
    <div className="flex w-full h-full max-w-[660px] flex-col">
      {/* Card */}
      <Card />
      {/* Tab */}
      <div className="border-t border-[#DDDDDD]">
        <UnderlineTab
          className="flex-none"
          items={navigationItems}
          defaultValue={activeNavigation}
          onValueChange={onNavigationChange}
          size="1"
          underlineColor="blue"
          underlineHeight={2}
        />
      </div>

      {/* 日期導航 */}
      <DateSidebar
        days={days}
        activeDay={activeDay}
        onDaySelect={onDaySelect}
        onAddDay={onAddDay}
      />
    </div>
  );
}
