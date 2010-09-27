package models;

/**
 * Created by IntelliJ IDEA.
 * User: rckenned
 * Date: Sep 26, 2010
 * Time: 5:08:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class TheaterIdentifier {
    private String id;
    private Theater theater;

    public TheaterIdentifier(String id, Theater theater) {
        this.id = id;
        this.theater = theater;
    }

    public String getId() {
        return id;
    }

    public Theater getTheater() {
        return theater;
    }
}
