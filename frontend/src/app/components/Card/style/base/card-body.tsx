import React from "react";
import type { ICardBodyProps } from "./../../card.types";
import Badge from "./../../../Badge";

export default function CardBody({
  title,
  tags = [],
  children,
}: ICardBodyProps) {
  return (
    <div>
      <h6 className="font-bold truncate">{title}</h6>
      <div className="py-1 space-x-2 divide-x flex items-center">
        {children}
      </div>
      {!!tags.length && (
        <div className="space-x-2">
          {tags.map((tag, index) => (
            <Badge key={index} text={tag} />
          ))}
        </div>
      )}
    </div>
  );
}
