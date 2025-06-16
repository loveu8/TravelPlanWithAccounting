import { RadixColorName } from "@/app/components/types";

export interface ISelectItemProps {
  value: string;
  label: string;
}

export type SelectSize = "1" | "2" | "3";

export interface ISelectProps {
  items: ISelectItemProps[];
  className?: string;
  size?: SelectSize;
  children?: React.ReactNode;
  variant?: "soft" | "classic" | "surface";
  placeholder?: string;
  label?: string;
  contentBg?: RadixColorName;
  triggerBg?: string;
  placeholderColor?: string;
}
