package br.com.portal_nr1.infrastructure.adapters.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.portal_nr1.application.exception.GroupAlreadyExistsException;
import br.com.portal_nr1.application.ports.in.FetchGroupsUseCase;
import br.com.portal_nr1.application.ports.in.ProvisionGroupUseCase;
import br.com.portal_nr1.application.ports.in.UpdateGroupUseCase;
import br.com.portal_nr1.domain.model.Group;
import br.com.portal_nr1.domain.model.GroupStatus;
import br.com.portal_nr1.infrastructure.adapters.in.web.dto.GroupRequestItem;

@SpringBootTest
class GroupControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private ProvisionGroupUseCase provisionGroupUseCase;

    @MockitoBean
    private UpdateGroupUseCase updateGroupUseCase;

    @MockitoBean
    private FetchGroupsUseCase fetchGroupsUseCase;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    // ── createGroup ──────────────────────────────────────────────────────────

    @Test
    void shouldCreateGroupSuccessfully() throws Exception {
        GroupRequestItem request = new GroupRequestItem(
                "Grupo A", "q-id-123", 1,
                Instant.now().plus(30, ChronoUnit.DAYS), "OPEN");

        when(provisionGroupUseCase.create(any())).thenReturn("new-group-id");

        mockMvc.perform(post("/api/groups/")
                .with(csrf())
                .with(oidcLogin().idToken(token -> token
                        .claim("email", "admin@portal.com")
                        .claim("preferred_username", "admin.portal")
                        .claim("realm_access", java.util.Map.of("roles", java.util.List.of("ADMIN")))
                        .claim("groups", java.util.List.of("group1"))))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("new-group-id"));
    }

    @Test
    void shouldReturn401WhenCreateGroupWithoutToken() throws Exception {
        GroupRequestItem request = new GroupRequestItem(
                "Grupo A", "q-id-123", 1,
                Instant.now().plus(30, ChronoUnit.DAYS), "OPEN");

        mockMvc.perform(post("/api/groups/")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400WhenCreateGroupWithNullExpiresAt() throws Exception {
        String body = """
                {
                    "name": "Grupo A",
                    "assignedQuestionnaireId": "q-id-123",
                    "assignedQuestionnaireVersion": 1,
                    "expiresAt": null,
                    "status": "OPEN"
                }
                """;

        mockMvc.perform(post("/api/groups/")
                .with(csrf())
                .with(oidcLogin().idToken(token -> token
                        .claim("email", "admin@portal.com")
                        .claim("preferred_username", "admin.portal")
                        .claim("realm_access", java.util.Map.of("roles", java.util.List.of("ADMIN")))
                        .claim("groups", java.util.List.of("group1"))))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldReturn400WhenCreateGroupWithPastExpiresAt() throws Exception {
        GroupRequestItem request = new GroupRequestItem(
                "Grupo A", "q-id-123", 1,
                Instant.now().minus(1, ChronoUnit.DAYS), "OPEN");

        mockMvc.perform(post("/api/groups/")
                .with(csrf())
                .with(oidcLogin().idToken(token -> token
                        .claim("email", "admin@portal.com")
                        .claim("preferred_username", "admin.portal")
                        .claim("realm_access", java.util.Map.of("roles", java.util.List.of("ADMIN")))
                        .claim("groups", java.util.List.of("group1"))))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldReturn409WhenCreateGroupAndGroupAlreadyExists() throws Exception {
        GroupRequestItem request = new GroupRequestItem(
                "Grupo A", "q-id-123", 1,
                Instant.now().plus(30, ChronoUnit.DAYS), "OPEN");

        when(provisionGroupUseCase.create(any()))
                .thenThrow(new GroupAlreadyExistsException("Group already exists in the identity provider."));

        mockMvc.perform(post("/api/groups/")
                .with(csrf())
                .with(oidcLogin().idToken(token -> token
                        .claim("email", "admin@portal.com")
                        .claim("preferred_username", "admin.portal")
                        .claim("realm_access", java.util.Map.of("roles", java.util.List.of("ADMIN")))
                        .claim("groups", java.util.List.of("group1"))))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Group already exists in the identity provider."));
    }

    // ── updateGroup ──────────────────────────────────────────────────────────

    @Test
    void shouldUpdateGroupSuccessfully() throws Exception {
        GroupRequestItem request = new GroupRequestItem(
                "Grupo A Updated", "q-id-456", 2,
                Instant.now().plus(30, ChronoUnit.DAYS), "OPEN");

        Instant expiresAt = Instant.now().plus(30, ChronoUnit.DAYS);
        Group updated = new Group("group-id-1", "Grupo A Updated", 0, new ArrayList<>(),
                "q-id-456", 2, GroupStatus.OPEN, expiresAt, null, null);

        when(updateGroupUseCase.update(eq("group-id-1"), any())).thenReturn(updated);

        mockMvc.perform(put("/api/groups/group-id-1")
                .with(csrf())
                .with(oidcLogin().idToken(token -> token
                        .claim("email", "admin@portal.com")
                        .claim("preferred_username", "admin.portal")
                        .claim("realm_access", java.util.Map.of("roles", java.util.List.of("ADMIN")))
                        .claim("groups", java.util.List.of("group1"))))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("group-id-1"))
                .andExpect(jsonPath("$.name").value("Grupo A Updated"))
                .andExpect(jsonPath("$.assignedQuestionnaireId").value("q-id-456"));
    }

    @Test
    void shouldReturn401WhenUpdateGroupWithoutToken() throws Exception {
        GroupRequestItem request = new GroupRequestItem(
                "Grupo A Updated", "q-id-456", 2,
                Instant.now().plus(30, ChronoUnit.DAYS), "OPEN");

        mockMvc.perform(put("/api/groups/group-id-1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn409WhenUpdateGroupAndGroupNameConflicts() throws Exception {
        GroupRequestItem request = new GroupRequestItem(
                "Grupo A Updated", "q-id-456", 2,
                Instant.now().plus(30, ChronoUnit.DAYS), "OPEN");

        when(updateGroupUseCase.update(eq("group-id-1"), any()))
                .thenThrow(new GroupAlreadyExistsException(
                        "Another group with the same name already exists in the identity provider."));

        mockMvc.perform(put("/api/groups/group-id-1")
                .with(csrf())
                .with(oidcLogin().idToken(token -> token
                        .claim("email", "admin@portal.com")
                        .claim("preferred_username", "admin.portal")
                        .claim("realm_access", java.util.Map.of("roles", java.util.List.of("ADMIN")))
                        .claim("groups", java.util.List.of("group1"))))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("Another group with the same name already exists in the identity provider."));
    }
}