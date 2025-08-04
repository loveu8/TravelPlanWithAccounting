import React from "react";
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

interface IAddScheduleModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export default function AddScheduleModal({
  open,
  onOpenChange,
}: IAddScheduleModalProps) {
  return (
    <DialogRoot open={open} onOpenChange={onOpenChange}>
      <DialogContent className="min-w-[80vw]" headerWithClose>
        <DialogHeader title="加入到哪一個行程" />
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
                  行程列表
                </Text>
                <Button
                  text="創建新行程"
                  isMain={false}
                  className="text-blue-11 inset-shadow-[0_0_0_1px_var(--blue-8)] hover:inset-shadow-[0_0_0_1px_var(--blue-11)] gap-1"
                  icon={<PlusIcon color="currentColor" />}
                  size="2"
                />
              </Flex>
              <Box as="div" px="4" py="2">
                <TextField placeholder="搜尋" size="2" />
              </Box>
              <ul>
                <li className="bg-white text-blue-11 px-4 py-3 flex items-center justify-between">
                  <Text size="2">日本的規劃進行中</Text>
                  <Polygon2 width={16} height={16} color="currentColor" />
                </li>
                <li className="px-4 py-3 flex items-center justify-between cursor-pointer hover:bg-white/40">
                  <Text size="2">泰國四日遊</Text>
                </li>
                <li className="px-4 py-3 flex items-center justify-between cursor-pointer hover:bg-white/40">
                  <Text size="2">泰國四日遊</Text>
                </li>
                <li className="px-4 py-3 flex items-center justify-between cursor-pointer hover:bg-white/40">
                  <Text size="2">泰國四日遊</Text>
                </li>
                <li className="px-4 py-3 flex items-center justify-between cursor-pointer hover:bg-white/40">
                  <Text size="2">泰國四日遊</Text>
                </li>
                <li className="px-4 py-3 flex items-center justify-between cursor-pointer hover:bg-white/40">
                  <Text size="2">泰國四日遊</Text>
                </li>
              </ul>
            </Box>
            <Box as="div">
              <Flex
                as="div"
                px="4"
                py="3"
                gap="1"
                align="center"
                className="border-b border-gray-6 text-blue-11"
              >
                <Pinpoint className="text-currentColor" />
                <Text size="4" className="font-bold leading-6">
                  東京迪士尼
                </Text>
              </Flex>
              <Grid as="div" p="4">
                <Text size="2" className="font-bold pb-3">
                  加到「日本的規劃行程中」的哪一日
                </Text>
                <ul className="grid gap-2">
                  <li className="text-blue-11 border border-current py-2 px-3 rounded-md flex items-center justify-between">
                    <Text size="3">第一天</Text>
                    <CheckCircledIcon
                      width={20}
                      height={20}
                      color="currentColor"
                    />
                  </li>
                  <li className="text-gray-11 border border-gray-7 py-2 px-3  rounded-md">
                    第二天
                  </li>
                  <li className="text-gray-11 border border-gray-7 py-2 px-3  rounded-md">
                    第二天
                  </li>
                  <li className="text-gray-11 border border-gray-7 py-2 px-3  rounded-md">
                    第二天
                  </li>
                  <li className="text-gray-11 border border-gray-7 py-2 px-3  rounded-md">
                    第二天
                  </li>
                </ul>
              </Grid>
            </Box>
          </Grid>
        </DialogBody>
        <DialogFooter>
          <Flex as="div" justify="between" width="100%">
            <Button text="查看景點資訊" isMain={false} />
            <Button text="確定加入" />
          </Flex>
        </DialogFooter>
      </DialogContent>
    </DialogRoot>
  );
}
