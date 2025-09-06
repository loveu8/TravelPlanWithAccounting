"use client";

import { Container, Grid, Flex, Button, Text } from "@radix-ui/themes";
import { useParams } from "next/navigation";
import { useT } from "@/app/i18n/client";
import TextField from "@/app/components/TextField";
import DatePicker from "@/app/components/DatePicker";
import ValidateEmail from "./ValidateEmail";
import { InfoCircledIcon } from "@radix-ui/react-icons";

export default function EditForm() {
  const { lng } = useParams<{ lng: string }>();
  const { t: commonTranslate } = useT("common");
  const { t: memberTranslate } = useT("member");

  return (
    <Container size="3" px="4" py="2">
      <Grid columns="1" gap="2">
        <TextField label={commonTranslate("account.surname")} size="2" />
        <TextField label={commonTranslate("account.given-name")} size="2" />
        <TextField label={commonTranslate("account.nickname")} size="2" />
        <DatePicker
          label={commonTranslate("account.birthday")}
          lng={lng}
          size="2"
        />
        <Flex>
          <TextField
            label={commonTranslate("account.email")}
            size="2"
            disabled
          />
          <ValidateEmail />
        </Flex>
        <Flex>
          <InfoCircledIcon />
          <Text ml="1" size="1">
            {memberTranslate("edit.email-edit-hint")}
          </Text>
        </Flex>
        <Flex>
          <Button
            size="2"
            my="2"
            variant="solid"
            className="cursor-pointer"
            onClick={() => console.log("Save button clicked")}
          >
            {memberTranslate("edit.button-save")}
          </Button>
        </Flex>
      </Grid>
    </Container>
  );
}
