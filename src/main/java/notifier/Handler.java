package notifier;

import static spark.Spark.get;

/**
 * Created by yurii on 28.04.17.
 */
public class Handler {

    public static void main(String[] args) {
        get("/hello", (req, res) -> "Hello World");
    }
}
