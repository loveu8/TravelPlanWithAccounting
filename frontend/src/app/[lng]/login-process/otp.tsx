"use client";
import { useImperativeHandle, useRef } from "react";
import { unstable_OneTimePasswordField as OneTimePasswordField } from "radix-ui";
import { Grid, Text } from "@radix-ui/themes";
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

export type OTPImperativeHandle = {
  openDialogWithPromise: () => Promise<boolean | undefined>;
};

export default function OTPDialog({
  ref,
}: {
  ref?: React.RefObject<OTPImperativeHandle | null>;
}) {
  const { t } = useT("common");
  const ERROR_EXPIRED = t("login.error-otp-expired");
  const formRef = useRef<HTMLFormElement | null>(null);

  const {
    open,
    error,
    setError,
    openDialogWithPromise,
    handleSubmit,
    handleOpenChange,
    countdown,
    startCountdown,
  } = useDialogWithForm({
    timeout: 60,
    onSubmit: async (e: React.FormEvent) => {
      console.log("onSubmit called");
      e.preventDefault();
      const formData = new FormData(e.currentTarget as HTMLFormElement);
      const otp = formData.get("otp") as string;
      if (!otp || otp.length !== 6) {
        throw new Error(
          t("validation.required", {
            label: t("login.otp"),
          }),
        );
      }
      /**
       * @todo 處理 OTP 驗證邏輯
       * 這裡可以加入 API 呼叫來驗證 OTP
       * 如果 OTP 為 "000000"，則模擬 OTP 過期錯誤
       * 這只是示範，實際情況應根據後端邏輯來處理
       */
      if (otp === "000000") {
        throw new Error(ERROR_EXPIRED);
      }
      return true;
    },
  });

  useImperativeHandle(ref, () => ({
    openDialogWithPromise,
  }));

  const onClickResend = async (
    e:
      | React.MouseEvent<HTMLButtonElement>
      | React.TouchEvent<HTMLButtonElement>,
  ) => {
    e.preventDefault();
    e.stopPropagation();
    try {
      formRef.current?.reset();
      setError("");
      startCountdown();
      /**
       * @todo 處理重新發送 OTP 的邏輯
       * 這裡可以加入 API 呼叫來重新發送 OTP
       */
    } catch (error) {
      if (!error || !(error instanceof Error)) {
        console.error("Unexpected error:", error);
        return;
      }
      console.error("Error resending OTP:", error.message);
    }
  };
  return (
    <DialogRoot open={open} onOpenChange={handleOpenChange}>
      <DialogContent maxWidth="450px" size="4">
        <form
          ref={formRef}
          onSubmit={open ? handleSubmit : (e) => e.preventDefault()}
        >
          <Grid columns="1" gap="5">
            <DialogHeader title={t("login.otp")}></DialogHeader>
            <DialogBody>
              <OneTimePasswordField.Root name="otp" id="otp">
                <Grid columns="6" gap="2" width="250px" className="mx-auto">
                  {[...Array(6)].map((_, i) => (
                    <OneTimePasswordField.Input key={i} asChild>
                      <TextField />
                    </OneTimePasswordField.Input>
                  ))}
                </Grid>
                <OneTimePasswordField.HiddenInput />
              </OneTimePasswordField.Root>
              <ErrorProcess
                error={error}
                buttonText={
                  error === ERROR_EXPIRED || countdown === 0
                    ? t("login.otp-resend")
                    : undefined
                }
                onClick={onClickResend}
              />
              {!!countdown && countdown > 0 && error !== ERROR_EXPIRED && (
                <div className="text-center">
                  <Text asChild size="3" align="center" my="4">
                    <div className="inline-block bg-neutral-100 py-1 px-4 rounded-4xl">
                      {t("login.valid-time", {
                        time: countdown,
                      })}
                    </div>
                  </Text>
                </div>
              )}
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
