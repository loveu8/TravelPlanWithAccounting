import { languages } from "@/app/i18n/settings";
import { getT } from "@/app/i18n";
import Header from "@/app/components/Header";

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
  params,
}: {
  children: React.ReactNode;
  params: { lng: string } | Promise<{ lng: string }>;
}) {
  const { lng } = await params;

  return (
    <>
      <Header lng={lng} />
      {children}
    </>
  );
}
