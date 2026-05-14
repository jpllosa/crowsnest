package net.codesamples.crowsnest;

public class App {

    private String name;

    private String url;

    private String status;

    private String hint;

    private String awryPayload;

    public App() {}

    public App(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getAwryPayload() {
        return awryPayload;
    }

    public void setAwryPayload(String awryPayload) {
        this.awryPayload = awryPayload;
    }

    @Override
    public String toString() {
        return "App{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", status='" + status + '\'' +
                ", hint='" + hint + '\'' +
                ", awryPayload='" + awryPayload + '\'' +
                '}';
    }
}
