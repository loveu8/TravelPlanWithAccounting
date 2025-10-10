import { api } from "./http";

import type { PreAuthFlowResponse } from "@/app/lib/types";

export async function preAuthFlow(email: string): Promise<PreAuthFlowResponse> {
  const { data } = await api.post<PreAuthFlowResponse>(
    "/api/members/pre-auth-flow",
    { email },
  );
  return data;
}

// Note: client-side should call BFF /api/members/auth-flow instead of backend directly

export async function verifySession() {
  const { data } = await api.post("/api/auth/verify", {});
  return data as {
    valid: boolean;
    tokenType?: "ACCESS" | "REFRESH";
    reason: string | null;
    sub?: string;
    role?: string;
    exp?: number;
  };
}
