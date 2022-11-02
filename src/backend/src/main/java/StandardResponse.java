package backend.src.main.java;

import com.google.gson.JsonElement;

public class StandardResponse {
    private StatusResponse status;
    private String message;
    private JsonElement data;

    public StandardResponse(StatusResponse status) {
        this.status = status;
    }

    public StandardResponse(StatusResponse status, String message) {
        this.status = status;
        this.message = message;
    }

    public StandardResponse(StatusResponse status, JsonElement data) {
        this.status = status;
        this.data = data;
    }

    public void setStatus(StatusResponse status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(JsonElement data) {
        this.data = data;
    }

    public StatusResponse getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public JsonElement getData() {
        return data;
    }
}