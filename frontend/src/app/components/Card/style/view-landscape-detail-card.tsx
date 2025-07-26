import React from "react";
import Image from "next/image";
import { Box, Flex, Grid, Heading, Text } from "@radix-ui/themes";
import {
  BookmarkIcon,
  BookmarkFilledIcon,
  StarFilledIcon,
  ReaderIcon,
  Link2Icon,
  TimerIcon,
} from "@radix-ui/react-icons";

import { ILandscapeDetailCardProps } from "../card.types";

import Button from "@/app/components/Button";
import Pinpoint from "@/app/assets/pinpoint.svg";
import Phone from "@/app/assets/phone.svg";

const ICONS = {
  description: <ReaderIcon width={16} height={16} className="mt-0.5" />,
  address: <Pinpoint width={16} height={16} className="mt-0.5" />,
  phone: <Phone width={16} height={16} className="mt-0.5" />,
  website: <Link2Icon width={16} height={16} className="mt-0.5" />,
  hours: <TimerIcon width={16} height={16} className="mt-0.5" />,
};

function LandscapeDetailItem({
  iconType,
  item,
}: {
  iconType: string;
  item: string | string[];
}) {
  return (
    <Grid as="div" align="start" gap="2" columns="auto 1fr">
      {ICONS[iconType as keyof typeof ICONS]}
      {Array.isArray(item) ? (
        <Box as="div">
          {item.map((text, index) => (
            <Text size="2" key={index} as="p">
              {text}
            </Text>
          ))}
        </Box>
      ) : (
        <Text size="2">{item}</Text>
      )}
    </Grid>
  );
}

export default function ViewLandscapeDetailCard({
  title,
  imgSrc,
  score,
  evaluateCount,
  isBookmarked,
  handleBookmarkClick,
  handleAddScheduleClick,
  details = {},
}: ILandscapeDetailCardProps) {
  return (
    <Grid as="div" gap="4" p="4">
      <Flex
        as="div"
        justify="between"
        pb="4"
        className="border-b border-gray-4"
      >
        <Flex as="div" direction="column">
          <Box className="mb-auto" as="div">
            <Heading size="4" as="h3">
              {title}
            </Heading>
            <Flex align="center" gapX="2">
              <StarFilledIcon width={18} height={18} color="orange" />
              <Text>
                {score} ({evaluateCount})
              </Text>
            </Flex>
          </Box>
          <Flex gap="2" as="div">
            <Button
              size="2"
              isMain={false}
              text="收藏"
              icon={
                isBookmarked ? (
                  <BookmarkFilledIcon
                    width={16}
                    height={16}
                    className="text-blue-9"
                  />
                ) : (
                  <BookmarkIcon width={16} height={16} />
                )
              }
              onClick={handleBookmarkClick}
            />
            <Button
              size="2"
              text="新增至行程"
              onClick={handleAddScheduleClick}
            />
          </Flex>
        </Flex>
        <Box
          width="180px"
          className="relative bg-gray-9 aspect-[16/9] rounded-sm overflow-hidden"
          as="div"
        >
          <Image
            src={imgSrc}
            alt={title}
            fill={true}
            className="w-full h-full object-cover"
          />
        </Box>
      </Flex>
      <Grid as="div" gap="2">
        {Object.entries(details).map(([key, value]) => {
          return <LandscapeDetailItem key={key} iconType={key} item={value} />;
        })}
      </Grid>
    </Grid>
  );
}

export { LandscapeDetailItem, ViewLandscapeDetailCard };
