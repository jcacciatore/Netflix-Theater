package netflix;

import models.Theater;
import models.Title;

public class TitleTheater implements Theater {
    private Title title;

    public TitleTheater(Title title) {
        this.title = title;
    }

    public String getName() {
        return title.getTitle();
    }

    public String getLink() {
        return title.getPlaybackUrl(false);
    }

    public String getPreviewImageUrl() {
        return title.getBoxArt().get("large");
    }
}
