import axios, { AxiosInstance } from "axios";
import { cookies } from "next/headers";
import { getLangFromCookies } from "@/app/api/_utils/lang";

type CreateBackendClientOptions = {
  attachAuth?: boolean; // attach Authorization header from access_token cookie
  timeoutMs?: number;
};

export async function createBackendClient(
  opts: CreateBackendClientOptions = {},
): Promise<AxiosInstance> {
  const jar = await cookies();
  const lang = await getLangFromCookies();
  const at = jar.get("access_token")?.value;

  const instance = axios.create({
    baseURL: process.env.NEXT_PUBLIC_API_BASE_URL || "",
    headers: {
      "Content-Type": "application/json",
      "Accept-Language": lang,
      ...(opts.attachAuth && at ? { Authorization: `Bearer ${at}` } : {}),
    },
    timeout: opts.timeoutMs ?? 10000,
    // No interceptors for BFF; handle errors per-route to keep behavior explicit
  });

  return instance;
}
