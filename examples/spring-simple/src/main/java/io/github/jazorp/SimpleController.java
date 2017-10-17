package io.github.jazorp;

import static io.github.jazorp.Validators.minLength;
import static io.github.jazorp.Validators.notBlank;
import static io.github.jazorp.Validators.notNull;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@SpringBootApplication
public class SimpleController {

	public static class User {

		private String username;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		private String password;

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

	}

	private static Validator<User> userValidator = new Validator<User>() {
		@Override
		public Aggregation collect(User user) {
			return aggregate(
					notNull("username", user.getUsername()), 
					notBlank("username", user.getUsername()),
					minLength("username", user.getUsername(), 5),
					minLength("password", user.getPassword(), 6));
		}
	};

	@RequestMapping(value = "/", method = POST)
	ResponseEntity<String> validateUser(@RequestBody User user) {
		HttpHeaders responseHeaders = new HttpHeaders();
		Result result = userValidator.validate(user);
		return result.isValid() ? new ResponseEntity<>("User details are valid", responseHeaders, NO_CONTENT)
				: new ResponseEntity<>(result.getErrors().toString(), responseHeaders, UNPROCESSABLE_ENTITY);
	}

	public static void main(String[] args) {
		SpringApplication.run(SimpleController.class, args);
	}
}
