package worder.commons;

public enum OS {
    LINUX,
    WINDOWS_10;


    public static OS getCurrentOS() {
        return switch (System.getProperty("os.name")) {
            case "Windows 10" -> WINDOWS_10;
            case "Linux" -> LINUX;
            default -> throw new IllegalStateException("Unsupported OS detected: " + System.getProperty("os.name"));
        };
    }
}
