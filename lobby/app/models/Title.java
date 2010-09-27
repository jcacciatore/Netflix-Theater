package models;

import oauth.signpost.OAuth;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

public class Title {
    private float averageRating;
    private Map<String, String> boxArt;
    private String webUrl;
    private Map<String, String> title;
    private String releaseYear;
    private String tinyUrl;

    public Title(float averageRating, Map<String, String> boxArt, String webUrl, Map<String, String> title, String releaseYear, String tinyUrl) {
        this.averageRating = averageRating;
        this.boxArt = boxArt;
        this.webUrl = webUrl;
        this.title = title;
        this.releaseYear = releaseYear;
        this.tinyUrl = tinyUrl;
    }

    public float getAverageRating() {
        return averageRating;
    }

    @JsonProperty("average_rating")
    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public Map<String, String> getBoxArt() {
        return boxArt;
    }

    @JsonProperty("box_art")
    public void setBoxArt(Map<String, String> boxArt) {
        this.boxArt = boxArt;
    }

    public String getWebUrl() {
        return webUrl;
    }

    @JsonProperty("web_page")
    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getTitle() {
        return title.get("regular");
    }

    @JsonProperty("title")
    public void setTitle(Map<String, String> title) {
        this.title = title;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    @JsonProperty("release_year")
    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getTinyUrl() {
        return tinyUrl;
    }

    @JsonProperty("tiny_url")
    public void setTinyUrl(String tinyUrl) {
        this.tinyUrl = tinyUrl;
    }

    public String getPlaybackUrl(boolean ignoreBookmark) {
        String url = String.format("http://www.netflix.com/WiPlayer?movieid=%s", getShortMovieId());
        if (ignoreBookmark) {
            url = OAuth.addQueryParameters(url, "ignbk", "true");
        }

        return url;
    }

    private String getShortMovieId() {
        return getWebUrl().substring(getWebUrl().lastIndexOf("/") + 1);
    }

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {

    }
}
