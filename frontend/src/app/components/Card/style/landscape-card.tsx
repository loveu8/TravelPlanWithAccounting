"use client";
import type { ILandscapeCardProps } from "./../card.types";
import Badge from "../../Badge";
import Button from "../../Button";
import CardBase from "./base/card-base";
import {
  BookmarkIcon,
  BookmarkFilledIcon,
  PlusCircledIcon,
  StarFilledIcon,
} from "@radix-ui/react-icons";
import PinpointIcon from "@/app/assets/pinpoint.svg";

export default function LandScapeCard(props: ILandscapeCardProps) {
  const {
    location,
    score,
    evaluateCount,
    isBookmarked = false,
    handleBookmarkClick,
    handleAddScheduleClick,
  } = props;
  return (
    <div className="relative group">
      <div className="absolute top-3 right-3 z-[1] hidden group-hover:block">
        <div className="grid gap-2">
          <Button
            size="2"
            icon={<PlusCircledIcon width={18} height={18} />}
            handleClick={handleAddScheduleClick}
          />
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
      </div>
      <CardBase {...props}>
        <Badge
          text={location}
          icon={<PinpointIcon />}
          bgColor="transparent"
          className="pl-0 py-0 rounded-none"
        />
        <Badge
          text={`${score.toFixed(1)} (${evaluateCount})`}
          icon={<StarFilledIcon width={18} height={18} color="orange" />}
          bgColor="transparent"
          className="pl-0 py-0 rounded-none"
        />
      </CardBase>
    </div>
  );
}
