interface ButtonProps {
    children: React.ReactNode;
    onClick?: () => void;
    variant?: "primary" | "secondary" | "danger";
    type?: "button" | "submit";
    disabled?: boolean;
    className?: string;
}

export const Button = ({
                           children,
                           onClick,
                           variant = "primary",
                           type = "button",
                           disabled = false,
                           className = "",
                       }: ButtonProps) => {
    const baseStyles = "px-4 py-2 rounded-lg font-medium transition-colors flex items-center justify-center";

    const variants = {
        primary: "bg-blue-600 hover:bg-blue-700 text-white",
        secondary: "bg-gray-200 hover:bg-gray-300 text-gray-800",
        danger: "bg-red-600 hover:bg-red-700 text-white",
    };

    return (
        <button
            type={type}
            onClick={onClick}
            disabled={disabled}
            className={`${baseStyles} ${variants[variant]} ${className} ${
                disabled ? "opacity-50 cursor-not-allowed" : ""
            }`}
        >
            {children}
        </button>
    );
};