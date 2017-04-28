package notifier;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import spark.Request;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.SparkBase.port;

/**
 * Created by yurii on 28.04.17.
 */
public class Handler {

    void watch() {
        port(getHerokuAssignedPort());
        get("/", (req, res) -> "Hello World");
        get("/msg", (request, response) -> parseRequestData(request));
        post("msg", (request, response) -> parseRequestData(request));
    }

    private String parseRequestData(Request request) {
        String body = request.body();
        System.out.println(body);
        JsonObject jsonObject = new Gson().fromJson(body, JsonObject.class);
        String state = jsonObject.get("state").toString();
        System.out.println(state);
        return sendMsgToSlack();
    }


    private String sendMsgToSlack() {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        try {
            HttpPost request = new HttpPost(getEnv("SLACK_URL"));
            StringEntity params = new StringEntity(getEnv("PAYLOAD"));
            request.addHeader("content-type", "application/x-www-form-urlencoded");
            request.setEntity(params);
            httpClient.execute(request);

            return "{\"status\": \"ok\"}";
        } catch (Exception ex) {

            return "{\"status\": " + ex + "}";
        }
    }

    private int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4575;
    }

    private String getEnv(String envName) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get(envName) != null) {
            return processBuilder.environment().get(envName);
        }
        return "";
    }
}
