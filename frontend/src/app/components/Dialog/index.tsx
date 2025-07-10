"use client";
import React, { createContext, useContext } from "react";
import {
  Root,
  Trigger,
  Content,
  Title,
  Close,
} from "@radix-ui/themes/components/dialog";
import { Flex, Box } from "@radix-ui/themes";
import Button from "@/app/components/Button";

import { Cross1Icon } from "@radix-ui/react-icons";

import {
  SizeConfigType,
  IDialogContentProps,
  IDialogHeaderProps,
  IDialogBodyProps,
  IDialogFooterProps,
} from "./dialog.types";

const sizeConfig: Record<"DEFAULT" | "SMALL", SizeConfigType> = {
  DEFAULT: {
    pSizeTW: "",
    gapSize: "5",
    pSize: "0",
    titleSize: "6",
    titleWeight: "bold",
    btnSize: "3",
  },
  SMALL: {
    pSizeTW: "p-0",
    gapSize: "0",
    pSize: "3",
    titleSize: "3",
    titleWeight: "regular",
    btnSize: "2",
  },
};

function getSizeConfig(headerWithClose?: boolean): SizeConfigType {
  return sizeConfig[headerWithClose ? "SMALL" : "DEFAULT"];
}

const DialogContext = createContext<{
  headerWithClose: boolean;
  sizeConfig: SizeConfigType;
}>({ headerWithClose: false, sizeConfig: sizeConfig.DEFAULT });

const useDialogContext = () => useContext(DialogContext);

function DialogContent({
  headerWithClose = false,
  children,
  ...contentProps
}: IDialogContentProps) {
  const sizeConfig = getSizeConfig(headerWithClose);

  return (
    <DialogContext.Provider value={{ headerWithClose, sizeConfig }}>
      <Content
        aria-describedby={undefined}
        className={sizeConfig.pSizeTW}
        {...contentProps}
      >
        <Flex direction="column" gap={sizeConfig.gapSize} py={sizeConfig.pSize}>
          {children}
        </Flex>
      </Content>
    </DialogContext.Provider>
  );
}

function DialogHeader({ title }: IDialogHeaderProps) {
  const { headerWithClose, sizeConfig } = useDialogContext();
  if (!headerWithClose) {
    return (
      <Title
        size={sizeConfig.titleSize}
        trim="end"
        weight={sizeConfig.titleWeight}
        align="center"
      >
        {title}
      </Title>
    );
  }
  return (
    <Flex
      justify="between"
      align="center"
      py="1"
      px={sizeConfig.pSize}
      className="border-b border-gray-4"
    >
      <Title
        size={sizeConfig.titleSize}
        trim="end"
        weight={sizeConfig.titleWeight}
      >
        {title}
      </Title>
      <Close className="cursor-pointer" aria-label="Close">
        <Cross1Icon />
      </Close>
    </Flex>
  );
}

function DialogBody({ className, children }: IDialogBodyProps) {
  const { sizeConfig } = useDialogContext();

  return (
    <Box px={sizeConfig.pSize} className={className}>
      {children}
    </Box>
  );
}

function DialogFooter({ withCloseBtn, justify, children }: IDialogFooterProps) {
  const { headerWithClose, sizeConfig } = useDialogContext();

  return (
    <Flex
      gap="3"
      justify={justify}
      pt={sizeConfig.pSize}
      px={sizeConfig.pSize}
      className={headerWithClose ? "border-t border-gray-4" : ""}
    >
      {withCloseBtn && (
        <Close>
          <Button
            type="button"
            size={sizeConfig.btnSize}
            text="取消"
            isMain={false}
          />
        </Close>
      )}
      {children}
    </Flex>
  );
}
export {
  Root as DialogRoot, // 若子元件無使用Trigger，請傳遞open和onOpenChange
  Trigger as DialogTrigger, // 選用
  DialogContent,
  DialogHeader,
  DialogBody,
  DialogFooter,
};
