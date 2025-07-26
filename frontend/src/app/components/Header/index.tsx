"use client";
// TODO: 串接 API

import { useState } from "react";
import { useTranslation } from "react-i18next";
import { GlobeIcon } from "@radix-ui/react-icons";
import Link from "next/link";
import Image from "next/image";
import { useT } from "@/app/i18n/client";
import LoginSignup from "@/app/[lng]/login-process";
import Button from "@/app/components/Button";

function Navigation() {
  const { t } = useT("common");

  return (
    <nav className="flex gap-13 items-center">
      <Link href="/">{t("header.my-travel-plan")}</Link>
      <Link href="/">{t("header.explore")}</Link>
    </nav>
  );
}

function LanguageSelector() {
  const { i18n } = useTranslation();
  const { t } = useT("common");
  const [isOpen, setIsOpen] = useState(false);

  const languages = [
    { value: "zh", label: t("lang.zh") },
    { value: "en", label: t("lang.en") },
  ];

  const handleLanguageChange = (value: string) => {
    i18n.changeLanguage(value);
    setIsOpen(false);
  };

  return (
    <div className="relative">
      <button
        className="cursor-pointer rounded-full p-2 hover:bg-gray-100 transition-colors flex items-center"
        onClick={() => setIsOpen(!isOpen)}
      >
        <GlobeIcon className="w-6 h-6" />
      </button>

      {isOpen && (
        <div className="absolute right-0 top-full p-2 mt-2 bg-white border border-gray-200 rounded-lg shadow-[0_25px_60px_-12px_rgba(0,0,0,0.15)] z-50 min-w-[150px]">
          {languages.map((lang) => (
            <button
              key={lang.value}
              className="w-full cursor-pointer px-3 py-[6px] text-left hover:bg-gray-50 transition-colors rounded"
              onClick={() => handleLanguageChange(lang.value)}
            >
              {lang.label}
            </button>
          ))}
        </div>
      )}
    </div>
  );
}

export default function Header() {
  const { t } = useT("common");

  return (
    <header className="mx-auto px-36">
      <div className="flex justify-between items-center">
        <div>
          <Image src="/img/logo.png" alt="logo" width={110} height={44} />
        </div>
        <div className="flex gap-13 items-center py-[19px]">
          <Navigation />
          <LoginSignup>
            <Button
              className="cursor-pointer bg-transparent text-black font-normal"
              text={t("header.login")}
            />
          </LoginSignup>
          <LanguageSelector />
        </div>
      </div>
    </header>
  );
}
