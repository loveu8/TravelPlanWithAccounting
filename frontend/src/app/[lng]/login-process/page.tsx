import LoginSignup from ".";
import Button from "@/app/components/Button";

/**
 * @todo 這只是 demo 用的頁面，實際上不會有這頁，page.tsx 之後會刪除
 */
export default function Page() {
  return (
    <>
      <LoginSignup>登入</LoginSignup>
      <LoginSignup asChild>
        <Button text="登入"></Button>
      </LoginSignup>
    </>
  );
}
