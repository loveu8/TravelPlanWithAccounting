"use client";
import type { ITravelPlanCardProps } from "@/app/components/Card/card.types";
import Badge from "@/app/components/Badge";
import Button from "@/app/components/Button";
import CardBase from "@/app/components/Card/style/base/card-base";
import {
  AvatarIcon,
  BookmarkIcon,
  BookmarkFilledIcon,
} from "@radix-ui/react-icons";

import PinpointIcon from "@/app/assets/pinpoint.svg";

export default function TravelPlanCard(props: ITravelPlanCardProps) {
  const {
    id,
    title,
    location,
    author,
    isBookmarked = false,
    handleBookmarkClick,
    handleCardClick,
  } = props;
  return (
    <div className="relative group">
      <div className="absolute top-3 right-3 z-[1] hidden group-hover:block">
        <Button
          size="2"
          icon={
            isBookmarked ? (
              <BookmarkFilledIcon width={18} height={18} />
            ) : (
              <BookmarkIcon width={18} height={18} />
            )
          }
          handleClick={() => handleBookmarkClick(id)}
        />
      </div>
      <CardBase {...props} handleCardClick={() => handleCardClick(id, title)}>
        <Badge
          text={location}
          icon={<PinpointIcon className="text-blue-9" />}
          bgColor="transparent"
          className="pl-0 py-0 rounded-none"
        />
        <Badge
          text={author}
          icon={<AvatarIcon width={18} height={18} />}
          bgColor="transparent"
          className="pl-0 py-0 rounded-none"
        />
      </CardBase>
    </div>
  );
}
