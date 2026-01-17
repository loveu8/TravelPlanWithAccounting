"use client";

import React, { useState } from "react";
import Iternerary from "./iternerary/index";
import Button from "@/app/components/Button";

export default function AsideMenu() {
  const [isOpen, setIsOpen] = useState(false);

  // Tab 主要項目
  const navigationItems = [
    { value: "itinerary", label: "行程列表" },
    { value: "map", label: "探索地圖" },
    { value: "favorites", label: "收藏列表" },
  ];

  // 日期列表
  const days = [
    { id: "day1", date: "5/7" },
    { id: "day2", date: "5/8" },
    { id: "day3", date: "5/9" },
    { id: "day4", date: "5/10" },
    { id: "day5", date: "5/11" },
    { id: "day6", date: "5/12" },
    { id: "day7", date: "5/13" },
  ];

  const [activeNavigation, setActiveNavigation] = useState("itinerary");
  const [activeDay, setActiveDay] = useState("day1");

  const handleToggle = () => {
    setIsOpen(!isOpen);
  };

  const handleNavigationChange = (itemId: string) => {
    setActiveNavigation(itemId);
    console.log("切換導航:", itemId);
  };

  const handleDaySelect = (dayId: string) => {
    setActiveDay(dayId);
    console.log("選擇日期:", dayId);
  };

  const handleAddDay = () => {
    console.log("新增日期");
  };

  return (
    <>
      {/* 展開按鈕 */}
      {!isOpen && <Button onClick={handleToggle} text="展開側邊欄" />}

      {/* 遮罩 */}
      {isOpen && (
        <div
          className="fixed top-0 left-0 right-0 bottom-0 bg-black/50 z-10 backdrop-blur-2px"
          onClick={handleToggle}
        />
      )}

      <div
        className={`fixed top-0 w-[660px] h-screen bg-white z-15 transition-all duration-300 overflow-x-hidden ${
          isOpen
            ? "left-0 shadow-[0px_12px_60px_0px_rgba(0,0,0,0.05),0px_12px_32px_-16px_rgba(0,0,0,0.08)]"
            : "-left-[660px]"
        }`}
      >
        <Iternerary
          travelMainId={""}
          navigationItems={navigationItems}
          activeNavigation={activeNavigation}
          onNavigationChange={handleNavigationChange}
          days={days}
          activeDay={activeDay}
          onDaySelect={handleDaySelect}
          onAddDay={handleAddDay}
        />
      </div>
    </>
  );
}
