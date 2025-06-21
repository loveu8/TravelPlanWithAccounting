import { useId } from "react";
import { TextArea as RadixTextArea } from "@radix-ui/themes/components/text-area";
import { Text } from "@radix-ui/themes";
import ITextAreaProps from "./text-area.types";

function TextArea({ size = "3", label, ...props }: ITextAreaProps) {
  const generatedId = useId();
  const id = props.id || generatedId;

  return (
    <>
      {label && (
        <Text asChild size={size}>
          <label htmlFor={id}>{label}</label>
        </Text>
      )}
      <RadixTextArea my="1" size={size} id={id} {...props} />
    </>
  );
}

export default TextArea;
