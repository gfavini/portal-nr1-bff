package br.com.portal_nr1.infrastructure.adapters.in.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class QuestionnaireControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc buildMockMvc() {
        return MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldCreateQuestionnaireVersionWithCreatedStatus() throws Exception {
        String payload = """
            {
              "title": "Questionario NR1 2026",
              "description": "Versao atualizada para auditoria anual.",
              "questions": [
                {
                  "id": "q-01",
                  "affirmation": "A empresa possui programa formal de capacitacao?",
                  "pillar": "Governanca",
                  "subPillar": "Treinamento",
                  "required": true
                }
              ]
            }
            """;

        buildMockMvc().perform(post("/api/questionnaires/versions")
                .with(csrf())
                .with(oidcLogin().idToken(token -> token
                        .claim("email", "admin@portal.com")
                        .claim("preferred_username", "admin.portal")
                        .claim("roles", List.of("ADMINISTRATOR"))))
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.version").value(1))
            .andExpect(jsonPath("$.createdBy").value("admin.portal"))
            .andExpect(jsonPath("$.title").value("Questionario NR1 2026"));
    }

    @Test
    void shouldReturnNotFoundWhenQuestionnaireVersionDoesNotExist() throws Exception {
        buildMockMvc().perform(get("/api/questionnaires/99")
                .with(oidcLogin().idToken(token -> token
                        .claim("email", "admin@portal.com")
                        .claim("preferred_username", "admin.portal")
                        .claim("roles", List.of("ADMINISTRATOR")))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.message").value("Questionario da versao 99 nao encontrado"))
            .andExpect(jsonPath("$.path").value("/api/questionnaires/99"));
    }
}