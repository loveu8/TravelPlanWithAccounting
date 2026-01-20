"use client";
// 3rd party libraries
import { useState, forwardRef, useImperativeHandle, useCallback } from "react";
import { useParams } from "next/navigation";
import { addDays } from "date-fns";
import { useForm, Controller } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Flex, Grid, Switch, Text } from "@radix-ui/themes";
// methods and hooks
import { getSchema, type ScheduleForm } from "./schema";
import { useT } from "@/app/i18n/client";
// components
import Autocomplete from "@/app/components/Autocomplete";
import Button from "@/app/components/Button";
import DatePicker from "@/app/components/DatePicker";
import {
  DialogRoot,
  DialogContent,
  DialogHeader,
  DialogBody,
  DialogFooter,
} from "@/app/components/Dialog";
import TextArea from "@/app/components/TextArea";
import TextField from "@/app/components/TextField";
// constants
import { FIELDS_DEFAULT, MAX_DURATION_DAYS } from "./consts";

interface IScheduleModalProps {
  scheduleDefault?: ScheduleForm;
  handleScheduleClick: (open: boolean) => void;
}

export interface IScheduleImperative {
  toggle: () => void;
}

const ScheduleModal = forwardRef(
  (
    {
      scheduleDefault = FIELDS_DEFAULT,
      handleScheduleClick,
    }: IScheduleModalProps,
    ref: React.ForwardedRef<IScheduleImperative>,
  ) => {
    const { lng } = useParams();
    const { t } = useT("common");
    const [open, setOpen] = useState(false);

    const fetchAutoCompleteList = async (query: string) => {
      /**
       * TODO: 處理 API 取資料
       */
      console.log(query);
      return ["123", "234", "345"];
    };

    const requiredMsg = (labelKey: string) =>
      t("validation.required", {
        label: t(labelKey),
      });

    const schema = getSchema(requiredMsg, {
      endDateMsg: t("validation.end-date-after-start-date"),
      endDateRangeMsg: t("validation.max-schedule-duration-days", {
        max: MAX_DURATION_DAYS,
      }),
    });

    const {
      control,
      handleSubmit,
      reset,
      watch,
      setValue,
      formState: { errors },
    } = useForm<ScheduleForm>({
      resolver: zodResolver(schema),
      defaultValues: scheduleDefault,
    });

    const startDateValue = watch("startDate");

    const endDateDisabledRange = (startDate: Date | undefined) => {
      if (!startDate) return undefined;
      const afterDate = addDays(startDate, 29);
      return {
        before: startDate,
        after: afterDate,
      };
    };

    const getError = (field: keyof ScheduleForm) =>
      errors[field]?.message?.toString();

    const handleKeyDown = (e: React.KeyboardEvent) => {
      if (e.key === "Enter") e.preventDefault();
    };

    const onSubmit = (data: ScheduleForm) => {
      /**
       * TODO: 處理 API 存資料
       */
      console.log({ data });
    };

    const handleCloseClick = useCallback(() => {
      reset();
      handleScheduleClick(false);
    }, [reset, handleScheduleClick]);

    useImperativeHandle(ref, () => ({
      toggle: () => setOpen((prev) => !prev),
    }));

    return (
      <DialogRoot open={open} onOpenChange={setOpen}>
        <DialogContent maxWidth="550px" size="4">
          <form
            onSubmit={open ? handleSubmit(onSubmit) : () => false}
            onKeyDown={handleKeyDown}
          >
            <Grid columns="1" gap="5">
              <DialogHeader title={t("schedule-modal.title")} />
              <DialogBody>
                <Grid columns="1" gap="2">
                  <Controller
                    name="title"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        label={t("schedule-modal.schedule-name")}
                        placeholder="Enter name"
                        size="2"
                        type="text"
                        errMsg={getError("title")}
                        {...field}
                      />
                    )}
                  />
                  <Flex justify="between" gap="3">
                    <Controller
                      name="startDate"
                      control={control}
                      render={({ field }) => (
                        <DatePicker
                          label={t("schedule-modal.start-date")}
                          lng={lng === "zh" ? "zh-TW" : "en-US"}
                          calendarOptions={{
                            captionLayout: "dropdown",
                          }}
                          value={field.value}
                          onChange={(date) => {
                            setValue("endDate", undefined);
                            return field.onChange(date);
                          }}
                          errMsg={getError("startDate")}
                        />
                      )}
                    />
                    <Controller
                      name="endDate"
                      control={control}
                      render={({ field }) => (
                        <DatePicker
                          label={t("schedule-modal.end-date")}
                          disabled={!startDateValue}
                          lng={lng === "zh" ? "zh-TW" : "en-US"}
                          calendarOptions={{
                            captionLayout: "dropdown",
                            disabled: endDateDisabledRange(startDateValue),
                          }}
                          value={field.value}
                          onChange={(date) => field.onChange(date)}
                          errMsg={getError("endDate")}
                        />
                      )}
                    />
                  </Flex>
                  <Controller
                    name="visitPlace"
                    control={control}
                    render={({ field }) => (
                      <Autocomplete
                        label={t("schedule-modal.visit-place")}
                        placeholder="Enter location"
                        size="2"
                        type="text"
                        getSuggestions={fetchAutoCompleteList}
                        errMsg={getError("visitPlace")}
                        {...field}
                      />
                    )}
                  />
                  <Controller
                    name="notes"
                    control={control}
                    render={({ field }) => (
                      <TextArea
                        label={t("schedule-modal.description")}
                        placeholder="Enter description"
                        size="2"
                        my="0"
                        mb="1"
                        errMsg={getError("notes")}
                        {...field}
                      />
                    )}
                  />
                  <Text as="label" size="2" mt="2" mb="4">
                    <Flex gap="2">
                      <Controller
                        name="isPrivate"
                        control={control}
                        render={({ field }) => (
                          <Switch
                            size="2"
                            checked={field.value}
                            onCheckedChange={field.onChange}
                          />
                        )}
                      />
                      {t("schedule-modal.share-schedule")}
                    </Flex>
                  </Text>
                </Grid>
              </DialogBody>
              <DialogFooter justify="center">
                <Button
                  type="button"
                  text={t("common.cancel")}
                  isMain={false}
                  onClick={handleCloseClick}
                />
                <Button
                  type="submit"
                  text={t("schedule-modal.button-create")}
                />
              </DialogFooter>
            </Grid>
          </form>
        </DialogContent>
      </DialogRoot>
    );
  },
);

ScheduleModal.displayName = "ScheduleModal";
export default ScheduleModal;
