"use client";

import { useImperativeHandle, useRef } from "react";
import { Grid } from "@radix-ui/themes";
import {
  DialogRoot,
  DialogContent,
  DialogHeader,
  DialogBody,
  DialogFooter,
} from "@/app/components/Dialog";
import Button from "@/app/components/Button";
import TextField from "@/app/components/TextField";
import { useT } from "@/app/i18n/client";
import useDialogWithForm from "./useDialogWithForm";
import ErrorProcess from "./error-process";
import type { PreAuthFlowResponse } from "@/app/lib/types";
import { useAuth } from "@/app/lib/useAuth";

export type LoginImperativeHandle = {
  openDialogWithPromise: () => Promise<PreAuthFlowResponse | undefined>;
};

export default function LoginDialog({
  ref,
  openSignUp,
  onPreAuthSuccess,
}: {
  ref?: React.RefObject<LoginImperativeHandle | null>;
  openSignUp: (e: React.MouseEvent | React.TouchEvent) => void;
  onPreAuthSuccess: (data: PreAuthFlowResponse) => void;
}) {
  const { t } = useT("common");
  const ERROR_NOT_FOUND = t("login.error-not-found");
  const inputRef = useRef<HTMLInputElement | null>(null);
  const { preAuthFlow, preAuthorizing } = useAuth();

  const { open, error, openDialogWithPromise, handleSubmit, handleOpenChange } =
    useDialogWithForm<PreAuthFlowResponse>({
      onSubmit: async (e: React.FormEvent) => {
        e.preventDefault();
        const formData = new FormData(e.currentTarget as HTMLFormElement);
        const email = formData.get("email") as string;
        const emailField = inputRef.current;
        if (!emailField) {
          throw new Error("Email input field not found");
        }
        emailField.setCustomValidity("");
        if (!email) {
          emailField.setCustomValidity(
            t("validation.required", {
              label: t("login.placeholder"),
            }),
          );
          emailField.reportValidity();
          throw new Error(
            t("validation.required", { label: t("login.placeholder") }),
          );
        }
        if (!emailField.validity.valid) {
          emailField.setCustomValidity(
            t("validation.pattern", {
              label: t("login.placeholder"),
            }),
          );
          emailField.reportValidity();
          throw new Error(
            t("validation.pattern", { label: t("login.placeholder") }),
          );
        }

        const result = await preAuthFlow(email);
        onPreAuthSuccess(result);
        return result;
      },
    });

  useImperativeHandle(ref, () => ({
    openDialogWithPromise,
  }));

  const onClickSignUp = (e: React.MouseEvent | React.TouchEvent) => {
    openSignUp(e);
    handleOpenChange(false);
  };

  return (
    <DialogRoot open={open} onOpenChange={handleOpenChange}>
      <DialogContent maxWidth="450px" size="4">
        <form onSubmit={open ? handleSubmit : (e) => e.preventDefault()}>
          <Grid columns="1" gap="5">
            <DialogHeader title={t("login.title")}></DialogHeader>
            <DialogBody>
              <TextField
                ref={inputRef}
                name="email"
                id="email"
                placeholder={t("login.placeholder")}
                size="2"
                type="email"
                pattern="[^@\s]+@[^@\s]+\.[^@\s]+"
                required
                autoFocus
                disabled={preAuthorizing}
              />
              <ErrorProcess
                error={error || ""}
                buttonText={
                  error === ERROR_NOT_FOUND ? t("login.sign-up") : undefined
                }
                onClick={onClickSignUp}
              />
            </DialogBody>
            <DialogFooter justify="center" withCloseBtn>
              <Button
                type="submit"
                text={t("login.button-login")}
                disabled={preAuthorizing}
              ></Button>
            </DialogFooter>
          </Grid>
        </form>
      </DialogContent>
    </DialogRoot>
  );
}
