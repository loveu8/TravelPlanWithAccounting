import { z } from "zod";
import { differenceInCalendarDays } from "date-fns";
import { MAX_DURATION_DAYS } from "./consts";

const getSchema = (
  requiredMsg: (labelKey: string) => string,
  msgs: { endDateMsg: string; endDateRangeMsg: string },
) =>
  z
    .object({
      title: z.string().nonempty({
        message: requiredMsg("schedule-modal.schedule-name"),
      }),
      startDate: z
        .date()
        .optional()
        .refine((value): value is Date => value !== undefined, {
          message: requiredMsg("schedule-modal.start-date"),
        }),
      endDate: z
        .date()
        .optional()
        .refine((value): value is Date => value !== undefined, {
          message: requiredMsg("schedule-modal.end-date"),
        }),
      visitPlace: z.string().nonempty({
        message: requiredMsg("schedule-modal.visit-place"),
      }),
      notes: z.string().optional(),
      isPrivate: z.boolean(),
    })
    .refine(
      ({ startDate, endDate }) => {
        if (!startDate || !endDate) return true;
        return endDate >= startDate;
      },
      { path: ["endDate"], message: msgs.endDateMsg },
    )
    .refine(
      ({ startDate, endDate }) => {
        if (!startDate || !endDate) return true;
        const diff = differenceInCalendarDays(endDate, startDate);
        return diff <= MAX_DURATION_DAYS;
      },
      { path: ["endDate"], message: msgs.endDateRangeMsg },
    );

export type ScheduleForm = z.infer<ReturnType<typeof getSchema>>;

export { getSchema };
