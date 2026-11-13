
package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CSVtoJSONConverter {

    public static void main(String[] args) {

        // STEP 1: Define file paths
        Path input = Path.of("products.csv");
        Path output = Path.of("products.json");
        Path errorLog = Path.of("errors.log");

        List<String> jsonRecords = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try {
            // STEP 2: Read CSV file
            List<String> lines = Files.readAllLines(input);

            // Skip the header row
            for (int i = 1; i < lines.size(); i++) {

                String line = lines.get(i);
                String[] data = line.split(",", -1);

                // STEP 3: Validate column count
                if (data.length != 3) {
                    errors.add("Line " + (i + 1)
                            + ": Invalid CSV format");
                    continue;
                }

                try {
                    int id = Integer.parseInt(
                            data[0].trim()
                    );

                    String name = data[1].trim();

                    int price = Integer.parseInt(
                            data[2].trim()
                    );

                    // STEP 4: Validate product details
                    if (id <= 0 || name.isEmpty()
                            || price < 0) {

                        errors.add("Line " + (i + 1)
                                + ": Invalid product data");
                        continue;
                    }

                    // STEP 5: Convert record to JSON
                    String json = String.format(
                            "  {\"id\":%d,\"name\":\"%s\",\"price\":%d}",
                            id, escapeJson(name), price
                    );

                    jsonRecords.add(json);

                } catch (NumberFormatException e) {
                    errors.add("Line " + (i + 1)
                            + ": Invalid number");
                }
            }

            // STEP 6: Write JSON output
            String jsonOutput = "[\n"
                    + String.join(",\n", jsonRecords)
                    + "\n]";

            Files.writeString(output, jsonOutput);

            // STEP 7: Save errors with date and time
            List<String> logLines = new ArrayList<>();

            for (String error : errors) {
                logLines.add(LocalDateTime.now()
                        + " - " + error);
            }

            Files.write(errorLog, logLines);

            // STEP 8: Display results
            System.out.println("CSV to JSON Conversion Completed!");
            System.out.println("Valid Records: "
                    + jsonRecords.size());
            System.out.println("Invalid Records: "
                    + errors.size());
            System.out.println("Output: products.json");
            System.out.println("Error Log: errors.log");

        } catch (IOException e) {
            System.out.println("File Error: "
                    + e.getMessage());
        }
    }

    // Escape special characters in JSON strings
    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}

