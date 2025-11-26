import ITextFieldProps from "@/app/components/TextField/text-field.types";

export default interface IAutoCompleteProps extends ITextFieldProps {
  getSuggestions: (query: string) => Promise<string[]>;
}
