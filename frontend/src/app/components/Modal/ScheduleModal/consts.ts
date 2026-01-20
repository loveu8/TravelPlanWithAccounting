import type { ScheduleForm } from "./schema";

export const MAX_DURATION_DAYS: number = 30;

export const FIELDS_DEFAULT: ScheduleForm = {
  title: "",
  startDate: undefined,
  endDate: undefined,
  visitPlace: "",
  notes: "",
  isPrivate: true,
};
