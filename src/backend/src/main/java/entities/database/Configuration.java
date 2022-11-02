package backend.src.main.java.entities.database;

import java.io.File;
import java.io.Serializable;

public class Configuration implements Serializable {
    private final String id;

    public Configuration(String id) {
        // Make sure to filter out everything which isn't a word for security
        this.id = id.replaceAll("\\W+", "");
    }

    public String getId() {
        return id;
    }

    public File getFile() {
        return new File("src/main/resources" + File.separatorChar + this.id + ".json");
    }

    public String getFilePath() {
        return this.getFile().getAbsolutePath();
    }

    public String getFileName() {
        return this.getFile().getName();
    }


}