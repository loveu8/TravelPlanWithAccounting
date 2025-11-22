import { useState, forwardRef, useImperativeHandle } from "react";
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
 * @property {function} handleBookmarkClick - 處理收藏按鈕點擊的函數
 * @property {function} handleAddScheduleClick - 處理添加行程按鈕點擊的函數
 */
interface ITravelPlanCardProps {
  handleBookmarkClick: (id: string) => void;
  handleAddScheduleClick: (id: string) => void;
}

type IModalDataType = Omit<
  ILandscapeDetailCardProps,
  "id" | "handleAddScheduleClick" | "handleBookmarkClick"
> & {
  iframeSrc: string;
};

export interface IViewPlanDetailImperativeHandle {
  open: () => void;
  close: () => void;
  toggle: (id: string) => void;
}

const ViewPlanDetailModal = forwardRef(
  (
    { handleAddScheduleClick, handleBookmarkClick }: ITravelPlanCardProps,
    ref: React.ForwardedRef<IViewPlanDetailImperativeHandle>,
  ) => {
    const [open, setOpen] = useState(false);
    const [iframeSrc, setIframeSrc] = useState<string>("");
    const [planDetail, setPlanDetail] =
      useState<ILandscapeDetailCardProps | null>(null);

    const getPlanDetail = (id: string) => {
      // TODO: 串取景點詳細資料API
      console.log("Fetching plan detail ID:", id);
      const fakeFetchedData: IModalDataType = {
        title: "東京迪士尼",
        iframeSrc: "https://www.google.com/maps?q=東京迪士尼&output=embed",
        imgSrc: "/img/Japan.jpg",
        score: 4.5,
        evaluateCount: 100,
        isBookmarked: true,
        details: {
          description:
            "著名主題公園的東京分支，以遊樂設施、現場表演和古裝人物聞名",
          address: "1-1 Maihama, Urayasu, Chiba 279-0031日本",
          phone: "+81453305211",
          website: "https://www.tokyodisneyresort.jp/tdl/",
          hours: [
            "星期日 08:00–22:00",
            "星期六 08:00–22:00",
            "星期六 08:00–22:00",
            "星期六 08:00–22:00",
            "星期六 08:00–22:00",
          ],
        },
      };

      const { iframeSrc, ...fakeData } = fakeFetchedData;
      setPlanDetail({
        ...fakeData,
        id,
        handleAddScheduleClick: () => handleAddScheduleClick(id),
        handleBookmarkClick: handleBookmarkClick,
      });
      setIframeSrc(iframeSrc);
    };

    useImperativeHandle(ref, () => ({
      open: () => {
        setOpen(true);
      },
      close: () => {
        setOpen(false);
      },
      toggle: (id: string) => {
        console.log({ id });
        if (open) {
          setPlanDetail(null);
        } else {
          getPlanDetail(id);
        }
        setOpen((prev) => !prev);
      },
    }));
    return (
      <DialogRoot open={open} onOpenChange={setOpen}>
        <DialogContent className="min-w-[80vw] pb-0" headerWithClose>
          <DialogHeader title={planDetail?.title || ""} />
          <DialogBody className="relative overflow-hidden px-0">
            <Grid
              as="div"
              columns="420px 1fr"
              align="start"
              className="min-h-[75vh]"
            >
              {planDetail && <ViewLandscapeDetailCard {...planDetail} />}
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
  },
);

ViewPlanDetailModal.displayName = "ViewPlanDetailModal";

export default ViewPlanDetailModal;
