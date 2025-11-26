"use client";
import React from "react";
import { useRouter } from "next/navigation";
import { Section, Grid, Box, Heading } from "@radix-ui/themes";
import Button from "@/app/components/Button";

interface CardSectionProps<T> {
  title: string;
  data: Partial<T>[];
  CardComponent: React.ComponentType<T>;
  buttonText?: string;
  viewMoreUrl?: string;
  handleCardClick?: (id: string, title: string) => void;
  handleBookmarkClick?: (id: string) => void;
}

export default function CardSection<T>({
  title,
  data,
  CardComponent,
  buttonText,
  viewMoreUrl,
  handleCardClick,
  handleBookmarkClick,
}: CardSectionProps<T>) {
  const router = useRouter();
  return (
    <Section maxWidth="1440px" mx="auto" size="2" className="px-8">
      <Heading mb="4" as="h6">
        {title}
      </Heading>
      <Grid columns="4" gap="6" width="auto">
        {data.map((item, index) => {
          const props = {
            ...item,
            handleCardClick,
            handleBookmarkClick,
          } as T;
          return <CardComponent key={index} {...props} />;
        })}
      </Grid>
      {buttonText && (
        <Box as="div" mt="6" className="text-center">
          <Button
            text={buttonText}
            size="3"
            isMain={false}
            className="mt-6 mx-auto font-bold"
            handleClick={
              viewMoreUrl ? () => router.push(viewMoreUrl) : undefined
            }
          />
        </Box>
      )}
    </Section>
  );
}
