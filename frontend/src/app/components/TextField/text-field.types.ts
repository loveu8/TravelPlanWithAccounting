import { RootProps, SlotProps } from "@radix-ui/themes/components/text-field";

export default interface ITextFieldProps extends RootProps {
  size?: "1" | "2" | "3";
  radius?: "small" | "medium" | "large";
  label?: string;
  className?: string;
}

export type ITextFieldSlotProps = SlotProps;
