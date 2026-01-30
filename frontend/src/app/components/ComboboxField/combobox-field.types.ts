export default interface IComboboxFieldProps<T> {
  size?: "1" | "2" | "3";
  label?: string;
  options: T[];
  value: string[];
  placeholder?: string;
  errMsg?: string;
  onChange: (value: string[]) => void;
  getOptionValue: (option: T) => string; // 取得選項的值
  getOptionLabel: (option: T) => string; //取得選項的標籤
  optionFilter?: (options: T[], value: string) => T[]; // 選項過濾函式
}
