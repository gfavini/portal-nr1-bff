## Plan: Substituir Keycloak pelo Spring Authorization Server + PostgreSQL
## Regras obrigatórias de execução

> **Precedência absoluta**: As Regras 1–6 têm precedência sobre qualquer etapa do plano. Quando uma etapa descreve uma ação de deleção ou renomeação, o agente deve apresentar o snippet/instrução completa para o usuário executar, nunca executar por conta própria.

0. O arquivo `memoria.md` nao é considerado um arquivo de aplicação e sim um arquivo do agente.
1. O agente NAO implementa alteracoes no projeto que não seja o arquivo `memoria.md`. Isso inclui passos que descrevem "deletar" ou "renomear" arquivos: o agente apresenta as instruções completas, mas o usuário executa.
2. O agente NAO edita arquivos da aplicacao, NAO roda refactors no codigo e NAO aplica patches no codigo-fonte.
3. O agente atua apenas como professor e guia tecnico.
4. O usuario e o unico executor das alteracoes. Com excesão ao arquivo `memoria.md`
5. Forneça o conteúdo integral do arquivo modificado (não trechos parciais), de forma que o usuário possa copiar e colar diretamente sem precisar mesclar manualmente. Inclua uma forma objetiva de verificar antes de seguir para o próximo passo.
6. O agente deve sempre considerar o estado atual do projeto e registrar o que ja foi feito e o que ainda falta.
7. Se o usuário reportar que uma verificação falhou, o agente deve diagnosticar a causa provável com base nos artefatos da fase atual e fornecer instruções corretivas antes de continuar para a próxima fase.

O Keycloak é um servidor de identidade externo. Vamos substituí-lo pelo **Spring Authorization Server (SAS)**, que é uma biblioteca do próprio Spring que transforma o seu app em um servidor de autenticação. O banco PostgreSQL guardará os usuários e senhas no lugar do Keycloak. O fluxo de login (redirect → form → sessão → logout) vai continuar igual, só que tudo roda dentro da sua própria aplicação.

Antes de qualquer alteracao no projeto, a primeira atividade do agente deve ser criar ou atualizar um arquivo `memoria.md` com duas responsabilidades:

- registrar as instrucoes principais de comportamento do agente
- manter o tracking do que ja foi implementado, do que foi validado e do que ainda falta executar

Sem esse arquivo de memoria, a execucao do plano nao deve comecar. Se o arquivo `memoria.md` não existir quando o usuário solicitar qualquer passo das fases, o agente deve recusar a continuar e responder exclusivamente: "O arquivo memoria.md ainda não foi criado. Vou criá-lo agora antes de qualquer outra ação." e então criá-lo.

---

### Atividade 0 — Criar o arquivo de memoria antes de qualquer outra coisa

Objetivo: garantir que o agente siga sempre o mesmo comportamento durante toda a migracao e que exista um ponto unico de controle do progresso.

O agente deve criar um arquivo `memoria.md` logo no inicio. Esse arquivo deve conter, no minimo:

- o papel do agente: professor, nao executor
- o papel do usuario: executor das alteracoes
- a regra de que o agente nunca altera os arquivos do projeto por conta propria
- a fase atual do plano
- checklist do que foi concluido
- checklist do que ainda esta pendente
- observacoes importantes descobertas durante a migracao

Verificacao da Atividade 0: o arquivo `memoria.md` existe e ja contem as regras de comportamento e o tracking inicial do plano.

---

### Fase 1 — Subir o PostgreSQL (Verificável de forma independente)

**Passo 1** — `compose.yaml`: remover o serviço `keycloak` e `Dockerfile.keycloak`, adicionar serviço `postgres:16` com banco `portal_nr1`, usuário `portal`, senha `portal`, porta `5432:5432`

**Passo 2** — `pom.xml`: quatro mudanças simultâneas:
- Remover `keycloak-admin-client`
- Adicionar `spring-boot-starter-oauth2-authorization-server`
- Adicionar `org.postgresql:postgresql` (scope runtime)
- Mover `com.h2database:h2` para `scope: test` (H2 só nos testes)

**Passo 3** — `application.yaml` (main): remover toda a seção `keycloak.*` e configurar datasource PostgreSQL

**Passo 4** — Criar `AppUser.java` — entidade JPA que representa um usuário com id, username, email, senha BCrypt, firstName, lastName, enabled, roles

**Passo 5** — Criar `AppUserRepository.java` — interface Spring Data JPA com `findByUsername`

**Passo 6** — Criar `src/main/resources/data.sql` com dois usuários iniciais:
- `admin@portal.com` / senha `Admin@123` / role `ADMINISTRATOR`
- `respondent@portal.com` / senha `Resp@123` / role `RESPONDENT`

✅ **Verificação da Fase 1**: `docker compose up postgres -d` → app inicia sem erros, tabela `app_users` criada com os dois usuários.

---

### Fase 2 — Spring Authorization Server

**Passo 7** — Criar `AuthorizationServerConfig.java`: configura o SAS como OIDC provider, registra o próprio BFF como cliente OAuth2, gera par de chaves RSA para assinar tokens, define issuer `http://localhost:8080`

**Passo 8** — Criar `AppUserDetailsService.java`: implementa `UserDetailsService` do Spring Security, busca usuário no `AppUserRepository`, retorna as roles do banco

