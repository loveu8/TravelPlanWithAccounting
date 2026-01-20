import { useId } from "react";
import { Root, Slot } from "@radix-ui/themes/components/text-field";
import { Text } from "@radix-ui/themes";
import { cn } from "@/app/lib/utils";
import ITextFieldProps from "./text-field.types";

function TextField({
  size = "3",
  label,
  className,
  errMsg,
  ref,
  ...props
}: ITextFieldProps) {
  const generatedId = useId();
  const id = props.id || generatedId;

  return (
    <div className="w-full">
      {label && (
        <Text asChild size={size}>
          <label htmlFor={id}>{label}</label>
        </Text>
      )}
      <Root
        my="1"
        size={size}
        id={id}
        ref={ref}
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

export default TextField;
export { Slot as TextFieldSlot };
