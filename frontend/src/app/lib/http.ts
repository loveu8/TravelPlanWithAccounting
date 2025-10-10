import axios, { AxiosError, InternalAxiosRequestConfig } from "axios";

// Central axios instance
export const api = axios.create({
  baseURL: "", // always call BFF on same origin
  headers: {
    "Content-Type": "application/json",
  },
});

// No request interceptor for Authorization: cookies are sent automatically to BFF

// ---- Separate client for refresh to avoid interceptor loops ----
const refreshClient = axios.create({
  baseURL: "", // call Next.js BFF routes on same origin
  headers: { "Content-Type": "application/json" },
});

type RefreshResponse = { accessToken: string; expiresIn: number };

let isRefreshing = false;
let pendingQueue: Array<(token: string | null) => void> = [];
function subscribeTokenRefresh(cb: (token: string | null) => void) {
  pendingQueue.push(cb);
}
function onRefreshed(token: string | null) {
  pendingQueue.forEach((cb) => cb(token));
  pendingQueue = [];
}

api.interceptors.response.use(
  (res) => res,
  async (error: AxiosError) => {
    const original = error.config as
      | (InternalAxiosRequestConfig & { _retry?: boolean })
      | undefined;
    if (!original || error.response?.status !== 401 || original._retry) {
      return Promise.reject(error);
    }

    original._retry = true;

    if (isRefreshing) {
      return new Promise((resolve, reject) => {
        subscribeTokenRefresh((newToken) => {
          if (!newToken) {
            reject(error);
            return;
          }
          resolve(api.request(original));
        });
      });
    }

    try {
      isRefreshing = true;
      const { data } = await refreshClient.post<RefreshResponse>(
        "/api/auth/refresh",
        {},
      );
      // Do not store or set headers; cookies updated by BFF are enough
      onRefreshed(data.accessToken || null);
      return api.request(original);
    } catch (e) {
      onRefreshed(null);
      return Promise.reject(e);
    } finally {
      isRefreshing = false;
    }
  },
);
