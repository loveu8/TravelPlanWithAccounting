"use client";
import React from "react";
import TextField from "@/app/components/TextField";

export default function AutoComplete() {
  return (
    <TextField className="relative">
      <ul className="absolute left-0 top-full border border-gray-8 w-full mt-1  rounded-sm bg-white z-10 overflow-y-auto overflow-clip">
        <li className="p-2 hover:bg-gray-2">456</li>
        <li className="p-2 hover:bg-gray-2">456</li>
        <li className="p-2 hover:bg-gray-2">456</li>
        <li className="p-2 hover:bg-gray-2">456</li>
      </ul>
    </TextField>
  );
}