**Passo 9** — Customizar os claims JWT: adicionar `roles` como lista plana (`["ADMINISTRATOR"]`) e claims OIDC (`email`, `preferred_username`) ao token emitido pelo SAS

✅ **Verificação da Fase 2**: Acessar `http://localhost:8080/.well-known/openid-configuration` → retorna o discovery document do SAS (JSON com endpoints).

---

### Fase 3 — SecurityConfig do BFF

**Passo 10** — Refatorar `SecurityConfig.java`: separar em duas filter chains — `@Order(1)` para o SAS, `@Order(2)` para a API. Remover referências ao Keycloak. Atualizar `JwtAuthenticationConverter` para ler `roles` no novo formato (lista plana, não `realm_access.roles`). Corrigir `hasRole("ADMIN")` → `hasRole("ADMINISTRATOR")`.

**Passo 11** — `application.yaml` (main): substituir provider `keycloak` por provider local apontando para `http://localhost:8080` (o próprio app)

✅ **Verificação da Fase 3**: Acessar `http://localhost:8080/oauth2/authorization/questionnaire-bff` → redireciona para `/login`. Logar com `admin@portal.com` → `GET /api/me` retorna `roles: ["ADMINISTRATOR"]`.

---

### Fase 4 — Novo Adapter de Provisionamento

**Passo 12** — Criar `SpringUserIdentityProvisioningAdapter.java` implementando `UserIdentityProvisioningPort`:
- `provisionRespondent` → cria usuário no PostgreSQL com role `RESPONDENT` e senha temporária
- `provisionGroup` → retorna `UUID.randomUUID()` (grupos já são locais)
- `assignRespondentToGroup`, `updateGroupName`, `deleteGroup` → no-op (operações locais)

**Passo 13** — Renomear as exceções Keycloak para nomes genéricos:
- `KeycloakProvisioningException` → `UserProvisioningException`
- `KeycloakEmailConflictException` → `UserEmailConflictException`
- `KeycloakGroupNameConflictException` → `GroupNameConflictException`
- Atualizar `GlobalExceptionHandler` com os novos nomes

**Passo 14** — Deletar `KeycloakUserIdentityProvisioningAdapter.java`

✅ **Verificação da Fase 4**: `POST /api/respondents` → usuário criado no banco PostgreSQL, senha temporária gerada.

---

### Fase 5 — Limpeza de Configurações

**Passo 15** — Deletar `KeycloakConfig.java`

**Passo 16** — Deletar `OAuth2ClientManagerConfig.java` (não será mais necessário com SAS embutido)

**Passo 17** — Atualizar `OpenApiConfig.java`: trocar URLs do Keycloak para os endpoints do SAS local (`/oauth2/authorize`, `/oauth2/token`)

**Passo 18** — Simplificar `AuthenticatedUserResolver.java`: remover extração de `realm_access.roles`, ler claim `roles` direto (lista plana)

✅ **Verificação da Fase 5**: Swagger UI → botão Authorize → fluxo OAuth2 funciona contra o SAS local.

---

### Fase 6 — Form de Login Customizável

**Passo 19** — Criar `src/main/resources/templates/login.html` (Thymeleaf): form simples com campos `username` e `password`, POST para `/login`, mensagem de erro quando `?error` está na URL — layout fácil de estilizar

**Passo 20** — Configurar o SAS para usar a página customizada (em vez do formulário padrão do Spring)

✅ **Verificação da Fase 6**: Acessar `http://localhost:8080/login` → exibe o form customizado.

---

### Fase 7 — Testes

**Passo 21** — `application.yaml` (test): remover referências ao Keycloak, configurar H2 + SAS em modo de teste

**Passo 22** — `SecurityIntegrationTest.java`: atualizar para usar `jwt()` do Spring Security Test com o novo formato de claims (`roles` lista plana)

**Passo 23** — Revisar `GroupControllerIntegrationTest` e `QuestionnaireControllerIntegrationTest` para o novo formato de claims

✅ **Verificação final**: `mvn test` passa sem erros.

---

**Arquivos relevantes**
- `compose.yaml` — trocar keycloak por postgres
- `pom.xml` — dependências
- `src/main/resources/application.yaml` — config principal
- `src/main/java/br/com/portal_nr1/infrastructure/config/SecurityConfig.java` — filter chains
- `src/main/java/br/com/portal_nr1/infrastructure/config/OpenApiConfig.java` — URLs OAuth2
- `src/main/java/br/com/portal_nr1/infrastructure/security/AuthenticatedUserResolver.java` — extração de claims
- `src/main/java/br/com/portal_nr1/infrastructure/adapters/out/identity/KeycloakUserIdentityProvisioningAdapter.java` — substituir
- `src/test/resources/application.yaml` — config de testes

**Decisões**
- SAS embutido na mesma aplicação (mais simples para aprendizado)
- Role `ADMIN` no SecurityConfig está inconsistente com `ADMINISTRATOR` — será corrigido para `ADMINISTRATOR` no Passo 10
- Groups continuam sendo gerenciados localmente (os métodos de grupo no `UserIdentityProvisioningPort` viram no-op no novo adapter)
- `provisionGroup` no novo adapter retorna um UUID aleatório local — o GroupsService não percebe diferença
