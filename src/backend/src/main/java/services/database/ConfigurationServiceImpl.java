package backend.src.main.java.services.database;

import backend.src.main.java.Server;
import backend.src.main.java.exceptions.InvalidCalciteSchemaException;
import backend.src.main.java.entities.database.Configuration;
import org.apache.commons.io.FilenameUtils;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import spark.Request;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigurationServiceImpl implements ConfigurationService {
    private final File uploadDirectory;

    public ConfigurationServiceImpl() {
        // Create file upload directory
        //this.uploadDirectory = new File("uploads");
        this.uploadDirectory = new File("src/main/resources");
        // noinspection ResultOfMethodCallIgnored
        uploadDirectory.mkdir();
    }

    @Override
    public String createConfiguration(Request req) throws IOException, ServletException, InvalidCalciteSchemaException {
        Path tempFile = Files.createTempFile(uploadDirectory.toPath(), "", ".json");
        req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
        try (InputStream input = req.raw().getPart("file").getInputStream()) {
            Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);

            // Load validation schema
            try (InputStream inputStream = Server.class.getResourceAsStream("../resources/calcite-schema-validation.json")) {
                JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
                Schema schema = SchemaLoader.load(rawSchema);

                // Read uploaded file
                Path filePath = Paths.get(tempFile.toAbsolutePath().toString());
                try (Stream<String> fileLines = Files.lines(filePath)) {
                    String fileData = fileLines.collect(Collectors.joining(System.lineSeparator()));

                    // Validate JSON schema
                    try {
                        schema.validate(new JSONObject(fileData));
                    } catch (JSONException jsonException) {
                        throw new InvalidCalciteSchemaException(jsonException.getMessage());
                    } catch (ValidationException validationException) {
                        throw new InvalidCalciteSchemaException(validationException);
                    }
                }
            }
        }

        return FilenameUtils.removeExtension(tempFile.getFileName().toString());
    }

    @Override
    public Configuration getConfiguration(String id) throws IOException {
        Configuration configuration = new Configuration(id);
        File configurationFile = configuration.getFile();
        if (!configurationFile.exists()) {
            throw new FileNotFoundException("Configuration file " + id + " not found");
        }
        if (!configurationFile.canRead()) {
            throw new AccessDeniedException("Configuration file " + id + " not readable");
        }

        return configuration;
    }

    @Override
    public boolean configurationExist(String id) throws IOException {
        File[] files = uploadDirectory.listFiles();
        if (files == null || files.length < 1) return false;

        return Arrays.stream(files).anyMatch(file -> FilenameUtils.removeExtension(file.getName()).equals(id));
    }
}
