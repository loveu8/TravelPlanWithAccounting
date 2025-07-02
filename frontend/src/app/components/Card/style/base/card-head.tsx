import React from "react";
import Image from "next/image";
import type { ICardHeadProps } from "@/app/components/Card/card.types";

import Styles from "@/components/Card/style/base/card-head.module.css";

export default function CardHead({ imgSrc, title }: ICardHeadProps) {
  return (
    <div className={Styles["img-container"]}>
      <Image
        src={imgSrc}
        alt={title}
        fill={true}
        className="w-full h-full object-cover"
      />
    </div>
  );
}
