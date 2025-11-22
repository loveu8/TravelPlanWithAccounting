"use client";
import { useState, useRef, forwardRef, useImperativeHandle } from "react";
import { useParams } from "next/navigation";
import { Grid, Flex, Text, Switch } from "@radix-ui/themes";
import Autocomplete from "@/app/components/Autocomplete";
import Button from "@/app/components/Button";
import {
  DialogRoot,
  DialogContent,
  DialogHeader,
  DialogBody,
  DialogFooter,
} from "@/app/components/Dialog";
import TextField from "@/app/components/TextField";
import TextArea from "@/app/components/TextArea";
import DatePicker from "@/app/components/DatePicker";

import { useT } from "@/app/i18n/client";

interface ICreateScheduleModalProps {
  handleCreateScheduleClick: (open: boolean) => void;
}

export interface ICreateScheduleImperativeHandle {
  open: () => void;
  close: () => void;
  toggle: () => void;
}

const CreateScheduleModal = forwardRef(
  (
    { handleCreateScheduleClick }: ICreateScheduleModalProps,
    ref: React.ForwardedRef<ICreateScheduleImperativeHandle>,
  ) => {
    const { lng } = useParams();
    const { t: translateCommon } = useT("common");
    const { t: translateHome } = useT("home");
    const [open, setOpen] = useState(false);
    const titleRef = useRef<HTMLInputElement>(null);
    const startDateRef = useRef<HTMLInputElement>(null);
    const endDateRef = useRef<HTMLInputElement>(null);
    const visitPlaceRef = useRef<HTMLInputElement>(null);
    const isPrivateRef = useRef<HTMLButtonElement>(null);

    const handleSubmit = (e: React.FormEvent) => {
      e.preventDefault();

      const formData = new FormData(e.currentTarget as HTMLFormElement);
      const isValid = requiredValidate(formData) && dateValidate(formData);
      if (!isValid) return;
      /**
       * @todo 處理 API 存資料
       */
    };

    // 必填欄位驗證
    const requiredValidate = (formData: FormData) => {
      const fieldsCheck = [
        { name: "title", ref: titleRef.current },
        { name: "startDate", ref: startDateRef.current },
        { name: "endDate", ref: endDateRef.current },
        { name: "visitPlace", ref: visitPlaceRef.current },
      ];
      for (const { name, ref } of fieldsCheck) {
        if (!ref) {
          console.error(`${name} input field not found`);
          return false;
        }
        ref.setCustomValidity("");
        const value = formData.get(name)?.toString().trim();
        if (!value) {
          ref.setCustomValidity(
            translateHome("validation.required", { field: name }),
          );
          ref.reportValidity();
          return false;
        }
      }
      return true;
    };

    const setEndDateError = () => {
      if (!endDateRef.current) {
        console.error(`endData input field not found`);
        return undefined;
      }
      endDateRef.current.setCustomValidity(
        translateHome("validation.end-date-after-start-date"),
      );
      endDateRef.current.reportValidity();
      return false;
    };

    const dateValidate = (formData: FormData) => {
      const startDateValue = formData.get("startDate")?.toString().trim();
      const endDateValue = formData.get("endDate")?.toString().trim();

      if (endDateValue && !startDateValue) return !!setEndDateError();

      if (startDateValue && endDateValue) {
        const startDate = new Date(startDateValue);
        const endDate = new Date(endDateValue);
        if (endDate < startDate) {
          return !!setEndDateError();
        }
      }
      return true;
    };

    useImperativeHandle(ref, () => ({
      open: () => setOpen(true),
      close: () => setOpen(false),
      toggle: () => setOpen((prev) => !prev),
    }));
    return (
      <DialogRoot open={open} onOpenChange={setOpen}>
        <DialogContent maxWidth="550px" size="4">
          <form
            name="createSchedule"
            onSubmit={open ? handleSubmit : (e) => e.preventDefault()}
          >
            <Grid columns="1" gap="5">
              <DialogHeader title={translateHome("create-schedule.title")} />
              <DialogBody>
                <Grid columns="1" gap="2">
                  <Autocomplete />
                  <TextField
                    ref={titleRef}
                    label={translateHome(
                      "create-schedule.fields.schedule-name",
                    )}
                    name="title"
                    id="title"
                    placeholder="Enter name"
                    size="2"
                    type="text"
                    required
                  />
                  <Flex justify="between" gap="2">
                    <DatePicker
                      ref={startDateRef}
                      label={translateHome("create-schedule.fields.start-date")}
                      name="startDate"
                      id="startDate"
                      localeType={lng === "zh" ? "zh-TW" : "en-US"}
                      calendarOptions={{
                        captionLayout: "dropdown",
                      }}
                      required
                    />
                    <DatePicker
                      ref={endDateRef}
                      label={translateHome("create-schedule.fields.end-date")}
                      name="endDate"
                      id="endDate"
                      localeType={lng === "zh" ? "zh-TW" : "en-US"}
                      calendarOptions={{
                        captionLayout: "dropdown",
                      }}
                      required
                    />
                  </Flex>
                  <TextField
                    ref={visitPlaceRef}
                    label={translateHome("create-schedule.fields.location")}
                    name="visitPlace"
                    id="visitPlace"
                    placeholder="Enter location"
                    size="2"
                    type="text"
                    required
                  />
                  <TextArea
                    label={translateHome("create-schedule.fields.description")}
                    name="notes"
                    id="notes"
                    placeholder="Enter description"
                    size="2"
                    my="0"
                    mb="1"
                  />
                  <Text as="label" size="2" mt="2" mb="4">
                    <Flex gap="2">
                      <Switch
                        size="2"
                        defaultChecked
                        name="isPrivate"
                        ref={isPrivateRef}
                      />
                      {translateHome("create-schedule.fields.share-schedule")}
                    </Flex>
                  </Text>
                </Grid>
              </DialogBody>
              <DialogFooter justify="center">
                <Button
                  type="button"
                  text={translateCommon("common.cancel")}
                  isMain={false}
                  onClick={() => handleCreateScheduleClick(false)}
                />
                <Button
                  type="submit"
                  text={translateHome("create-schedule.button-create")}
                />
              </DialogFooter>
            </Grid>
          </form>
        </DialogContent>
      </DialogRoot>
    );
  },
);

CreateScheduleModal.displayName = "CreateScheduleModal";
export default CreateScheduleModal;
