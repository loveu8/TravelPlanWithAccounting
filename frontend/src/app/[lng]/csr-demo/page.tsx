"use client";

import { useState } from "react";
import Link from "next/link";
import { useT } from "@/app/i18n/client";
import AsideMenu from "@/app/components/AsideMenu/AsideMenu";

import Button from "@/app/components/Button";
import {
  CountryCard,
  LandScapeCard,
  MyTravelPlanCard,
  TravelPlanCard,
} from "@/app/components/Card";

export default function Page() {
  const { t } = useT("common");
  const [counter, setCounter] = useState(0);

  const handleIncrement = () => {
    setCounter((prevCounter) => Math.min(10, prevCounter + 1));
  };

  const handleDecrement = () => {
    setCounter((prevCounter) => Math.max(0, prevCounter - 1));
  };

  return (
    <>
      <main>
        <h1>{t("csr-demo.h1")}</h1>
        <p>{t("csr-demo.counter", { count: counter })}</p>
        <div className="space-x-2 my-2">
          <Button text="+" onClick={handleIncrement} />
          <Button text="-" onClick={handleDecrement} />
        </div>
        <Link href="/">
          <Button text={t("csr-demo.to-ssr-page")} variant="soft" />
        </Link>
        <div className="grid grid-cols-4 gap-10 max-w-[1440px] mx-auto my-4 px-8">
          <CountryCard countryName="日本" imgSrc="/img/Japan.jpg" href="/tw" />
          <TravelPlanCard
            title="東京賞櫻與經典美食全攻略六天五夜"
            imgSrc="/img/Japan.jpg"
            location="東京"
            author="艾莉莎莎"
            tags={["六日遊", "觀光景點", "自由行"]}
            isBookmarked={true}
            handleBookmarkClick={() => console.log("Bookmark clicked")}
            handleCardClick={() => console.log("Card clicked")}
          />

          <LandScapeCard
            title="東京鐵塔"
            imgSrc="/img/Japan.jpg"
            location="東京"
            score={4.5}
            evaluateCount={1200}
            tags={["六日遊", "觀光景點", "自由行"]}
            isBookmarked={false}
            handleBookmarkClick={() => console.log("Bookmark clicked")}
            handleAddScheduleClick={() => console.log("Add Schedule clicked")}
            handleCardClick={() => console.log("Card clicked")}
          />
          <MyTravelPlanCard
            title="東京鐵塔"
            imgSrc="/img/Japan.jpg"
            location="東京"
            days={4}
            handleEditClick={() => console.log("Edit clicked")}
            handleCardClick={() => console.log("Card clicked")}
          />
        </div>
        {/* TODO: check lng */}
        <AsideMenu lng="zh" />
      </main>
    </>
  );
}
