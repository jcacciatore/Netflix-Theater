package netflix;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.HttpRequestAdapter;
import oauth.signpost.exception.OAuthException;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.util.Map;

public class User {
    private OAuthConsumer consumer;
    private DefaultHttpClient client;
    private UserInfo userInfo;

    private ObjectMapper mapper;

    public User(OAuthConsumer consumer) {
        this.consumer = consumer;
        client = new DefaultHttpClient();
        mapper = new ObjectMapper();
    }

    public UserInfo getUserInfo() throws IOException, OAuthException {
        if (userInfo == null) {
            userInfo = loadUserInfo();
        }
        return userInfo;
    }

    public String serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(consumer);
        oos.flush();
        return new String(Base64.encodeBase64(baos.toByteArray()), "utf-8");
    }

    public static User deserialize(String serialized) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                Base64.decodeBase64(serialized.getBytes())));
        return new User((OAuthConsumer) ois.readObject());
    }

    public <T> T executeAndParseRequest(HttpUriRequest request, Class<T> type) throws OAuthException, IOException {
        consumer.sign(new HttpRequestAdapter(request));
        return mapper.readValue(client.execute(request).getEntity().getContent(), type);
    }

    private UserInfo loadUserInfo() throws IOException, OAuthException {
        Map values = executeAndParseRequest(new HttpGet("http://api.netflix.com/users/current?v=2.0&output=json"),
                Map.class);

        JsonNode node = executeAndParseRequest(new HttpGet(values.get("http://schemas.netflix.com/user.current") + "?v=2.0&output=json"),
                JsonNode.class);
        return mapper.readValue(node.get("user"), UserInfo.class);
    }
}
