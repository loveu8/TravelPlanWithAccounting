import { Container, Heading, Grid } from "@radix-ui/themes";
import { PersonIcon, ExitIcon } from "@radix-ui/react-icons";
import { getT } from "@/app/i18n";
import LinkButton from "./LinkButton";

type LayoutParams = { params: { lng: string } | Promise<{ lng: string }> };

export async function generateMetadata({ params }: LayoutParams) {
  const { lng } = await params;
  const { t } = await getT(lng, "member");
  return { title: t("page.title") };
}

export default async function Page({ params }: LayoutParams) {
  const { lng } = await params;
  const { t: memberTranslate } = await getT(lng, "member");
  const { t: commonTranslate } = await getT(lng, "common");
  return (
    <main>
      <Container size="3" p="4">
        <Heading as="h1">{memberTranslate("page.title")}</Heading>
      </Container>
      <Container size="3" px="4" py="2">
        <Grid columns="1" gap="2">
          <LinkButton
            href="/member/edit"
            icon={<PersonIcon width={20} height={20} />}
          >
            {memberTranslate("edit.title")}
          </LinkButton>
          <LinkButton
            href="/member/logout"
            icon={<ExitIcon width={20} height={20} />}
          >
            {commonTranslate("logout")}
          </LinkButton>
        </Grid>
      </Container>
    </main>
  );
}
