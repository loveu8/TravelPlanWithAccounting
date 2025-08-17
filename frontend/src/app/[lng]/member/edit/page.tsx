import { Container, Heading } from "@radix-ui/themes";
import { getT } from "@/app/i18n";
import EditForm from "./EditForm";

type LayoutParams = { params: { lng: string } | Promise<{ lng: string }> };

export async function generateMetadata({ params }: LayoutParams) {
  const { lng } = await params;
  const { t } = await getT(lng, "member");
  return { title: t("edit.title") };
}

export default async function Page({ params }: LayoutParams) {
  const { lng } = await params;
  const { t: memberTranslate } = await getT(lng, "member");

  return (
    <main>
      <Container size="3" p="4">
        <Heading as="h1">{memberTranslate("edit.title")}</Heading>
      </Container>
      <EditForm />
    </main>
  );
}
