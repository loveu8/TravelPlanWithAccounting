"use client";

import { useState } from "react";
import { GlobeIcon } from "@radix-ui/react-icons";
import { useRouter, usePathname } from "next/navigation";
import { useTranslation } from "react-i18next";
import i18n from "@/app/i18n/i18next";

export default function LanguageSelector({ lng }: { lng: string }) {
  const { t } = useTranslation("common");
  const [isOpen, setIsOpen] = useState(false);
  const router = useRouter();
  const pathname = usePathname();

  const languageOptions = [
    { value: "zh", label: t("lang.zh") },
    { value: "en", label: t("lang.en") },
  ];

  const handleLanguageChange = (value: string) => {
    if (value === lng) {
      setIsOpen(false);
      return;
    }

    i18n.changeLanguage(value);
    const pathWithoutLang = pathname.replace(new RegExp(`^/${lng}`), "");
    const { search, hash } = window.location;
    router.push(`/${value}${pathWithoutLang}${search}${hash}`);
    setIsOpen(false);
  };

  return (
    <div className="relative">
      <button
        className="cursor-pointer rounded-full p-2 hover:bg-gray-100 transition-colors flex items-center"
        onClick={() => setIsOpen((prev) => !prev)}
      >
        <GlobeIcon className="w-6 h-6" />
      </button>

      {isOpen && (
        <div className="absolute right-0 top-full p-2 mt-2 bg-white border border-gray-200 rounded-lg shadow-[0_25px_60px_-12px_rgba(0,0,0,0.15)] z-50 min-w-[150px]">
          {languageOptions.map((lang) => (
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
