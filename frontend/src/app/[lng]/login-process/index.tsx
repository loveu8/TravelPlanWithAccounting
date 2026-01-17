"use client";
import { useRef, useState } from "react";
import { Slot } from "radix-ui";
import { CLOSE_REJECT } from "./useDialogWithForm";
import LoginDialog, { LoginImperativeHandle } from "./login";
import OTPDialog, { OTPImperativeHandle } from "./otp";
import SignUpDialog, { SignUpImperativeHandle } from "./sign-up";
import type { PreAuthFlowResponse } from "@/app/lib/types";
import { useAuth } from "@/app/lib/useAuth";
import type { SignUpData } from "./sign-up";

export default function LoginSignup({
  children,
  button,
  buttonAsChild = false,
}: {
  button?: React.ReactNode;
  buttonAsChild?: boolean;
  children?: React.ReactNode;
}) {
  const loginDialogRef = useRef<LoginImperativeHandle>(null);
  const signUpDialogRef = useRef<SignUpImperativeHandle>(null);
  const otpDialogRef = useRef<OTPImperativeHandle>(null);
  const signupDataRef = useRef<SignUpData | null>(null);
  const [preAuth, setPreAuth] = useState<PreAuthFlowResponse | null>(null);
  const [serverError, setServerError] = useState<string>("");
  const [resending, setResending] = useState(false);
  const { loginWithOtp, loggingIn, preAuthFlow, isAuthenticated } = useAuth();

  if (isAuthenticated) {
    return <>{children}</>;
  }

  // Use useAuth for login, keep local state only for errors

  // resend OTP by calling preAuth again
  const resend = async () => {
    if (!preAuth?.data.email) return;
    try {
      setResending(true);
      const data = await preAuthFlow(preAuth.data.email);
      console.log("resend", preAuth);
      setPreAuth(data);
      setServerError("");
    } catch (err) {
      setServerError(err instanceof Error ? err.message : String(err));
    } finally {
      setResending(false);
    }
  };

  const handleStartLogin = async () => {
    try {
      setPreAuth(null);
      setServerError("");
      const result = await loginDialogRef.current?.openDialogWithPromise();
      if (!result) return;
      console.log("handleStartLogin", result);
      setPreAuth(result);
      if (result.data.purpose === "REGISTRATION") {
        const signup = await signUpDialogRef.current?.openDialogWithPromise();
        if (!signup) return;
        signupDataRef.current = signup;
        const ok = await otpDialogRef.current?.openDialogWithPromise();
        if (!ok) return;
      } else {
        await otpDialogRef.current?.openDialogWithPromise();
      }
      setPreAuth(null);
    } catch (error) {
      if (error === CLOSE_REJECT) {
        setPreAuth(null);
        setServerError("");
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

  const handlePreAuthSuccess = (data: PreAuthFlowResponse) => {
    // Let index drive next dialogs
    console.log("handlePreAuthSuccess", data);
    setPreAuth(data);
  };

  const handleSubmitOtp = async (otp: string) => {
    if (!preAuth?.data.email || !preAuth?.data.token) {
      setServerError("Missing pre-auth token");
      return false;
    }
    try {
      // If we came from registration, we might have signup data resolved already
      // The SignUpDialog returned data will be used right before OTP open; here only pass if present
      const extra = signupDataRef.current;
      await loginWithOtp({
        email: preAuth.data.email,
        otpCode: otp,
        token: preAuth.data.token,
        ua: typeof navigator !== "undefined" ? navigator.userAgent : "",
        givenName: extra?.givenName,
        familyName: extra?.familyName,
        nickName: extra?.nickName,
        birthday: extra?.birthday,
      });
      setServerError("");
      return true;
    } catch (err) {
      setServerError(err instanceof Error ? err.message : String(err));
      return false;
    }
  };

  const handleResend = async () => {
    await resend();
  };

  const Comp = buttonAsChild ? Slot.Root : "button";

  return (
    <>
      <Comp onClick={handleStartLogin}>{button}</Comp>
      <LoginDialog
        ref={loginDialogRef}
        openSignUp={handleOpenSignUp}
        onPreAuthSuccess={handlePreAuthSuccess}
      />
      <SignUpDialog
        ref={signUpDialogRef}
        defaultEmail={preAuth?.data.email ?? ""}
      />
      <OTPDialog
        ref={otpDialogRef}
        email={preAuth?.data.email}
        onSubmitOtp={handleSubmitOtp}
        onResend={handleResend}
        loading={loggingIn || resending}
        serverError={serverError}
        onClearServerError={() => setServerError("")}
      />
    </>
  );
}
