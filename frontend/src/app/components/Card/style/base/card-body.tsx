import React from "react";
import type { ICardBodyProps } from "@/app/components/Card/card.types";
import Badge from "@/app/components/Badge";

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
          {tags.map((tag) => (
            <Badge key={tag} text={tag} />
          ))}
        </div>
      )}
    </div>
  );
}
