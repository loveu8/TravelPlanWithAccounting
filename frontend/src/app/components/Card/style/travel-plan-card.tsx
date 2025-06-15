"use client";
import type { ITravelPlanCardProps } from "./../card.types";
import Badge from "../../Badge";
import Button from "../../Button";
import CardBase from "./base/card-base";
import {
  AvatarIcon,
  BookmarkIcon,
  BookmarkFilledIcon,
} from "@radix-ui/react-icons";

import PinpointIcon from "@/app/assets/pinpoint.svg";

export default function TravelPlanCard(props: ITravelPlanCardProps) {
  const { location, author, isBookmarked = false, handleBookmarkClick } = props;
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
          handleClick={handleBookmarkClick}
        />
      </div>
      <CardBase {...props}>
        <Badge
          text={location}
          icon={<PinpointIcon />}
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
