import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public class JsonToCsvConverter {

    public static String convertJsonToCsv(String jsonResponseBody, String outputFilePath) throws IOException {
        // Parse JSON into a list of maps
        List<Map<String, String>> jsonData = parseJson(jsonResponseBody);

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

    private static List<Map<String, String>> parseJson(String jsonString) throws IOException {
        // Parse JSON into a list of maps
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        // Check if the JSON is an array or an object
        if (jsonString.startsWith("[")) {
            return objectMapper.readValue(jsonString, new TypeReference<List<Map<String, String>>>() {});
        } else {
            // If it's not an array, create a list with a single map
            Map<String, String> singleEntry = objectMapper.readValue(jsonString, new TypeReference<Map<String, String>>() {});
            return List.of(singleEntry);
        }
    }
}
