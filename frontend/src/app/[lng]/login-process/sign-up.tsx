"use client";

import { useImperativeHandle } from "react";
import { useParams } from "next/navigation";
import { Grid, Flex } from "@radix-ui/themes";
import {
  DialogRoot,
  DialogContent,
  DialogHeader,
  DialogBody,
  DialogFooter,
} from "@/app/components/Dialog";
import Button from "@/app/components/Button";
import TextField from "@/app/components/TextField";
import DatePicker from "@/app/components/DatePicker";
import { useT } from "@/app/i18n/client";
import useDialogWithForm from "./useDialogWithForm";
import ErrorProcess from "./error-process";

export type SignUpImperativeHandle = {
  openDialogWithPromise: () => Promise<boolean | undefined>;
};

const nameFields = [
  {
    label: "account.surname",
    name: "surname",
    id: "surname",
  },
  {
    label: "account.given-name",
    name: "givenName",
    id: "givenName",
  },
];

export default function SignUpDialog({
  ref,
}: {
  ref?: React.RefObject<SignUpImperativeHandle | null>;
}) {
  const { lng } = useParams();
  const { t } = useT("common");
  const { open, error, openDialogWithPromise, handleSubmit, handleOpenChange } =
    useDialogWithForm({
      onSubmit: async (e: React.FormEvent) => {
        e.preventDefault();
        const formData = new FormData(e.currentTarget as HTMLFormElement);
        /**
         * @todo 處理 API 存資料和驗證
         */
        console.log(
          "Form data submitted:",
          Object.fromEntries(formData.entries()),
        );
        return true;
      },
    });

  useImperativeHandle(ref, () => ({
    openDialogWithPromise,
  }));

  const nameFieldsByLng = lng === "zh" ? nameFields : nameFields.reverse();

  return (
    <DialogRoot open={open} onOpenChange={handleOpenChange}>
      <DialogContent maxWidth="450px" size="4">
        <form onSubmit={open ? handleSubmit : (e) => e.preventDefault()}>
          <Grid columns="1" gap="5">
            <DialogHeader title={t("login.title")}></DialogHeader>
            <DialogBody>
              <Grid columns="1" gap="2">
                <Flex justify="between" gap="2">
                  {nameFieldsByLng.map((field) => (
                    <TextField
                      key={field.id}
                      label={t(field.label)}
                      name={field.name}
                      id={field.id}
                      size="2"
                      type="text"
                      required
                    />
                  ))}
                </Flex>
                <TextField
                  label={t("account.nickname")}
                  name="nickname"
                  id="nickname"
                  size="2"
                  type="text"
                  required
                />
                <DatePicker
                  label={t("account.birthday")}
                  name="birthday"
                  id="birthday"
                  localeType={lng === "zh" ? "zh-TW" : "en-US"}
                  calendarOptions={{
                    captionLayout: "dropdown",
                  }}
                  required
                />
                <TextField
                  label={t("account.email")}
                  name="email"
                  id="email"
                  size="2"
                  type="email"
                  required
                />
              </Grid>
              <ErrorProcess error={error} />
            </DialogBody>
            <DialogFooter justify="center" withCloseBtn>
              <Button type="button" text={t("login.button-sign-up")}></Button>
            </DialogFooter>
          </Grid>
        </form>
      </DialogContent>
    </DialogRoot>
  );
}
