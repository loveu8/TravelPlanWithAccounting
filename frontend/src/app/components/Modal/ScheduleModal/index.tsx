"use client";
// 3rd party libraries
import { useState, forwardRef, useImperativeHandle, useCallback } from "react";
import { useParams } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { addDays } from "date-fns";
import { useForm, Controller } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Flex, Grid, Switch, Text } from "@radix-ui/themes";
// hooks + methods
import { useT } from "@/app/i18n/client";
import { api } from "@/app/lib/http";
import { getSchema, type ScheduleForm } from "./schema";
// components
import Button from "@/app/components/Button";
import DatePicker from "@/app/components/DatePicker";
import {
  DialogRoot,
  DialogContent,
  DialogHeader,
  DialogBody,
  DialogFooter,
} from "@/app/components/Dialog";
import ComboboxField from "@/app/components/ComboboxField";
import TextArea from "@/app/components/TextArea";
import TextField from "@/app/components/TextField";
// constants
import { FIELDS_DEFAULT, MAX_DURATION_DAYS } from "./consts";
import { AllLocationsResponse } from "@/app/lib/types";

interface IScheduleModalProps {
  scheduleDefault?: ScheduleForm;
  handleScheduleClick: (open: boolean) => void;
  onSubmit: (data: ScheduleForm) => Promise<void>;
}

export interface IScheduleImperative {
  toggle: () => void;
}

const ScheduleModal = forwardRef(
  (
    {
      scheduleDefault = FIELDS_DEFAULT,
      handleScheduleClick,
      onSubmit,
    }: IScheduleModalProps,
    ref: React.ForwardedRef<IScheduleImperative>,
  ) => {
    const { lng } = useParams();
    const { t } = useT("common");
    const [open, setOpen] = useState(false);
    const currentLang = lng === "zh" ? "zh-TW" : "en-US";

    const { data: allLocationsData } = useQuery({
      queryKey: ["all-locations"],
      queryFn: async () => {
        const res: AllLocationsResponse = await api.get(
          "/api/search/all-locations",
        );
        return res.data;
      },
    });

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

    // 取得 Combobox option 的 value
    const setComboboxValue = ({
      code,
    }: AllLocationsResponse["data"][number]) => {
      return code;
    };

    // Combobox option 過濾函式
    const filterComboboxOptions = (
      options: AllLocationsResponse["data"],
      value: string,
    ) => {
      const optionsByCode = new Map(
        options.map((option) => [`${option.code}@${option.langType}`, option]),
      );

      if (!value.length)
        return options.filter((option) => option.langType === currentLang);

      const matchOptions = options.filter((option) =>
        option.name.toLowerCase().includes(value.toLowerCase()),
      );

      const langMatchedOptions = matchOptions.map(
        ({ code }) => optionsByCode.get(`${code}@${currentLang}`)!,
      );

      return langMatchedOptions;
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
                <Controller
                  name="id"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      size="2"
                      type="text"
                      className="hidden"
                      errMsg={getError("id")}
                      readOnly
                      {...field}
                    />
                  )}
                />
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
                          lng={currentLang}
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
                          lng={currentLang}
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
                      <ComboboxField
                        label={t("schedule-modal.visit-place")}
                        placeholder="Enter location"
                        size="2"
                        options={allLocationsData || []}
                        value={field.value ?? []}
                        onChange={(next) => field.onChange(next)}
                        errMsg={getError("visitPlace")}
                        getOptionValue={setComboboxValue}
                        getOptionLabel={({ name }) => name}
                        optionFilter={filterComboboxOptions}
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
                            className="z-1"
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
