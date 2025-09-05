import { languages } from "@/app/i18n/settings";
import { generateI18NTitle } from "@/app/i18n";
import { LayoutParams } from "@/app/lib/types";

export async function generateStaticParams() {
  return languages.map((lng) => ({ lng }));
}

export async function generateMetadata(props: LayoutParams) {
  return generateI18NTitle(props, "common", "ssr-demo.title");
}

export default async function Layout({
  children,
}: {
  children: React.ReactNode;
}) {
  return children;
}
