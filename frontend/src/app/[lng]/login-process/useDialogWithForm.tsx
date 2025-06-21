"use client";
import { useState, useEffect, useRef, useCallback } from "react";

export const CLOSE_REJECT = "CLOSED";

function useDialogWithForm<T>({
  onSubmit,
  timeout,
}: {
  onSubmit: (e: React.FormEvent<HTMLFormElement>) => Promise<T> | void;
  /**
   * 可傳入timeout參數，設定當 Dialog 開啟時開始倒數計時
   */
  timeout?: number;
}) {
  const [open, setOpen] = useState(false);
  const [error, setError] = useState<string>("");
  const [countdown, setCountdown] = useState<number>(timeout || 0);
  const animationTimeoutRef = useRef<NodeJS.Timeout | null>(null);
  const formRef = useRef<HTMLFormElement>(null);
  const resolverRef = useRef<((value: T) => void) | null>(null);
  const rejecterRef = useRef<((reason?: unknown) => void) | null>(null);
  const intervalRef = useRef<NodeJS.Timeout | null>(null);
  const countdownStartRef = useRef<number | null>(null);

  const startCountdown = useCallback(() => {
    if (intervalRef.current) {
      clearInterval(intervalRef.current);
      intervalRef.current = null;
    }
    if (!timeout || timeout <= 0) {
      setCountdown(0);
      return;
    }
    countdownStartRef.current = Date.now();
    setCountdown(timeout);
    intervalRef.current = setInterval(() => {
      if (!countdownStartRef.current) return;
      const elapsed = Math.floor(
        (Date.now() - countdownStartRef.current) / 1000,
      );
      const remain = Math.max(timeout - elapsed, 0);
      setCountdown(remain);
      if (remain <= 0) {
        clearInterval(intervalRef.current!);
        intervalRef.current = null;
      }
    }, 250); // 更頻繁地檢查，確保精度
  }, [timeout]);

  const stopCountdown = useCallback(() => {
    if (intervalRef.current) {
      clearInterval(intervalRef.current);
      intervalRef.current = null;
    }
    setCountdown(0);
  }, []);

  const handleOpenChange = useCallback(
    (isOpen: boolean) => {
      setOpen(isOpen);
      if (!isOpen) {
        animationTimeoutRef.current = setTimeout(() => {
          setError("");
          if (formRef.current) {
            formRef.current.reset();
          }
        }, 300);
        // If dialog closed without submit, reject the promise
        if (rejecterRef.current) {
          rejecterRef.current(CLOSE_REJECT);
          resolverRef.current = null;
          rejecterRef.current = null;
        }
        stopCountdown();
      } else {
        startCountdown();
      }
    },
    [startCountdown, stopCountdown],
  );

  useEffect(
    () => () => {
      if (animationTimeoutRef.current) {
        clearTimeout(animationTimeoutRef.current);
      }
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
      }
    },
    [],
  );

  const openDialogWithPromise = useCallback(() => {
    setOpen(true);
    startCountdown();
    return new Promise<T>((resolve, reject) => {
      resolverRef.current = resolve;
      rejecterRef.current = reject;
    });
  }, [startCountdown]);

  const handleSubmit = useCallback(
    async (e: React.FormEvent<HTMLFormElement>) => {
      e.preventDefault();
      try {
        const result = await onSubmit(e);
        if (!result) {
          throw new Error("Form submission did not return a valid result");
        }
        setOpen(false);
        animationTimeoutRef.current = setTimeout(() => {
          setError("");
          if (formRef.current) {
            formRef.current.reset();
          }
        }, 300);
        if (resolverRef.current) {
          resolverRef.current(result);
          resolverRef.current = null;
          rejecterRef.current = null;
        }
        stopCountdown();
      } catch (err) {
        if (err !== CLOSE_REJECT) {
          setError(
            err instanceof Error ? err.message : "An unexpected error occurred",
          );
        }
        // You can show an error message here if needed
        if (rejecterRef.current) {
          rejecterRef.current(err);
          resolverRef.current = null;
          rejecterRef.current = null;
        }
      }
    },
    [onSubmit, stopCountdown],
  );

  return {
    open,
    error,
    setError,
    openDialogWithPromise,
    handleSubmit,
    handleOpenChange,
    countdown,
    startCountdown,
  };
}

export default useDialogWithForm;
