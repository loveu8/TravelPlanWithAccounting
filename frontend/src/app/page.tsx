"use client";
import Button from "@/app/components/Button";
import Badge from "@/app/components/Badge";
import {
  DialogRoot,
  DialogContent,
  DialogHeader,
  DialogBody,
  DialogFooter,
  DialogTrigger,
} from "@/app/components/Dialog";
import TextField, { TextFieldSlot } from "@/app/components/TextField";
import DatePicker from "@/app/components/DatePicker";

import { AvatarIcon } from "@radix-ui/react-icons";
import Image from "next/image";
import { UnderlineTab, PillTab } from "@/app/components/Tab";

const tabItems = [
  {
    value: "tab1",
    label: "行程列表",
    content: <div>行程列表的内容</div>,
  },
  {
    value: "tab2",
    label: "探索地圖",
    content: <div>探索地圖的内容</div>,
  },
  {
    value: "tab3",
    label: "收藏",
    content: "收藏的内容",
  },
];

export default function Home() {
  const handleRemoveClick = (id: string, num: number) => {
    console.log("Remove badge with id:", id, num);
  };

  return (
    <div className="grid grid-rows-[auto_1fr_20px] items-center justify-items-center min-h-screen p-8 pb-20 gap-16 sm:p-20 font-[family-name:var(--font-geist-sans)]">
      <div className="space-y-4 w-full">
        <div className="space-x-4">
          <Button text="主按鈕" />
          <Button text="disable按鈕" isDisabled={true} />
          <Button text="次按鈕" isMain={false} />
        </div>
        <div className="space-x-4">
          <Badge text="文化活動" />
          <Badge text="badge" bgColor="blue" />
          <Badge
            text="badgeWithCloseIcon"
            handleRemoveClick={() => handleRemoveClick("1", 2)}
          />
          <Badge
            text="User"
            icon={<AvatarIcon width="20px" height="20px" />}
            bgColor="transparent"
          />
        </div>

        <DialogRoot>
          <DialogTrigger>
            <Button
              text="test"
              size="3"
              onClick={(e) => {
                console.log(e.target);
              }}
            />
          </DialogTrigger>
          <DialogContent headerWithClose={true}>
            <DialogHeader title="Title" />
            <DialogBody className="max-h-[100px] overflow-y-auto">
              <div className="flex flex-col gap-4">
                <p>這是一個對話框的內容。</p>
                <p>這是一個對話框的內容。</p>
                <p>這是一個對話框的內容。</p>
                <p>這是一個對話框的內容。</p>
                <p>這是一個對話框的內容。</p>
              </div>
            </DialogBody>
            <DialogFooter withCloseBtn={true} justify="end">
              <Button text="確認" size="2" />
            </DialogFooter>
          </DialogContent>
        </DialogRoot>

        <div className="w-full flex gap-10 mb-8">
          <div>
            <h3>underline</h3>
            <UnderlineTab
              items={tabItems}
              underlineColor="yellow"
              underlineHeight={3}
              onValueChange={(value: string) =>
                console.log("UnderlineTab onValueChange", value)
              }
            />
          </div>
          <div>
            <h3>pill</h3>
            <PillTab
              items={tabItems}
              backgroundColor="bg-red-100"
              onValueChange={(value: string) =>
                console.log("PillTab onValueChange", value)
              }
            />
          </div>
        </div>
        <div className="space-x-4">
          <TextField size="3" placeholder="請輸入..." label="輸入欄位" disabled>
            <TextFieldSlot side="right">
              <AvatarIcon />
            </TextFieldSlot>
          </TextField>
        </div>
        <div className="space-x-4">
          <DatePicker localeType="zh-TW" />
          <form onSubmit={(e) => e.preventDefault()}>
            <TextField
              size="2"
              placeholder="電話號碼"
              defaultValue="0900000000"
              pattern="[0-9]{10}"
            >
              <TextFieldSlot side="left">輸入</TextFieldSlot>
            </TextField>
          </form>
        </div>
      </div>

      <main className="flex flex-col gap-[32px] row-start-2 items-center sm:items-start">
        <Image
          className="dark:invert"
          src="/next.svg"
          alt="Next.js logo"
          width={180}
          height={38}
          priority
        />
        <ol className="list-inside list-decimal text-sm/6 text-center sm:text-left font-[family-name:var(--font-geist-mono)]">
          <li className="mb-2 tracking-[-.01em]">
            Get started by editing{" "}
            <code className="bg-black/[.05] dark:bg-white/[.06] px-1 py-0.5 rounded font-[family-name:var(--font-geist-mono)] font-semibold">
              src/app/page.tsx
            </code>
            .
          </li>
          <li className="tracking-[-.01em]">
            Save and see your changes instantly.
          </li>
        </ol>

        <div className="flex gap-4 items-center flex-col sm:flex-row">
          <a
            className="rounded-full border border-solid border-transparent transition-colors flex items-center justify-center bg-foreground text-background gap-2 hover:bg-[#383838] dark:hover:bg-[#ccc] font-medium text-sm sm:text-base h-10 sm:h-12 px-4 sm:px-5 sm:w-auto"
            href="https://vercel.com/new?utm_source=create-next-app&utm_medium=appdir-template-tw&utm_campaign=create-next-app"
            target="_blank"
            rel="noopener noreferrer"
          >
            <Image
              className="dark:invert"
              src="/vercel.svg"
              alt="Vercel logomark"
              width={20}
              height={20}
            />
            Deploy now
          </a>
          <a
            className="rounded-full border border-solid border-black/[.08] dark:border-white/[.145] transition-colors flex items-center justify-center hover:bg-[#f2f2f2] dark:hover:bg-[#1a1a1a] hover:border-transparent font-medium text-sm sm:text-base h-10 sm:h-12 px-4 sm:px-5 w-full sm:w-auto md:w-[158px]"
            href="https://nextjs.org/docs?utm_source=create-next-app&utm_medium=appdir-template-tw&utm_campaign=create-next-app"
            target="_blank"
            rel="noopener noreferrer"
          >
            Read our docs
          </a>
        </div>
      </main>
      <footer className="row-start-3 flex gap-[24px] flex-wrap items-center justify-center">
        <a
          className="flex items-center gap-2 hover:underline hover:underline-offset-4"
          href="https://nextjs.org/learn?utm_source=create-next-app&utm_medium=appdir-template-tw&utm_campaign=create-next-app"
          target="_blank"
          rel="noopener noreferrer"
        >
          <Image
            aria-hidden
            src="/file.svg"
            alt="File icon"
            width={16}
            height={16}
          />
          Learn
        </a>
        <a
          className="flex items-center gap-2 hover:underline hover:underline-offset-4"
          href="https://vercel.com/templates?framework=next.js&utm_source=create-next-app&utm_medium=appdir-template-tw&utm_campaign=create-next-app"
          target="_blank"
          rel="noopener noreferrer"
        >
          <Image
            aria-hidden
            src="/window.svg"
            alt="Window icon"
            width={16}
            height={16}
          />
          Examples
        </a>
        <a
          className="flex items-center gap-2 hover:underline hover:underline-offset-4"
          href="https://nextjs.org?utm_source=create-next-app&utm_medium=appdir-template-tw&utm_campaign=create-next-app"
          target="_blank"
          rel="noopener noreferrer"
        >
          <Image
            aria-hidden
            src="/globe.svg"
            alt="Globe icon"
            width={16}
            height={16}
          />
          Go to nextjs.org →
        </a>
      </footer>
    </div>
  );
}
