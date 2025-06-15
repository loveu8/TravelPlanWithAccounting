import React from "react";
import Link from "next/link";
import Image from "next/image";
import type { ICountryCardProps } from "./../card.types";

export default function CountryCard({
  countryName,
  imgSrc,
  href,
}: ICountryCardProps) {
  return (
    <Link href={href}>
      <div className="relative overflow-hidden rounded-lg aspect-[4/3] p-4 bg-black place-content-end">
        <Image
          src={imgSrc}
          alt={countryName}
          fill={true}
          className="absolute z-0 inset-0 object-cover mask-linear-0 mask-linear-from-transparent mask-linear-to-fuchsia-500 mask-linear-to-70%"
        />
        <h6 className="text-white font-bold text-2xl relative z-[1]">
          {countryName}
        </h6>
      </div>
    </Link>
  );
}
