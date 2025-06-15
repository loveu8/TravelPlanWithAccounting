import React from "react";
import type { ICardBaseProps } from "./../../card.types";
import CardHead from "./card-head";
import CardBody from "./card-body";

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
