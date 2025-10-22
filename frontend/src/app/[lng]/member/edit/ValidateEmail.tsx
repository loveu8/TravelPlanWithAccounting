"use client";

import { useImperativeHandle, useRef } from "react";
import { Button as ThemeButton, Grid } from "@radix-ui/themes";
import {
  DialogRoot,
  DialogContent,
  DialogHeader,
  DialogBody,
  DialogFooter,
} from "@/app/components/Dialog";
import Button from "@/app/components/Button";
import TextField from "@/app/components/TextField";
import OTPDialog, { OTPImperativeHandle } from "@/app/[lng]/login-process/otp";
import useDialogWithForm, {
  CLOSE_REJECT,
} from "@/app/[lng]/login-process/useDialogWithForm";
import { useT } from "@/app/i18n/client";

export default function ValidateEmail() {
  const { t: memberTranslate } = useT("member");
  const otpDialogRef = useRef<OTPImperativeHandle>(null);
  const newEmailOtpDialogRef = useRef<OTPImperativeHandle>(null);
  const newEmailDialogRef = useRef<OTPImperativeHandle>(null);

  const handleEditEmail = async () => {
    try {
      await otpDialogRef.current?.openDialogWithPromise();
      await newEmailDialogRef.current?.openDialogWithPromise();
      await newEmailOtpDialogRef.current?.openDialogWithPromise();
    } catch (error) {
      if (error === CLOSE_REJECT) {
        return;
      }
      if (!error || !(error instanceof Error)) {
        console.error("Unexpected error:", error);
        return;
      }
    }
  };

  return (
    <>
      <ThemeButton
        size="2"
        my="1"
        ml="2"
        variant="surface"
        className="self-end cursor-pointer"
        onClick={handleEditEmail}
      >
        {memberTranslate("edit.email-edit")}
      </ThemeButton>
      <OTPDialog
        ref={otpDialogRef}
        title={memberTranslate("edit.original-email-otp")}
        buttonText={memberTranslate("edit.button-validate")}
        onResend={async () => {
          console.log("Resend OTP");
        }}
        onSubmitOtp={async (otp: string) => {
          console.log("Submitted OTP:", otp);
          return true;
        }}
      />
      <OTPDialog
        ref={newEmailOtpDialogRef}
        title={memberTranslate("edit.new-email-otp")}
        buttonText={memberTranslate("edit.button-validate")}
        onResend={async () => {
          console.log("Resend OTP for new email");
        }}
        onSubmitOtp={async (otp: string) => {
          console.log("Submitted OTP for new email:", otp);
          return true;
        }}
      />
      <NewEmailDialog ref={newEmailDialogRef} />
    </>
  );
}

function NewEmailDialog({
  ref,
}: {
  ref?: React.RefObject<OTPImperativeHandle | null>;
}) {
  const { t } = useT("member");
  const inputRef = useRef<HTMLInputElement | null>(null);
  const formRef = useRef<HTMLFormElement | null>(null);

  const { open, openDialogWithPromise, handleSubmit, handleOpenChange } =
    useDialogWithForm({
      onSubmit: async (e: React.FormEvent) => {
        console.log("onSubmit called");
        e.preventDefault();
        const formData = new FormData(e.currentTarget as HTMLFormElement);
        const email = formData.get("email") as string;

        console.log("Email submitted:", email);
        return true;
      },
    });

  useImperativeHandle(ref, () => ({
    openDialogWithPromise,
  }));
  return (
    <DialogRoot open={open} onOpenChange={handleOpenChange}>
      <DialogContent maxWidth="450px" size="4">
        <form
          ref={formRef}
          onSubmit={open ? handleSubmit : (e) => e.preventDefault()}
        >
          <Grid columns="1" gap="5">
            <DialogHeader title={t("edit.enter-email")}></DialogHeader>
            <DialogBody>
              <TextField
                ref={inputRef}
                name="email"
                id="email"
                size="2"
                type="email"
                pattern="[^@\s]+@[^@\s]+\.[^@\s]+"
                required
                autoFocus
              />
            </DialogBody>
            <DialogFooter justify="center" withCloseBtn>
              <Button type="submit" text={t("edit.email-edit")}></Button>
            </DialogFooter>
          </Grid>
        </form>
      </DialogContent>
    </DialogRoot>
  );
}
