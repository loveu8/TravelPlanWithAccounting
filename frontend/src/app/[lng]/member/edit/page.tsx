import { Container, Heading } from "@radix-ui/themes";
import { getT, generateI18NTitle } from "@/app/i18n";
import EditForm from "./EditForm";
import { LayoutParams } from "@/app/lib/types";

export async function generateMetadata(props: LayoutParams) {
  return generateI18NTitle(props, "member", "edit.title");
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
