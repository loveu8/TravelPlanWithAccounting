import { RootProps, SlotProps } from "@radix-ui/themes/components/text-field";

export default interface ITextFieldProps extends RootProps {
  size?: "1" | "2" | "3";
  radius?: "small" | "medium" | "large";
  label?: string;
  className?: string;
  ref?: React.Ref<HTMLInputElement>;
}

export type ITextFieldSlotProps = SlotProps;
