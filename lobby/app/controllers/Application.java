package controllers;

import models.Lobby;
import models.Theater;
import netflix.Client;
import netflix.TitleTheater;
import netflix.User;
import oauth.signpost.OAuth;
import oauth.signpost.exception.OAuthException;
import org.apache.http.client.methods.HttpGet;
import org.codehaus.jackson.JsonNode;
import play.Play;
import play.mvc.*;

import java.io.IOException;
import java.net.URLEncoder;

public class Application extends Controller {
    private static final String TITLE_SEARCH_URL_TEMPLATE = "http://api.netflix.com/catalog/titles?v=2.0&output=json&term=%s&max_results=1&filters=http://api.netflix.com/categories/title_formats/instant";

    private static final String ck = Play.configuration.getProperty("nfapibrowser.ck");
    private static final String cks = Play.configuration.getProperty("nfapibrowser.cks");
    private static final String appName = Play.configuration.getProperty("nfapibrowser.application_name");

    private static final Client client = new Client(ck, cks, appName);
    private static final Lobby lobby = new Lobby();

    public static void index() throws Exception {
        User user = getSessionUser();
        boolean loggedIn = user != null;

        if (!loggedIn) {
            render();
        } else {
            redirect("Application.lobby");
        }
    }

    public static void lobby() throws ClassNotFoundException, IOException, OAuthException {
        User user = getSessionUser();
        Lobby lobby = Application.lobby;
        render(user, lobby);
    }

    public static void authorize() throws Exception {
        redirect(client.getAuthorizationUrl(Router.getFullUrl("Application.complete")));
    }

    public static void complete(String oauth_token, String oauth_verifier) throws Exception {
        User user = client.completeAuthorization(oauth_token, oauth_verifier);
        sessionUser(user);
        redirect("Application.lobby");
    }

    public static void browse(String urlString) {
        if (urlString == null) {
            render();
        }

        try {
            urlString = OAuth.addQueryParameters(urlString, "output", "json");
            User user = getSessionUser();
            JsonNode node = user.executeAndParseRequest(new HttpGet(urlString), JsonNode.class);

            renderJSON(node.toString());
        } catch (Exception e) {
            error(e);
        }
    }

    public static void add(String query) throws Exception {
        HttpGet get = new HttpGet(String.format(TITLE_SEARCH_URL_TEMPLATE, URLEncoder.encode(query, "utf-8")));
        JsonNode response = client.executeAndParseRequest(get, JsonNode.class);
        if (response.get("number_of_results").getIntValue() <= 0) {
            error("No results");
        } else {
            final JsonNode title = response.get("catalog").get(0);

            lobby.addTheater(new TitleTheater(client.getTitle(title.get("id").getValueAsText())));

            redirect("Application.index");
        }
    }

    private static void sessionUser(User user) throws IOException {
        session.put("netflix_user", user.serialize());
    }

    private static User getSessionUser() throws IOException, ClassNotFoundException, OAuthException {
        if (session.contains("netflix_user")) {
            return User.deserialize(session.get("netflix_user"));
        }

        return null;
    }
}
