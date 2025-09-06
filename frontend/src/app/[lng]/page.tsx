import Link from "next/link";
import { getT, generateI18NTitle } from "@/app/i18n";
import { LayoutParams } from "@/app/lib/types";

export async function generateMetadata(props: LayoutParams) {
  return generateI18NTitle(props, "common", "ssr-demo.title");
}

export default async function Page({ params }: LayoutParams) {
  const { lng } = await params;
  const { t } = await getT(lng, "common");
  return (
    <>
      <main>
        <h1>{t("ssr-demo.h1")}</h1>
        <Link href="/csr-demo">
          <button type="button">{t("ssr-demo.to-csr-page")}</button>
        </Link>
      </main>
    </>
  );
}
