import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public class JsonToCsvConverter {

    public static String convertJsonToCsv(String jsonResponseBody, String outputFilePath) throws IOException {
        // Parse JSON array into a list of maps
        List<Map<String, String>> jsonData = parseJsonArray(jsonResponseBody);

        // Use OpenCSV to write data to CSV string
        StringWriter csvData = new StringWriter();
        try (CSVWriter csvWriter = new CSVWriter(csvData, ',', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
            // Write headers
            String[] headers = jsonData.get(0).keySet().toArray(new String[0]);
            csvWriter.writeNext(headers);

            // Write data
            for (Map<String, String> entry : jsonData) {
                String[] line = entry.values().toArray(new String[0]);
                csvWriter.writeNext(line);
            }
        }

        // Write CSV data to a file
        try (Writer fileWriter = new FileWriter(outputFilePath)) {
            fileWriter.write(csvData.toString());
        }

        return csvData.toString();
    }

    private static List<Map<String, String>> parseJsonArray(String jsonArray) throws IOException {
        // Parse JSON array into a list of maps
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonArray, new TypeReference<List<Map<String, String>>>() {});
    }
}