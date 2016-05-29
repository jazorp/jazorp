import io.github.jazorp.Aggregation;
import io.github.jazorp.Result;
import io.github.jazorp.Validator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static io.github.jazorp.Validators.minLength;
import static io.github.jazorp.Validators.notNull;

@Controller
@EnableAutoConfiguration
class SimpleController {

	private static Validator<User> userValidator = new Validator<User>() {

		@Override
		public Aggregation collect(User user) {
			return aggregate(notNull("username", user.getUsername()), minLength("password", user.getPassword(), 6));
		}
	};

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	String validateUser(@RequestBody User user) {
		Result result = userValidator.validate(user);
		return result.isValid() ? "User details are valid" : result.getErrors().toString();
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SimpleController.class, args);
	}
}
