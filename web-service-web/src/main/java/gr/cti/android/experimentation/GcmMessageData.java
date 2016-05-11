package gr.cti.android.experimentation;

/**
 * Created by amaxilatis on 2/11/2015.
 */
public class GcmMessageData {
    private String type;
    private Integer count;
    private String text;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
