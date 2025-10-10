"use client";
import React, { createContext, useCallback, useContext, useMemo } from "react";
import { api } from "./http";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { PreAuthFlowResponse } from "./types";

export type AuthState = {
  user: { id: string; role: string } | null;
  isAuthenticated: boolean;
  loading: boolean;
  // request states
  verifying: boolean;
  loggingIn: boolean;
  refreshing: boolean;
  loggingOut: boolean;
  preAuthorizing: boolean;
  // actions
  preAuthFlow: (email: string) => Promise<PreAuthFlowResponse>;
  loginWithOtp: (input: {
    email: string;
    otpCode: string;
    token: string;
    ip?: string;
    ua?: string;
    givenName?: string;
    familyName?: string;
    nickName?: string;
    birthday?: string;
  }) => Promise<void>;
  refresh: () => Promise<boolean>;
  logout: () => Promise<void>;
};

const AuthContext = createContext<AuthState | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const queryClient = useQueryClient();
  type SessionData = {
    valid?: boolean;
    sub?: string;
    id?: string;
    role?: string;
    exp?: number;
    tokenType?: "ACCESS" | "REFRESH";
    reason?: string | null;
  };
  const sessionQuery = useQuery({
    queryKey: ["auth", "session"],
    queryFn: async () => {
      try {
        const { data } = await api.post("/api/auth/verify", {});
        return data as SessionData;
      } catch {
        return { valid: false } as SessionData;
      }
    },
    staleTime: 60 * 1000,
    refetchOnWindowFocus: true,
  });

  const user = useMemo(() => {
    const d = sessionQuery.data as
      | { valid?: boolean; sub?: string; id?: string; role?: string }
      | undefined;
    if (!d || d.valid === false) return null;
    return { id: d.sub ?? d.id ?? "", role: d.role ?? "" };
  }, [sessionQuery.data]);
  const loading = sessionQuery.isLoading;

  const { mutateAsync: loginMutateAsync, isPending: loggingIn } = useMutation({
    mutationFn: async (input: {
      email: string;
      otpCode: string;
      token: string;
      ip?: string;
      ua?: string;
      givenName?: string;
      familyName?: string;
      nickName?: string;
      birthday?: string;
    }) => {
      const res = await api.post("/api/members/auth-flow", input);
      return res.data as { id: string; role: string };
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["auth", "session"] });
    },
  });
  const loginWithOtp = useCallback(
    (input: Parameters<typeof loginMutateAsync>[0]) =>
      loginMutateAsync(input).then(() => {}),
    [loginMutateAsync],
  );

  // Pre-auth to decide LOGIN vs REGISTRATION and send OTP
  const { mutateAsync: preAuthMutateAsync, isPending: preAuthorizing } =
    useMutation({
      mutationFn: async (email: string) => {
        const { data } = await api.post<PreAuthFlowResponse>(
          "/api/members/pre-auth-flow",
          { email },
        );
        return data;
      },
    });
  const preAuthFlow = useCallback(
    (email: string) => {
      return preAuthMutateAsync(email);
    },
    [preAuthMutateAsync],
  );

  const { mutateAsync: refreshMutateAsync, isPending: refreshing } =
    useMutation({
      mutationFn: async () => {
        const res = await api.post("/api/auth/refresh", {});
        return res.data as { accessToken?: string };
      },
      onSuccess: async () => {
        await queryClient.invalidateQueries({ queryKey: ["auth", "session"] });
      },
    });
  const refresh = useCallback(async (): Promise<boolean> => {
    const data = await refreshMutateAsync();
    return !!data?.accessToken;
  }, [refreshMutateAsync]);

  const { mutateAsync: logoutMutateAsync, isPending: loggingOut } = useMutation(
    {
      mutationFn: async () => {
        await api.post("/api/auth/logout");
      },
      onSuccess: async () => {
        await queryClient.invalidateQueries({ queryKey: ["auth", "session"] });
      },
    },
  );
  const logout = useCallback(async () => {
    await logoutMutateAsync();
  }, [logoutMutateAsync]);

  const value = useMemo<AuthState>(
    () => ({
      user,
      isAuthenticated: !!user,
      loading,
      verifying: sessionQuery.isFetching,
      loggingIn,
      refreshing,
      loggingOut,
      preAuthorizing,
      preAuthFlow,
      loginWithOtp,
      refresh,
      logout,
    }),
    [
      user,
      loading,
      sessionQuery.isFetching,
      loggingIn,
      refreshing,
      loggingOut,
      preAuthorizing,
      preAuthFlow,
      loginWithOtp,
      refresh,
      logout,
    ],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
