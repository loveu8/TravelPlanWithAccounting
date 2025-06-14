import { languages } from "@/app/i18n/settings";
import { getT } from "@/app/i18n";

export async function generateStaticParams() {
  return languages.map((lng) => ({ lng }));
}

type LayoutParams = { params: { lng: string } | Promise<{ lng: string }> };

export async function generateMetadata({ params }: LayoutParams) {
  const { lng } = await params;
  const { t } = await getT(lng, "common");
  return {
    title: t("ssr-demo.title"),
  };
}

export default async function Layout({
  children,
}: {
  children: React.ReactNode;
}) {
  return children;
}
