import React from "react";
import {
  DialogRoot,
  DialogContent,
  DialogHeader,
  DialogBody,
} from "@/app/components/Dialog";
import { Grid } from "@radix-ui/themes";

import type { ILandscapeDetailCardProps } from "@/app/components/Card/card.types";

import ViewLandscapeDetailCard from "@/app/components/Card/style/view-landscape-detail-card";

/*
 * ViewLandscapeDetailModal 元件 Props
 * @type {Object} ITravelPlanCardProps
 * @extends ILandscapeDetailCardProps
 * @property {boolean} open - 是否打開彈窗
 * @property {function} onOpenChange - 控制彈窗開關的函數
 * @property {string} title - 彈窗標題
 * @property {string} iframeSrc - 嵌入的iframe的URL
 */
interface ITravelPlanCardProps extends ILandscapeDetailCardProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  iframeSrc: string;
}

export default function ViewLandscapeDetailModal({
  open,
  onOpenChange,
  title,
  iframeSrc,
  ...props
}: ITravelPlanCardProps) {
  return (
    <DialogRoot open={open} onOpenChange={onOpenChange}>
      <DialogContent className="min-w-[80vw] pb-0" headerWithClose>
        <DialogHeader title={title} />
        <DialogBody className="relative overflow-hidden px-0">
          <Grid
            as="div"
            columns="420px 1fr"
            align="start"
            className="min-h-[75vh]"
          >
            <ViewLandscapeDetailCard title={title} {...props} />
            <iframe
              width="100%"
              height="100%"
              loading="lazy"
              allowFullScreen
              referrerPolicy="no-referrer-when-downgrade"
              src={iframeSrc}
            />
          </Grid>
        </DialogBody>
      </DialogContent>
    </DialogRoot>
  );
}
