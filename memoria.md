# memoria.md — Controle da Migração Keycloak → Spring Authorization Server

---

## Papel do Agente
- **Professor e guia técnico** — nunca executor
- Fornece snippets completos prontos para copiar e colar
- Explica o "porquê" antes do "como"
- Avança para a próxima fase apenas quando o usuário confirmar que a verificação passou
- Atualiza este arquivo após cada etapa concluída

## Papel do Usuário
- **Executor único** de todas as alterações no projeto
- O agente não edita, deleta nem cria arquivos do projeto (exceto este `memoria.md`)
- Confirmações e resultados de verificação devem ser reportados pelo usuário

---

## Progresso

### ✅ Atividade 0 — Criar memoria.md
- [x] Arquivo criado e visível no projeto

### ✅ Fase 1 — PostgreSQL
- [x] Passo 1: `compose.yaml` — serviço `keycloak` removido, serviço `postgres:16` adicionado
- [x] Passo 2 (parcial): `pom.xml` — adicionados `spring-boot-starter-oauth2-authorization-server`, `postgresql` (runtime), H2 movido para `test`
  - ⚠️ `keycloak-admin-client` mantido por ora — remover apenas na Fase 4, após deletar o adapter
- [x] Passo 3: `application.yaml` — seção `keycloak.*` removida, datasource PostgreSQL configurado, URLs explícitas (sem `issuer-uri`) para evitar OIDC discovery no startup
- [x] Passo 4: `AppUser.java` criado em `domain/model`
- [x] Passo 5: `AppUserRepository.java` criado em `infrastructure/adapters/out/persistence`
- [x] Passo 6: `data.sql` criado com admin e respondent
- [x] Verificação: banco e tabelas subiram corretamente (`app_users`, `app_user_roles`)

### ⬜ Fase 2 — Spring Authorization Server (Passos 7–9)
- [ ] Passo 7: Criar `AuthorizationServerConfig.java`
- [ ] Passo 8: Criar `AppUserDetailsService.java`
- [ ] Passo 9: Customizar claims JWT

### ✅ Fase 3 — SecurityConfig do BFF (Passos 10–11)
- [x] Passo 10: `SecurityConfig.java` com `@Order(2)`, `formLogin`, novo `JwtAuthenticationConverter` (lista plana), `rolesAuthoritiesMapper`, `hasRole("ADMINISTRATOR")`
- [x] Passo 11: `application.yaml` com provider local e URLs explícitas
- [x] Verificação: redirect → `/login` → login → `/api/me` com `roles: ["ADMINISTRATOR"]`

### ✅ Fase 4 — Novo Adapter de Provisionamento (Passos 12–14)
- [x] Passo 12: `SpringUserIdentityProvisioningAdapter.java` criado + `PasswordEncoder` em `BeanConfig.java`
- [x] Passo 13: Exceções genéricas criadas (`UserProvisioningException`, `UserEmailConflictException`, `GroupNameConflictException`)
- [x] Passo 14: `KeycloakUserIdentityProvisioningAdapter.java` e exceções Keycloak deletados; `keycloak-admin-client` removido do `pom.xml`
- [x] Verificação: `POST /api/respondents` → usuário criado no PostgreSQL

### ✅ Fase 5 — Limpeza de Configurações (Passos 15–18)
- [x] Passo 15: `KeycloakConfig.java` deletado
- [x] Passo 16: `OAuth2ClientManagerConfig.java` deletado
- [x] Passo 17: `OpenApiConfig.java` atualizado com URLs do SAS local
- [x] Passo 18: `AuthenticatedUserResolver.java` já estava correto (nenhuma alteração necessária)
- [x] Verificação: Swagger UI → Authorize → fluxo OAuth2 funciona contra SAS local

### ✅ Fase 6 — Form de Login Customizável (Passos 19–20)
- [x] Passo 19: `login.html` (Thymeleaf) criado em `templates/`
- [x] Passo 20: `SecurityConfig.formLogin` configurado com `loginPage("/login")`
- [x] Pré-requisito: `spring-boot-starter-thymeleaf` adicionado ao `pom.xml`
- [x] Verificação: `http://localhost:8080/login` exibe form customizado

### ⬜ Fase 7 — Testes (Passos 21–23)
- [ ] Passo 19: Criar `login.html` (Thymeleaf)
- [ ] Passo 20: Configurar SAS para usar página customizada

### ⬜ Fase 7 — Testes (Passos 21–23)
- [ ] Passo 21: `application.yaml` (test) — remover Keycloak, configurar H2
- [ ] Passo 22: Atualizar `SecurityIntegrationTest.java`
- [ ] Passo 23: Revisar `GroupControllerIntegrationTest` e `QuestionnaireControllerIntegrationTest`

---

## Fase Atual
**FASE 7 — Testes**

---

## Correções e Observações Técnicas

| # | Observação |
|---|-----------|
| 1 | `keycloak-admin-client` NÃO foi removido no Passo 2 — remover apenas no Passo 14 |
| 2 | `spring.jpa.defer-datasource-initialization: true` é obrigatório com `ddl-auto: create` + `data.sql` |
| 3 | `issuer-uri` causa OIDC discovery HTTP no startup (chicken-and-egg). Usar URLs explícitas (`authorization-uri`, `token-uri`, `jwk-set-uri`) |
| 4 | `OAuth2ClientManagerConfig` será deletado na Fase 5 — bean `authorizedClientService` deixará de existir depois disso |
