"use client";
import { useState, forwardRef, useImperativeHandle } from "react";
import Button from "@/app/components/Button";
import {
  DialogRoot,
  DialogContent,
  DialogHeader,
  DialogBody,
  DialogFooter,
} from "@/app/components/Dialog";
import { Flex, Grid, Box, Text } from "@radix-ui/themes";
import { PlusIcon, CheckCircledIcon } from "@radix-ui/react-icons";
import TextField from "@/app/components/TextField";
import Pinpoint from "@/app/assets/pinpoint.svg";
import Polygon2 from "@/app/assets/polygon-2.svg";

import { useT } from "@/app/i18n/client";
import { cn } from "@/app/lib/utils";

interface IAddScheduleModalProps {
  location: string;
  viewDetailClick: (id: string) => void;
  handleCreateScheduleClick: (open: boolean) => void;
}

export interface IAddScheduleImperativeHandle {
  open: () => void;
  close: () => void;
  toggle: (id: string) => void;
}

type ISchedule = { id: string; name: string; days: string[] };
type ISelectedSchedule = {
  id: string;
  name: string;
  day?: number;
};

const AddScheduleModal = forwardRef(
  (
    {
      location,
      viewDetailClick,
      handleCreateScheduleClick,
    }: IAddScheduleModalProps,
    ref: React.ForwardedRef<IAddScheduleImperativeHandle>,
  ) => {
    const { t: translateCommon } = useT("common");
    const { t: translateHome } = useT("home");
    const [open, setOpen] = useState(false);
    const [selectedLandscapeId, setSelectedLandscapeId] = useState<string>("");
    const [selectedSchedule, setSelectedSchedule] =
      useState<ISelectedSchedule | null>(null);

    const [scheduleData, setScheduleData] = useState<ISchedule[]>([]);

    const getSchedule = () => {
      // TODO: 串取個人行程資料API
      console.log("Fetching schedule data");
      const fakeFetchedData: ISchedule[] = [
        {
          id: "1",
          name: "日本的規劃行程中",
          days: ["第一天", "第二天", "第三天"],
        },
        { id: "2", name: "泰國四日遊", days: ["第一天", "第二天"] },
        { id: "3", name: "韓國四日遊", days: ["第一天", "第二天"] },
      ];
      setScheduleData(fakeFetchedData);
      setSelectedSchedule({
        id: fakeFetchedData[0]?.id || "",
        name: fakeFetchedData[0]?.name || "",
      });
    };

    const saveSchedule = ({
      selectedLandscapeId,
      selectedSchedule,
    }: {
      selectedLandscapeId: string;
      selectedSchedule: ISelectedSchedule | null;
    }) => {
      if (!selectedLandscapeId && !selectedSchedule) {
        console.error("No landscape/schedule selected to save.");
        return;
      }
      // TODO: 串接儲存行程資料API
      console.log("Saving schedule data...");
      setOpen(false);
      setSelectedSchedule(null);
      setScheduleData([]);
    };

    const filterSchedule = (
      schedule: ISchedule[],
      selectSchedule: ISelectedSchedule | null,
    ): string[] => {
      if (!selectSchedule) {
        return [];
      }
      return (
        schedule.find((schedule) => schedule.id === selectSchedule.id)?.days ||
        []
      );
    };

    useImperativeHandle(ref, () => ({
      open: () => {
        setOpen(true);
      },
      close: () => {
        setOpen(false);
      },
      toggle: (id: string) => {
        console.log("add schedule id:" + id);
        if (open) {
          setSelectedSchedule(null);
          setScheduleData([]);
        } else {
          setSelectedLandscapeId(id);
          getSchedule();
        }
        setOpen((prev) => !prev);
      },
    }));

    return (
      <DialogRoot open={open} onOpenChange={setOpen}>
        <DialogContent className="min-w-[80vw]" headerWithClose>
          <DialogHeader title={translateHome("add-schedule.title")} />
          <DialogBody className="px-0">
            <Grid
              as="div"
              columns="minmax(200px, 1fr) 2fr "
              className="min-h-[60vh]"
            >
              <Box as="div" className="bg-gray-2">
                <Flex
                  as="div"
                  px="4"
                  py="2"
                  justify="between"
                  align="center"
                  className="border-b border-gray-6"
                >
                  <Text as="p" size="2" className="font-bold">
                    {translateHome("add-schedule.schedule-list")}
                  </Text>
                  <Button
                    text={translateHome("add-schedule.button-create-schedule")}
                    isMain={false}
                    className="text-blue-11 inset-shadow-[0_0_0_1px_var(--blue-8)] hover:inset-shadow-[0_0_0_1px_var(--blue-11)] gap-1"
                    icon={<PlusIcon color="currentColor" />}
                    size="2"
                    onClick={() => handleCreateScheduleClick(true)}
                  />
                </Flex>
                <Box as="div" px="4" py="2">
                  <TextField
                    placeholder={translateCommon("common.search")}
                    size="2"
                  />
                </Box>
                <ul>
                  {scheduleData.map((schedule) => {
                    const { id, name } = schedule;
                    return (
                      <li
                        key={id}
                        className={cn(
                          "px-4 py-3 flex items-center justify-between cursor-pointer ",
                          selectedSchedule?.id === id
                            ? "bg-white text-blue-11"
                            : "text-gray-11 hover:bg-white/40",
                        )}
                        onClick={() => setSelectedSchedule({ id, name })}
                      >
                        <Text size="2">{name}</Text>
                        {selectedSchedule?.id === id && (
                          <Polygon2
                            width={16}
                            height={16}
                            color="currentColor"
                          />
                        )}
                      </li>
                    );
                  })}
                </ul>
              </Box>
              <Box as="div">
                <Flex
                  as="div"
                  px="4"
                  py="3"
                  align="center"
                  className="border-b border-gray-6 text-blue-11"
                >
                  <Pinpoint className="text-currentColor" />
                  <Text size="4" className="font-bold leading-6 text-black">
                    {location}
                  </Text>
                </Flex>
                <Grid as="div" p="4">
                  <Text size="2" className="font-bold pb-3">
                    {translateHome("add-schedule.add-schedule-to", {
                      schedule: selectedSchedule?.name,
                    })}
                  </Text>
                  <ul className="grid gap-2">
                    {filterSchedule(scheduleData, selectedSchedule).map(
                      (day, index) => (
                        <li
                          key={index}
                          className={cn(
                            "border py-2 px-3 rounded-md flex items-center justify-between cursor-pointer",
                            index === selectedSchedule?.day
                              ? "text-blue-11 border-current"
                              : "text-gray-11 border-gray-7 ",
                          )}
                          onClick={() =>
                            setSelectedSchedule((pre) =>
                              pre ? { ...pre, day: index } : null,
                            )
                          }
                        >
                          {day}
                          {index === selectedSchedule?.day && (
                            <CheckCircledIcon
                              width={20}
                              height={20}
                              color="currentColor"
                            />
                          )}
                        </li>
                      ),
                    )}
                  </ul>
                </Grid>
              </Box>
            </Grid>
          </DialogBody>
          <DialogFooter>
            <Flex as="div" justify="between" width="100%">
              <Button
                text={translateHome("add-schedule.button-view-info")}
                isMain={false}
                handleClick={() => viewDetailClick(selectedLandscapeId)}
              />
              <Button
                text={translateHome("add-schedule.button-confirm-add")}
                handleClick={() =>
                  saveSchedule({ selectedLandscapeId, selectedSchedule })
                }
              />
            </Flex>
          </DialogFooter>
        </DialogContent>
      </DialogRoot>
    );
  },
);

AddScheduleModal.displayName = "AddScheduleModal";

export default AddScheduleModal;
