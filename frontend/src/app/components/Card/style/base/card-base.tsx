import React from "react";
import type { ICardBaseProps } from "@/app/components/Card/card.types";
import CardHead from "@/app/components/Card/style/base/card-head";
import CardBody from "@/app/components/Card/style/base/card-body";

export default function CardBase({
  title,
  imgSrc,
  tags,
  handleCardClick,
  children,
}: ICardBaseProps) {
  return (
    <div className="space-y-2 cursor-pointer" onClick={handleCardClick}>
      <CardHead imgSrc={imgSrc} title={title} />
      <CardBody title={title} tags={tags}>
        {children}
      </CardBody>
    </div>
  );
}
