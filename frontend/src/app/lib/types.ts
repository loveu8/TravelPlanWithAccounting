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

export type RefreshResponse = {
  data: { accessToken: string; expiresIn: number };
};

export type SessionData = {
  valid?: boolean;
  sub?: string;
  id?: string;
  role?: string;
  exp?: number;
  tokenType?: "ACCESS" | "REFRESH";
  reason?: string | null;
};

export type AllLocationsResponse = {
  data: {
    code: string;
    name: string;
    langType: string;
  }[];
};

export type CreateTravelMainResponse = {
  data: {
    id: string;
    memberId: string;
    isPrivate: boolean;
    startDate: string;
    endDate: string;
    title: string;
    notes: string;
    visitPlace: string;
    createdAt: string;
    generatedTravelDates: {
      id: string;
      travelMainId: string;
      travelDate: string;
      sort: number;
    }[];
  } | null;
  meta: null;
  error: {
    code: number;
    message: string;
    timestamp: string;
    details: null;
  } | null;
};
