package safety;

public enum HazardLevel {

    SAFE(0),
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    CRITICAL(4);

    private final int severity;

    HazardLevel(int severity) {
        this.severity = severity;
    }

    public int getSeverity() {
        return severity;
    }

    public static HazardLevel fromCode(int code) {

        for (HazardLevel level : HazardLevel.values()) {

            if (level.getSeverity() == code) {
                return level;
            }
        }

        throw new IllegalArgumentException("Invalid hazard code: " + code);
    }
}