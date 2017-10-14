package io.github.jazorp;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@RunWith(SpringRunner.class)
@WebMvcTest(SimpleController.class)
public class SimpleControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void username_cannot_be_blank() throws Exception {
		String content = "{\"username\":\"\"}";
		mockMvc.perform(postJson(content)).andExpect(status().is(UNPROCESSABLE_ENTITY.value()))
				.andExpect(content().string(containsString("username cannot be blank")));

	}

	@Test
	public void password_at_least_six_characters() throws Exception {
		String content = "{\"username\":\"foo123\", \"password\":\"123\"}";
		mockMvc.perform(postJson(content)).andExpect(status().is(UNPROCESSABLE_ENTITY.value()))
				.andExpect(content().string(containsString("password must have at least 6 characters")));

	}

	@Test
	public void valid_user() throws Exception {
		String content = "{\"username\":\"foo123\", \"password\":\"123456\"}";
		mockMvc.perform(postJson(content)).andExpect(status().is(NO_CONTENT.value()))
				.andExpect(content().string(containsString("User details are valid")));

	}

	private MockHttpServletRequestBuilder postJson(String content) {
		return post("").contentType(APPLICATION_JSON).content(content);
	}

}
