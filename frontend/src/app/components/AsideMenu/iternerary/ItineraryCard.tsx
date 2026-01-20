import Button from "@/app/components/Button";
import Image from "next/image";
import styles from "./card.module.css";
// TODO: 語系
export default function ItineraryCard() {
  return (
    <div className="flex gap-3 p-5">
      <Image src="/img/Japan.jpg" alt="日本" width={220} height={155} />
      <div className="flex-1 flex flex-col gap-2">
        <div className="flex justify-between items-center w-full">
          <h3 className="text-lg font-bold">日本東京五天四夜行程</h3>
          <Button
            className={`bg-transparent text-[var(--gray-11)] font-510 ${styles["card-item-outline"]}`}
            text="編輯總覽"
            size="2"
          />
        </div>
        <p className="w-[300px] text-sm text-[var(--gray-11)] flex items-center gap-[6px] flex-wrap">
          {/* TODO: 迴圈 */}
          <span className={`p-[4px_8px] ${styles["card-item-outline"]}`}>
            地點
          </span>
          <span>日本東京</span>
          <span className={`p-[4px_8px] ${styles["card-item-outline"]}`}>
            天數
          </span>
          <span>(6天)- 05/07~05/12</span>
          <span className={`p-[4px_8px] ${styles["card-item-outline"]}`}>
            天氣
          </span>
          <span>天氣晴 25~30 度</span>
        </p>
        <p className="text-[13px] opacity-60">
          即將展開令人期待的東京五日遊，行程涵蓋淺草寺、東京塔、台場等經典景點，感受古今融合的都市魅力。除了購物血拼，還能大啖壽司、拉麵等日式美食，體驗文化與風景的完美結合
        </p>
      </div>
    </div>
  );
}
