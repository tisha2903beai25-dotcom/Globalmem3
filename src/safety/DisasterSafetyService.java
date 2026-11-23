package safety;

import exceptions.ContaminationException;
import exceptions.SignalLostException;
import exceptions.SafetyViolationException;

public class DisasterSafetyService {

    private HazardDetector detector;
    private SafetyAuditLogger logger;

    // Constructor
    public DisasterSafetyService(
            HazardDetector detector,
            SafetyAuditLogger logger) {

        this.detector = detector;
        this.logger = logger;
    }

    // Generic validation method
    public <T> T validateInput(T value, String fieldName) {

        if (value == null) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be null");
        }

        return value;
    }

    // Inspect a disaster zone
    public void inspectZone(
            int row,
            int col,
            boolean contaminated,
            boolean authorized,
            boolean protectiveGear)
            throws ContaminationException,
                   SignalLostException,
                   SafetyViolationException {

        HazardLevel level = detector.getHazardAt(row, col);

        // Check contamination
        if (contaminated) {

            logger.submitEvent("CONTAMINATION detected at "
                    + row + "," + col);

            throw new ContaminationException(
                    "Zone is contaminated!");
        }

        // Check critical hazard
        if (level == HazardLevel.CRITICAL) {

            logger.submitEvent("CRITICAL hazard at "
                    + row + "," + col);

            throw new SignalLostException(
                    "Communication safety alert: Critical zone!");
        }

        // Check high-risk safety requirements
        if (level == HazardLevel.HIGH
                && (!authorized || !protectiveGear)) {

            logger.submitEvent("SAFETY VIOLATION at "
                    + row + "," + col);

            throw new SafetyViolationException(
                    "Authorization or protective gear missing!");
        }

        // Successful inspection
        logger.submitEvent("Zone inspected successfully at "
                + row + "," + col);

        System.out.println("Zone inspection successful: " + level);
    }

    // Display high-risk zones
    public void displayHighRiskZones() {

        System.out.println("\n--- HIGH RISK ZONES ---");

        for (String zone : detector.getHighRiskZones()) {
            System.out.println(zone);
        }
    }

    // Display critical zone count
    public void displayCriticalCount() {

        System.out.println(
                "Total Critical Zones: "
                + detector.countCriticalZones());
    }
}