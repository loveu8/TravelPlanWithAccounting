"use client";
import { useState, useEffect, useCallback } from "react";
import { cn } from "@/app/lib/utils";
import TextField from "@/app/components/TextField";
import IAutoCompleteProps from "./autocomplete.types";

export default function AutoComplete({
  getSuggestions,
  ...props
}: IAutoCompleteProps) {
  const [isOpen, setIsOpen] = useState(false);
  const [inputValue, setInputValue] = useState("");
  const [suggestions, setSuggestions] = useState<string[]>([]);
  const [highlightedIndex, setHighlightedIndex] = useState(-1);

  const handleTextChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (!isOpen) setIsOpen(true);
    if (highlightedIndex !== -1) setHighlightedIndex(-1);
    setInputValue(e.target.value);
  };

  const handleTextBlur = () => {
    setIsOpen(false);
    setHighlightedIndex(-1);
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    e.stopPropagation();
    if (!isOpen || suggestions.length === 0) return;

    if (e.key === "ArrowDown") {
      setHighlightedIndex((prevIndex) =>
        prevIndex < suggestions.length - 1 ? prevIndex + 1 : 0,
      );
    }

    if (e.key === "ArrowUp") {
      setHighlightedIndex((prevIndex) =>
        prevIndex > 0 ? prevIndex - 1 : suggestions.length - 1,
      );
    }

    if (e.key === "Enter" && highlightedIndex >= 0) {
      setInputValue(suggestions[highlightedIndex]);
      setIsOpen(false);
    }
  };

  const handleSuggestionClick = (e: React.MouseEvent, suggestion: string) => {
    e.preventDefault();
    setInputValue(suggestion);
    setIsOpen(false);
  };

  const fetchSuggestions = useCallback(
    async (query: string) => {
      try {
        const data = await getSuggestions(query);
        setSuggestions(data);
      } catch (error) {
        console.error("Failed to fetch suggestions:", error);
        setSuggestions([]);
      }
    },
    [getSuggestions],
  );

  // 當輸入值改變時，向後端取資料
  useEffect(() => {
    if (inputValue.trim() === "") {
      setSuggestions([]);
      return;
    }

    const delayDebounceFn = setTimeout(() => {
      fetchSuggestions(inputValue);
    }, 300);

    return () => clearTimeout(delayDebounceFn);
  }, [inputValue, fetchSuggestions]);

  return (
    <TextField
      className="relative"
      value={inputValue}
      onChange={handleTextChange}
      onFocus={() => setIsOpen(true)}
      onBlur={handleTextBlur}
      onKeyUp={handleKeyDown}
      {...props}
    >
      {isOpen && suggestions.length > 0 && (
        <ul className="absolute left-0 top-full border border-gray-7 w-full mt-1 rounded-md bg-white z-10 overflow-y-auto overflow-clip shadow-xs">
          {suggestions.map((suggestion, index) => (
            <li
              key={index}
              className={cn(
                "p-2 cursor-pointer",
                index === highlightedIndex && "bg-gray-2",
              )}
              onMouseEnter={() => setHighlightedIndex(index)}
              onMouseDown={(e) => {
                handleSuggestionClick(e, suggestion);
              }}
            >
              {suggestion}
            </li>
          ))}
        </ul>
      )}
    </TextField>
  );
}
