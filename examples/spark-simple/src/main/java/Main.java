import com.google.gson.Gson;
import io.github.jazorp.Aggregation;
import io.github.jazorp.Validator;

import static io.github.jazorp.Validators.notNull;
import static spark.Spark.halt;
import static spark.Spark.post;

public class Main {

    private static Gson gson = new Gson();
    private static Validator<User> userValidator = new Validator<User>() {

        @Override
        public Aggregation collect(User user) {
            return aggregate(notNull("username", user.getUsername()),
                             notNull("password", user.getPassword()));
        }
    };

    public static void main(String[] args) {
        post("/login", "application/json", (req, res) -> {
            User user = gson.fromJson(req.body(), User.class);
            return userValidator.validate(user).getOrElse(() -> user, e -> e);
        }, gson::toJson);
    }
}
