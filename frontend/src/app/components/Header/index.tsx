import Link from "next/link";
import Image from "next/image";
import { getT } from "@/app/i18n";
import LoginSignup from "@/app/[lng]/login-process";
import Button from "@/app/components/Button";
import LanguageSelector from "@/app/components/Header/LanguageSelector";

export default async function Header({ lng }: { lng: string }) {
  const { t } = await getT(lng, "common");

  return (
    <header className="mx-auto px-36 bg-white">
      <div className="flex justify-between items-center">
        <div>
          <Image src="/img/logo.png" alt="logo" width={110} height={44} />
        </div>
        <div className="flex gap-13 items-center py-[19px] text-black">
          <nav className="flex gap-13 items-center">
            <Link href={`/${lng}/my-travel-plan`}>
              {t("header.my-travel-plan")}
            </Link>
            <Link href={`/${lng}/explore`}>{t("header.explore")}</Link>
          </nav>
          <LoginSignup asChild>
            <Button
              className="cursor-pointer bg-transparent text-black font-normal"
              text={t("header.login")}
            />
          </LoginSignup>
          <LanguageSelector lng={lng} />
        </div>
      </div>
    </header>
  );
}
