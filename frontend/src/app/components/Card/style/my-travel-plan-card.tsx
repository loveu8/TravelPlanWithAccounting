"use client";
import type { IMyTravelPlanCardProps } from "@/app/components/Card/card.types";
import Badge from "@/app/components/Badge";
import Button from "@/app/components/Button";
import CardBase from "@/app/components/Card/style/base/card-base";
import PinpointIcon from "@/app/assets/pinpoint.svg";

import { Pencil2Icon } from "@radix-ui/react-icons";

export default function MyTravelPlanCard(props: IMyTravelPlanCardProps) {
  const { location, days, handleEditClick } = props;
  return (
    <div className="relative group">
      <div className="absolute top-3 right-3 z-[1] hidden group-hover:block">
        <Button
          size="2"
          text="編輯"
          icon={<Pencil2Icon width={16} height={16} />}
          handleClick={handleEditClick}
        />
      </div>
      <CardBase {...props}>
        <Badge
          text={location}
          icon={<PinpointIcon className="text-blue-9" />}
          bgColor="transparent"
          className="pl-0 py-0 rounded-none"
        />
        <Badge
          text={`${days}天`}
          bgColor="transparent"
          className="pl-0 py-0 rounded-none text-base"
        />
      </CardBase>
    </div>
  );
}
