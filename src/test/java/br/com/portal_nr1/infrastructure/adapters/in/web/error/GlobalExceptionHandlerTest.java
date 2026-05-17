package br.com.portal_nr1.infrastructure.adapters.in.web.error;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.portal_nr1.application.exception.GroupAlreadyExistsException;
import br.com.portal_nr1.application.exception.QuestionnaireNotFoundException;

class GlobalExceptionHandlerTest {

	@Test
	void shouldReturnNotFoundWhenQuestionnaireIsMissingForGroupCreation() throws Exception {
		MockMvc mockMvc = MockMvcBuilders
			.standaloneSetup(new ThrowingController())
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();

		mockMvc.perform(get("/test/questionnaire-not-found"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value(404))
			.andExpect(jsonPath("$.message").value("No questionnaire available to assign to the group."))
			.andExpect(jsonPath("$.path").value("/test/questionnaire-not-found"));
	}

	@Test
	void shouldReturnConflictWhenGroupAlreadyExistsInIdentityProvider() throws Exception {
		MockMvc mockMvc = MockMvcBuilders
			.standaloneSetup(new ThrowingController())
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();

		mockMvc.perform(get("/test/group-already-exists"))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.status").value(409))
			.andExpect(jsonPath("$.message").value("Group already exists in the identity provider."))
			.andExpect(jsonPath("$.path").value("/test/group-already-exists"));
	}

	@RestController
	private static class ThrowingController {

		@GetMapping("/test/questionnaire-not-found")
		void fail() {
			throw new QuestionnaireNotFoundException("No questionnaire available to assign to the group.");
		}

		@GetMapping("/test/group-already-exists")
		void failWhenGroupAlreadyExists() {
			throw new GroupAlreadyExistsException("Group already exists in the identity provider.");
		}
	}
}