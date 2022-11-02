package backend.src.main.java.exceptions;

import org.everit.json.schema.ValidationException;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InvalidCalciteSchemaException extends Exception {
    public InvalidCalciteSchemaException(String message) {
        super(message);
    }

    public InvalidCalciteSchemaException(ValidationException exception) {
        super(
            Stream.concat(
                Stream.of(exception.getMessage() + ":"),
                exception.getCausingExceptions().stream().map(ValidationException::getMessage)
            ).collect(Collectors.joining(System.lineSeparator()))
        );
    }
}