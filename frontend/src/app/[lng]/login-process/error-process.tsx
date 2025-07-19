import { Text } from "@radix-ui/themes";

const ErrorProcess = ({
  error,
  buttonText,
  onClick,
}: {
  error?: string;
  buttonText?: string | React.ReactNode;
  onClick?: (
    e:
      | React.MouseEvent<HTMLButtonElement>
      | React.TouchEvent<HTMLButtonElement>,
  ) => void;
}) => {
  if (!error && !buttonText) {
    return null;
  }
  return (
    <>
      {error && (
        <Text asChild size="2" align="center" my="4" className="text-red-500">
          <p>{error}</p>
        </Text>
      )}
      {buttonText && (
        <Text asChild size="2" align="center" mt="4">
          <button
            className="mx-auto block text-neutral-500 hover:text-neutral-700 hover:underline cursor-pointer"
            onClick={onClick}
          >
            {buttonText}
          </button>
        </Text>
      )}
    </>
  );
};

export default ErrorProcess;
