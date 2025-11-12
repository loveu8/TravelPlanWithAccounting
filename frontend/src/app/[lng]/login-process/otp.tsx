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
  title,
  buttonText,
  ref,
  email,
  onSubmitOtp,
  onResend,
  loading,
  serverError,
  onClearServerError,
}: {
  title?: string;
  buttonText?: string;
  ref?: React.RefObject<OTPImperativeHandle | null>;
  email?: string;
  onSubmitOtp: (otp: string) => Promise<boolean | void>;
  onResend: () => Promise<void>;
  loading?: boolean;
  serverError?: string;
  onClearServerError?: () => void;
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
      const res = await onSubmitOtp(otp);
      return res ?? true;
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
      onClearServerError?.();
      if (!email) return;
      await onResend();
      startCountdown();
    } catch (err) {
      if (!err || !(err instanceof Error)) {
        console.error("Unexpected error:", err);
        return;
      }
      console.error("Error resending OTP:", err.message);
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
            <DialogHeader title={title || t("login.otp")}></DialogHeader>
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
                error={serverError || error}
                buttonText={
                  serverError === ERROR_EXPIRED ||
                  error === ERROR_EXPIRED ||
                  countdown === 0
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
              <Button
                type="submit"
                text={buttonText || t("login.button-login")}
                disabled={!!loading}
              ></Button>
            </DialogFooter>
          </Grid>
        </form>
      </DialogContent>
    </DialogRoot>
  );
}
