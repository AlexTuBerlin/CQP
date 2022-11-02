package backend.src.main.java.services.database;

import backend.src.main.java.exceptions.InvalidCalciteSchemaException;
import backend.src.main.java.entities.database.Configuration;
import spark.Request;

import javax.servlet.ServletException;
import java.io.IOException;

public interface ConfigurationService {
    String createConfiguration(Request configuration) throws IOException, ServletException, InvalidCalciteSchemaException;

    Configuration getConfiguration(String id) throws IOException;

    boolean configurationExist(String id) throws IOException;
}