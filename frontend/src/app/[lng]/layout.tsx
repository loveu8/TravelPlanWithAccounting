import { languages } from "@/app/i18n/settings";
import { generateI18NTitle } from "@/app/i18n";
import { LayoutParams } from "@/app/lib/types";
import Header from "@/app/components/Header";

export async function generateStaticParams() {
  return languages.map((lng) => ({ lng }));
}

export async function generateMetadata(props: LayoutParams) {
  return generateI18NTitle(props, "common", "ssr-demo.title");
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
