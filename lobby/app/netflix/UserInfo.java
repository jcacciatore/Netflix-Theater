package netflix;

import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonProperty;

public class UserInfo {
    private String userId;
    private String nickname;
    private String firstName;
    private String lastName;
    private String[] preferredFormats;
    private boolean canStream;

    public UserInfo(String userId, String nickname, String firstName, String lastName, String[] preferredFormats, boolean canStream) {
        this.userId = userId;
        this.nickname = nickname;
        this.firstName = firstName;
        this.lastName = lastName;
        this.preferredFormats = preferredFormats;
        this.canStream = canStream;
    }

    public String getUserId() {
        return userId;
    }

    @JsonProperty("user_id")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    @JsonProperty("nickname")
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFirstName() {
        return firstName;
    }

    @JsonProperty("first_name")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @JsonProperty("last_name")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String[] getPreferredFormats() {
        return preferredFormats;
    }

    @JsonProperty("preferred_format")
    public void setPreferredFormats(String[] preferredFormats) {
        this.preferredFormats = preferredFormats;
    }

    public boolean isCanStream() {
        return canStream;
    }

    @JsonProperty("can_instant_watch")
    public void setCanStream(boolean canStream) {
        this.canStream = canStream;
    }

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
        
    }
}
