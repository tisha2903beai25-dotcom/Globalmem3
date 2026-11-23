package testing;

import safety.*;
import java.util.*;


public class SafetyTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // int[][] grid = {
        //     {0, 1, 2},
        //     {3, 4, 1},
        //     {0, 2, 3}
        // };
        System.out.println("Enter number of rows:");
        int rows = sc.nextInt();

        System.out.println("Enter number of columns:");
        int cols = sc.nextInt();

        int[][] grid = new int[rows][cols];

        System.out.println("Enter hazard values (0-4):");

for (int i = 0; i < rows; i++) {
    for (int j = 0; j < cols; j++) {
        grid[i][j] = sc.nextInt();
    }
}

        HazardDetector detector = new HazardDetector(grid);

        System.out.println("===== SAFETY MODULE TEST =====");

        System.out.println("\nAll Hazards:");
        for (String hazard : detector.getAllHazards()) {
            System.out.println(hazard);
        }

        System.out.println("\nHigh Risk Zones:");
        for (String zone : detector.getHighRiskZones()) {
            System.out.println(zone);
        }

        System.out.println("\nCritical Zones: "
                + detector.countCriticalZones());

        System.out.println("\nHazard at (1,1): "
                + detector.getHazardAt(1, 1));
    }
}