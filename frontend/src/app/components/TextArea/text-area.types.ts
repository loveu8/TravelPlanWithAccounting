import { TextAreaProps } from "@radix-ui/themes/components/text-area";

export default interface ITextAreaProps extends TextAreaProps {
  label?: string;
  className?: string;
  errMsg?: string;
}
