"use client";
// 3rd party libraries
import { useState, useRef } from "react";
import { useParams } from "next/navigation";
import { useQuery, useMutation } from "@tanstack/react-query";
import { Section, Heading, Text } from "@radix-ui/themes";
import { api } from "@/app/lib/http";
// methods and hooks
import { useT } from "@/app/i18n/client";
// components
import Button from "@/app/components/Button";
import {
  CountryCard,
  TravelPlanCard,
  LandScapeCard,
} from "@/app/components/Card";
import CardSection from "./card-section";
import {
  LandscapeDetailModal,
  ScheduleModal,
  ScheduleAddModal,
} from "@/app/components/Modal";

// types
import type {
  ILandscapeDetailImperative,
  IScheduleImperative,
  IScheduleAddImperative,
} from "@/app/components/Modal";
import type { ScheduleForm } from "@/app/components/Modal/ScheduleModal/schema";

const countries = [
  { countryName: "日本", imgSrc: "/img/Japan.jpg", href: "/tw" },
  { countryName: "日本", imgSrc: "/img/Japan.jpg", href: "/tw" },
  { countryName: "日本", imgSrc: "/img/Japan.jpg", href: "/tw" },
  { countryName: "日本", imgSrc: "/img/Japan.jpg", href: "/tw" },
];

const landscapes = [
  {
    poiId: "1",
    title: "東京鐵塔",
    imgSrc: "/img/Japan.jpg",
    tags: ["文化活動", "觀光景點"],
    country: "東京",
    score: 5.5,
    evaluateCount: 1300,
    isBookmarked: false,
  },
  {
    poiId: "2",
    title: "淺草寺",
    imgSrc: "/img/Japan.jpg",
    tags: ["文化活動"],
    country: "京都",
    score: 5.5,
    evaluateCount: 1300,
    isBookmarked: false,
  },
];

export default function Home() {
  const { lng } = useParams();
  const { t: homeTranslate } = useT("home");
  const viewLandscapeDetailModalRef = useRef<ILandscapeDetailImperative>(null);
  const addScheduleModalRef = useRef<IScheduleAddImperative>(null);
  const createScheduleModalRef = useRef<IScheduleImperative>(null);
  const [title, setTitle] = useState<string>("");

  const { data: popularTravels } = useQuery({
    queryKey: ["popular-travels"],
    queryFn: async () => {
      const res = await api.get("/api/travels/popular");
      return res.data;
    },
  });

  const { data: recommendLandscapes } = useQuery({
    queryKey: ["recommend-landscapes"],
    queryFn: async () => {
      const res = await api.post("/api/recommands", { country: "ALL" });
      return res.data;
    },
  });

  console.log(recommendLandscapes);

  const { mutateAsync: createScheduleMutateAsync } = useMutation({
    mutationFn: async (data: ScheduleForm) => {
      const res = await api.post("/api/travels/upsert-travel-main", {
        ...data,
      });
      console.log(res.data);
      return res.data;
    },
    onSuccess: async () => {
      handleScheduleClick(false);
      createScheduleModalRef.current?.close();
    },
  });

  const handleCardClick = (id: string, title: string) => {
    viewLandscapeDetailModalRef.current?.toggle(id);
    setTitle(title);
  };

  const toggleViewAndAddModal = (id: string) => {
    viewLandscapeDetailModalRef.current?.toggle(id);
    addScheduleModalRef.current?.toggle(id);
  };

  const handleBookmarkClick = (id: string) => {
    if (!id) {
      console.error("No ID provided for bookmarking.");
      return;
    }
    // TODO: 串接API來處理收藏
    console.log(`Bookmarking item with ID: ${id}`);
  };

  const handleScheduleClick = (open: boolean) => {
    createScheduleModalRef.current?.toggle();
    if (open) {
      addScheduleModalRef.current?.close();
      return;
    }
    addScheduleModalRef.current?.open();
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
        title={homeTranslate("explore-countries")}
        data={countries}
        CardComponent={CountryCard}
      />
      {/* TODO:修改正確url路徑 */}
      <CardSection
        title={homeTranslate("popular-travel-plans")}
        data={popularTravels || []}
        CardComponent={TravelPlanCard}
        buttonText={`${homeTranslate("view-more")}${homeTranslate("popular-travel-plans")}`}
        viewMoreUrl={`/${lng}/popular-list`}
        handleCardClick={handleCardClick}
        handleBookmarkClick={handleBookmarkClick}
      />
      {/* TODO:修改正確url路徑 */}
      <CardSection
        title={homeTranslate("recommend-landscapes")}
        data={landscapes}
        CardComponent={LandScapeCard}
        buttonText={`${homeTranslate("view-more")}${homeTranslate("recommend-landscapes")}`}
        viewMoreUrl={`/${lng}/landscapes-list`}
        handleCardClick={handleCardClick}
        handleBookmarkClick={handleBookmarkClick}
      />
      <LandscapeDetailModal
        ref={viewLandscapeDetailModalRef}
        handleAddScheduleClick={toggleViewAndAddModal}
        handleBookmarkClick={handleBookmarkClick}
      />
      <ScheduleAddModal
        ref={addScheduleModalRef}
        location={title}
        viewDetailClick={toggleViewAndAddModal}
        handleCreateScheduleClick={handleScheduleClick}
      />
      <ScheduleModal
        ref={createScheduleModalRef}
        handleScheduleClick={handleScheduleClick}
        onSubmit={createScheduleMutateAsync}
      />
    </>
  );
}
