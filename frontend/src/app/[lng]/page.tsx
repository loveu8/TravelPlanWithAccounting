import { getT } from "@/app/i18n";
import Link from "next/link";

type LayoutParams = { params: { lng: string } | Promise<{ lng: string }> };

export async function generateMetadata({ params }: LayoutParams) {
  const { lng } = await params;
  const { t } = await getT(lng, "common");
  return { title: t("ssr-demo.title") };
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
