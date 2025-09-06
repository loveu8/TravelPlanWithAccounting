import Link from "next/link";
import { Box, Button } from "@radix-ui/themes";
import { Text } from "@radix-ui/themes";

export default function LinkButton({
  href,
  icon,
  children,
}: {
  href: string;
  icon: React.ReactNode;
  children: React.ReactNode;
}) {
  return (
    <Link href={href}>
      <Button
        size="4"
        variant="surface"
        color="gray"
        className="cursor-pointer w-full"
        highContrast
      >
        {icon}
        <Text size="3" className="shrink-0">
          {children}
        </Text>
        <Box width="100%" />
      </Button>
    </Link>
  );
}
