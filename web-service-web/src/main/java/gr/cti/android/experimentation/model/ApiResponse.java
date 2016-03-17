package gr.cti.android.experimentation.model;

/**
 * Created by amaxilatis on 19/1/2016.
 */
public class ApiResponse {

    private int status;
    private String message;
    private Object value;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
