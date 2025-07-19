"use client";
import { useRef } from "react";
import { Slot } from "radix-ui";
import { CLOSE_REJECT } from "./useDialogWithForm";
import LoginDialog, { LoginImperativeHandle } from "./login";
import OTPDialog, { OTPImperativeHandle } from "./otp";
import SignUpDialog, { SignUpImperativeHandle } from "./sign-up";

export default function LoginSignup({
  children,
  asChild = false,
}: {
  asChild?: boolean;
  children?: React.ReactNode;
}) {
  const Comp = asChild ? Slot.Root : "button";

  const loginDialogRef = useRef<LoginImperativeHandle>(null);
  const signUpDialogRef = useRef<SignUpImperativeHandle>(null);
  const otpDialogRef = useRef<OTPImperativeHandle>(null);

  const handleStartLogin = async () => {
    try {
      await loginDialogRef.current?.openDialogWithPromise();
      await otpDialogRef.current?.openDialogWithPromise();
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

  const handleOpenSignUp = async () => {
    try {
      await signUpDialogRef.current?.openDialogWithPromise();
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
      <Comp onClick={handleStartLogin}>{children}</Comp>
      <LoginDialog ref={loginDialogRef} openSignUp={handleOpenSignUp} />
      <SignUpDialog ref={signUpDialogRef} />
      <OTPDialog ref={otpDialogRef} />
    </>
  );
}
