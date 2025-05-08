import { Root, Slot } from "@radix-ui/themes/components/text-field";
import { Text } from "@radix-ui/themes";
import ITextFieldProps from "./text-field.types";

function TextField({ size = "3", label, ...props }: ITextFieldProps) {
  return (
    <>
      {label && (
        <Text asChild size={size}>
          <label htmlFor={props.id}>{label}</label>
        </Text>
      )}
      <Root my="1" size={size} {...props} />
    </>
  );
}

export default TextField;
export { Slot as TextFieldSlot };
