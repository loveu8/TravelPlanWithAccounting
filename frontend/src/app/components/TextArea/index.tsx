import { useId } from "react";
import { TextArea as RadixTextArea } from "@radix-ui/themes/components/text-area";
import { Text } from "@radix-ui/themes";
import { cn } from "@/app/lib/utils";
import ITextAreaProps from "./text-area.types";

function TextArea({
  size = "3",
  label,
  className,
  errMsg,
  ...props
}: ITextAreaProps) {
  const generatedId = useId();
  const id = props.id || generatedId;

  return (
    <div>
      {label && (
        <Text asChild size={size}>
          <label htmlFor={id}>{label}</label>
        </Text>
      )}
      <RadixTextArea
        my="1"
        size={size}
        id={id}
        className={cn(className, errMsg && "ring ring-red-500")}
        {...props}
      />
      {errMsg && (
        <Text size="2" color="red" as="p">
          {errMsg}
        </Text>
      )}
    </div>
  );
}

export default TextArea;
