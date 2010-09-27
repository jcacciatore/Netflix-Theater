package netflix;

import models.Title;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.HttpRequestAdapter;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;

public class Client {
    private String consumerKey;
    private String consumerKeySecret;
    private String applicationName;

    private OAuthConsumer twoLeggedConsumer;
    private OAuthProvider provider;

    private HttpClient client;

    private Map<String, OAuthConsumer> requestTokenMap = new WeakHashMap<String, OAuthConsumer>();
    private ObjectMapper mapper;

    public Client(String consumerKey, String consumerKeySecret, String applicationName) {
        this.consumerKey = consumerKey;
        this.consumerKeySecret = consumerKeySecret;
        this.applicationName = applicationName;

        twoLeggedConsumer = new DefaultOAuthConsumer(consumerKey, consumerKeySecret);

        provider = new DefaultOAuthProvider(
            "http://api.netflix.com/oauth/request_token",
            "http://api.netflix.com/oauth/access_token",
            "https://api-user.netflix.com/oauth/login");
        provider.setOAuth10a(true);

        client = new DefaultHttpClient();
        mapper = new ObjectMapper();
    }

    public String getAuthorizationUrl(String doneUrl) throws Exception {
        OAuthConsumer consumer = new DefaultOAuthConsumer(consumerKey, consumerKeySecret);

        String url = provider.retrieveRequestToken(consumer, doneUrl);
        url = OAuth.addQueryParameters(url, OAuth.OAUTH_CONSUMER_KEY, consumer.getConsumerKey(),
                "application_name", applicationName);

        requestTokenMap.put(consumer.getToken(), consumer);

        return url;
    }

    public User completeAuthorization(String requestToken, String verifier) throws Exception {
        if (!requestTokenMap.containsKey(requestToken)) {
            throw new Exception(String.format("I don't know this request token: %s", requestToken));
        }

        try {
            OAuthConsumer consumer = requestTokenMap.get(requestToken);
            provider.retrieveAccessToken(consumer, verifier);

            return new User(consumer);
        } finally {
            requestTokenMap.remove(requestToken);
        }
    }

    public <T> T executeAndParseRequest(HttpUriRequest request, Class<T> type) throws OAuthException, IOException {
        twoLeggedConsumer.sign(new HttpRequestAdapter(request));
        return mapper.readValue(client.execute(request).getEntity().getContent(), type);
    }

    public Title getTitle(String titleId) throws OAuthException, IOException {
        JsonNode node = executeAndParseRequest(new HttpGet(OAuth.addQueryParameters(titleId, "v", "2.0", "output", "json")),
                JsonNode.class);
        return mapper.readValue(node.get("catalog_title"), Title.class);
    }
}
