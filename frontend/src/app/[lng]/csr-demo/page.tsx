"use client";

import { useState } from "react";
import Link from "next/link";
import { useT } from "@/app/i18n/client";

import Button from "@/app/components/Button";

export default function Page() {
  const { t } = useT("common");
  const [counter, setCounter] = useState(0);

  const handleIncrement = () => {
    setCounter((prevCounter) => Math.min(10, prevCounter + 1));
  };

  const handleDecrement = () => {
    setCounter((prevCounter) => Math.max(0, prevCounter - 1));
  };

  return (
    <>
      <main>
        <h1>{t("csr-demo.h1")}</h1>
        <p>{t("csr-demo.counter", { count: counter })}</p>
        <div className="space-x-2 my-2">
          <Button text="+" onClick={handleIncrement} />
          <Button text="-" onClick={handleDecrement} />
        </div>
        <Link href="/">
          <Button text={t("csr-demo.to-ssr-page")} variant="soft" />
        </Link>
      </main>
    </>
  );
}
