package safety;

import java.util.*;
import java.util.stream.Collectors;

public class HazardDetector {

    private int[][] hazardGrid;

    // 1. Constructor with validation
    public HazardDetector(int[][] grid) {

        if (grid == null || grid.length == 0) {
            throw new IllegalArgumentException("Grid cannot be empty");
        }

        hazardGrid = new int[grid.length][];

        for (int i = 0; i < grid.length; i++) {

            if (grid[i] == null || grid[i].length == 0) {
                throw new IllegalArgumentException("Invalid row");
            }

            hazardGrid[i] = grid[i].clone();

            for (int j = 0; j < grid[i].length; j++) {

                if (grid[i][j] < 0 || grid[i][j] > 4) {
                    throw new IllegalArgumentException("Invalid hazard code");
                }
            }
        }
    }

    // 2. Get hazard at a particular location
    public HazardLevel getHazardAt(int row, int col) {

        if (row < 0 || row >= hazardGrid.length ||
            col < 0 || col >= hazardGrid[row].length) {

            throw new IndexOutOfBoundsException("Invalid zone coordinates");
        }

        return HazardLevel.fromCode(hazardGrid[row][col]);
    }

    // 3. Get all non-safe hazards
    public ArrayList<String> getAllHazards() {

        ArrayList<String> hazards = new ArrayList<>();

        for (int i = 0; i < hazardGrid.length; i++) {

            for (int j = 0; j < hazardGrid[i].length; j++) {

                HazardLevel level = getHazardAt(i, j);

                if (level != HazardLevel.SAFE) {

                    hazards.add(
                        "Row: " + i +
                        ", Column: " + j +
                        ", Hazard: " + level
                    );
                }
            }
        }

        return hazards;
    }

    // 4. Get high-risk zones using Stream API
    public List<String> getHighRiskZones() {

        return getAllHazards()
                .stream()
                .filter(h -> h.contains("HIGH") || h.contains("CRITICAL"))
                .sorted()
                .collect(Collectors.toList());
    }

    // 5. Get top hazards
    public List<String> getTopHazards(int limit) {

        if (limit < 0) {
            throw new IllegalArgumentException("Limit cannot be negative");
        }

        return getAllHazards()
                .stream()
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());
    }

    // 6. Get hazard descriptions using skip and map
    public List<String> getHazardDescriptions(int skipCount, int limit) {

        if (skipCount < 0 || limit < 0) {
            throw new IllegalArgumentException("Invalid skip or limit");
        }

        return getAllHazards()
                .stream()
                .skip(skipCount)
                .limit(limit)
                .map(String::toUpperCase)
                .collect(Collectors.toList());
    }

    // 7. Count critical zones using filter
    public long countCriticalZones() {

        return getAllHazards()
                .stream()
                .filter(h -> h.contains("CRITICAL"))
                .count();
    }
}
