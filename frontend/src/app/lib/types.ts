export type LayoutParams = {
  params: { lng: string } | Promise<{ lng: string }>;
};

export type PreAuthFlowResponse = {
  data: {
    email: string;
    exists: boolean;
    purpose: "REGISTRATION" | "LOGIN";
    actionCode: string;
    token: string;
  };
};

export type AuthResponse = {
  data: {
    id: string;
    role: string;
    cookies: {
      access_token: { code: string; time: number };
      refresh_token: { code: string; time: number };
    };
  };
};
