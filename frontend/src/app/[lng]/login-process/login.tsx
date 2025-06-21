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

export type LoginImperativeHandle = {
  openDialogWithPromise: () => Promise<boolean | undefined>;
};

export default function LoginDialog({
  ref,
  openSignUp,
}: {
  ref?: React.RefObject<LoginImperativeHandle | null>;
  openSignUp: (e: React.MouseEvent | React.TouchEvent) => void;
}) {
  const { t } = useT("common");
  const ERROR_NOT_FOUND = t("login.error-not-found");
  const inputRef = useRef<HTMLInputElement | null>(null);
  const { open, error, openDialogWithPromise, handleSubmit, handleOpenChange } =
    useDialogWithForm({
      onSubmit: async (e: React.FormEvent) => {
        e.preventDefault();
        const formData = new FormData(e.currentTarget as HTMLFormElement);
        const email = formData.get("email") as string;
        const emailField = inputRef.current;
        if (!emailField) {
          console.error("Email input field not found");
          return;
        }
        emailField.setCustomValidity("");
        if (!email) {
          // 用 setCustomValidity 可以在表單提交時顯示錯誤訊息
          emailField.setCustomValidity(
            t("validation.required", {
              label: t("login.placeholder"),
            }),
          );
          emailField.reportValidity();
          return;
        }
        if (!emailField.validity.valid) {
          emailField.setCustomValidity(
            t("validation.pattern", {
              label: t("login.placeholder"),
            }),
          );
          emailField.reportValidity();
          return;
        }

        /**
         * @todo 這裡應該是發送請求到後端進行登入驗證
         * 這裡模擬一個請求，實際情況應該是使用 fetch 或 axios 等庫來發送請求
         * 如果請求成功，返回 true
         * 如果請求失敗，根據錯誤類型返回不同的錯誤信息
         */
        if (email === "none@test.com") {
          // 模擬一個錯誤情況
          throw new Error(ERROR_NOT_FOUND);
        }
        return true;
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
              />
              <ErrorProcess
                error={error}
                buttonText={
                  error === ERROR_NOT_FOUND ? t("login.sign-up") : undefined
                }
                onClick={onClickSignUp}
              />
            </DialogBody>
            <DialogFooter justify="center" withCloseBtn>
              <Button type="submit" text={t("login.button-login")}></Button>
            </DialogFooter>
          </Grid>
        </form>
      </DialogContent>
    </DialogRoot>
  );
}
