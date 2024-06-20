package Progetto.Gateway;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Mediaquartiere {
    private Double media;
    private String timestamp;

    public Mediaquartiere(){}

    public Mediaquartiere(Double media, String timestamp) {
        this.media = media;
        this.timestamp = timestamp;
    }

    public Double getMedia() {
        return media;
    }

    public void setMedia(Double media) { this.media = media; }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Media: " + getMedia() + ", Timestamp: " + getTimestamp();
    }
}
