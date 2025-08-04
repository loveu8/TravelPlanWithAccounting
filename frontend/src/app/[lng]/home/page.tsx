"use client";
import { use, useState } from "react";
import { Section, Heading, Text } from "@radix-ui/themes";
import Button from "@/app/components/Button";
import {
  CountryCard,
  TravelPlanCard,
  LandScapeCard,
} from "@/app/components/Card";
import CardSection from "./card-section";
import ViewLandscapeDetailModal from "./view-landscape-detail-modal";
import AddScheduleModal from "./add-schedule-modal";

import { useT } from "@/app/i18n/client";

const countries = [
  { countryName: "日本", imgSrc: "/img/Japan.jpg", href: "/tw" },
  { countryName: "日本", imgSrc: "/img/Japan.jpg", href: "/tw" },
  { countryName: "日本", imgSrc: "/img/Japan.jpg", href: "/tw" },
  { countryName: "日本", imgSrc: "/img/Japan.jpg", href: "/tw" },
];

const travelPlans = [
  {
    title: "日本",
    imgSrc: "/img/Japan.jpg",
    tags: ["六日遊"],
    location: "東京",
    author: "安安莎莉",
    isBookmarked: true,
    handleBookmarkClick: () => {},
    handleCardClick: () => {},
  },
  // ...其他行程
];

const landscapes = [
  {
    title: "日本",
    imgSrc: "/img/Japan.jpg",
    tags: ["六日遊"],
    location: "東京",
    score: 5.5,
    evaluateCount: 1300,
    isBookmarked: false,
    handleBookmarkClick: () => {},
    handleAddScheduleClick: () => {},
    handleCardClick: () => {},
  },
  // ...其他景點
];

export default function Home({ params }: { params: Promise<{ lng: string }> }) {
  const { lng } = use(params);
  const { t } = useT("common");

  const [isViewDetailModalOpen, setIsViewDetailModalOpen] = useState(true);
  const [isAddScheduleModalOpen, setIsAddScheduleModalOpen] = useState(false);

  const handleAddScheduleClick = () => {
    // TODO: 取得個人所有的行程表API
    toggleDetailAndScheduleModals();
  };

  const toggleDetailAndScheduleModals = () => {
    setIsViewDetailModalOpen((pre) => !pre);
    setIsAddScheduleModalOpen((pre) => !pre);
  };

  return (
    <>
      <Section
        minHeight="240px"
        size="2"
        className="place-content-center text-center space-y-4 bg-blue-3"
      >
        <Heading size="8" as="h2">
          Travel Mate – Making Every Trip Better
        </Heading>
        <div className="space-y-1">
          <Text size="3" color="gray" as="p">
            Ready to Travel?
          </Text>
          <Button text="Start Planning Now" />
        </div>
      </Section>
      <CardSection
        title={t("home.explore-countries")}
        data={countries}
        CardComponent={CountryCard}
      />
      {/* TODO:修改正確url路徑 */}
      <CardSection
        title={t("home.popular-travel-plans")}
        data={travelPlans}
        CardComponent={TravelPlanCard}
        buttonText={`${t("home.view-more")}${t("home.popular-travel-plans")}`}
        viewMoreUrl={`/${lng}/popular-list`}
      />

      {/* TODO:修改正確url路徑 */}
      <CardSection
        title={t("home.recommend-landscapes")}
        data={landscapes}
        CardComponent={LandScapeCard}
        buttonText={`${t("home.view-more")}${t("home.recommend-landscapes")}`}
        viewMoreUrl={`/${lng}/landscapes-list`}
      />
      <AddScheduleModal
        open={isAddScheduleModalOpen}
        onOpenChange={setIsAddScheduleModalOpen}
      />
      <ViewLandscapeDetailModal
        open={isViewDetailModalOpen}
        onOpenChange={setIsViewDetailModalOpen}
        title="東京迪士尼"
        imgSrc="/img/Japan.jpg"
        iframeSrc="https://www.google.com/maps?q=東京迪士尼&output=embed"
        score={4.5}
        evaluateCount={100}
        isBookmarked={true}
        handleBookmarkClick={() => {}}
        handleAddScheduleClick={handleAddScheduleClick}
        details={{
          description:
            "著名主題公園的東京分支，以遊樂設施、現場表演和古裝人物聞名",
          address: "1-1 Maihama, Urayasu, Chiba 279-0031日本",
          phone: "+81453305211",
          website: "https://www.tokyodisneyresort.jp/tdl/",
          hours: [
            "星期日 08:00–22:00",
            "星期六 08:00–22:00",
            "星期六 08:00–22:00",
            "星期六 08:00–22:00",
            "星期六 08:00–22:00",
          ],
        }}
      />
    </>
  );
}
