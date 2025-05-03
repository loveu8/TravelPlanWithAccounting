"use client";
import React from "react";
import {
  Root,
  Content,
  Title,
  Close,
} from "@radix-ui/themes/components/dialog";
import { Flex, Box } from "@radix-ui/themes";
import Button from "@/app/components/Button";

import { Cross1Icon } from "@radix-ui/react-icons";

import IDialogProps, {
  IDialogHeaderWithCloseProps,
  SizeConfigType,
} from "./dialog.types";

const sizeConfig: { [key: string]: SizeConfigType } = {
  DEFAULT: {
    gapSize: "5",
    pSize: "0",
    btnSize: "3",
  },
  SMALL: {
    gapSize: "0",
    pSize: "3",
    btnSize: "2",
  },
};

const DialogHeaderWithClose = ({ title }: IDialogHeaderWithCloseProps) => {
  const { pSize } = sizeConfig.SMALL;
  return (
    <Flex
      justify="between"
      align="center"
      py="1"
      px={pSize}
      className="border-b border-[var(--gray-4)]"
    >
      <Title size="3" trim="end" weight="regular">
        {title}
      </Title>
      <Close className="cursor-pointer" aria-label="Close">
        <Cross1Icon />
      </Close>
    </Flex>
  );
};

export default function Dialog({
  isOpen,
  title,
  closeButtons,
  footerBtnText,
  footerBtnJustify = "center",
  handleToggleClick,
  handleFooterBtnClick,
  customBtn,
  children,
}: IDialogProps) {
  const { gapSize, pSize, btnSize } = closeButtons?.header
    ? sizeConfig.SMALL
    : sizeConfig.DEFAULT;

  return (
    <Root open={isOpen} onOpenChange={handleToggleClick}>
      <Content
        aria-describedby={undefined}
        className={closeButtons?.header ? "p-0" : ""}
      >
        <Flex direction="column" gap={gapSize} py={pSize}>
          {closeButtons?.header ? (
            <DialogHeaderWithClose title={title} />
          ) : (
            <Title size="6" trim="end" align="center">
              {title}
            </Title>
          )}
          <Box px={pSize}>{children}</Box>
          {footerBtnText && (
            <Flex
              gap="3"
              justify={footerBtnJustify}
              pt={pSize}
              px={pSize}
              className={
                closeButtons?.header ? "border-t border-[var(--gray-4)]" : ""
              }
            >
              {closeButtons?.footer && (
                <Button
                  size={btnSize}
                  text="取消"
                  isMain={false}
                  handleClick={() => handleToggleClick(false)}
                />
              )}
              {customBtn && customBtn(btnSize)}
              <Button
                size={btnSize}
                text={footerBtnText}
                handleClick={handleFooterBtnClick}
              />
            </Flex>
          )}
        </Flex>
      </Content>
    </Root>
  );
}
