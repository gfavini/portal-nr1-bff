-- 1. Inserir o Grupo na tabela principal
INSERT INTO groups (
    id, 
    name, 
    assigned_questionnaire_id, 
    assigned_questionnaire_version, 
    status, 
    expires_at, 
    last_invite_sent_at, 
    last_invite_scope, 
    created_at, 
    updated_at
)
VALUES (
    gen_random_uuid(),
    'Porto Seguro',                         -- Nome único do grupo
    NULL,                                   -- assigned_questionnaire_id (aceita nulo)
    NULL,                                   -- assigned_questionnaire_version (aceita nulo)
    'OPEN',                                 -- status (Ex: OPEN, CLOSE, EXPIRED)
    CURRENT_TIMESTAMP + INTERVAL '30 days', -- expires_at (Exemplo: expira em 30 dias)
    NULL,                                   -- last_invite_sent_at (aceita nulo)
    NULL,                                   -- last_invite_scope (aceita nulo)
    CURRENT_TIMESTAMP,                      -- created_at
    CURRENT_TIMESTAMP                       -- updated_at
) ON CONFLICT (name) DO NOTHING;


INSERT INTO app_users (id, username, email, password, first_name, last_name, enabled, group_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'admin@portal.com',
    'admin@portal.com',
    '$2a$12$ZCeamIFTw08BmWhlpWwEwe7i4s2MPie11p8.ZlOIPGcfJ2.lA6Ni6',
    'Admin',
    'Portal',
    true,
    (SELECT id FROM groups WHERE name = 'Porto Seguro' LIMIT 1), -- Vincula ao grupo
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (username) DO NOTHING;

INSERT INTO app_users (id, username, email, password, first_name, last_name, enabled, group_id, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'respondent@portal.com',
    'respondent@portal.com',
    '$2a$12$bcreSDnvpNMHplT04NiDduuIFIE4pnrzFgdeQ5UciK/RcGvcP7X6e',
    'Respondent',
    'Portal',
    true,
    (SELECT id FROM groups WHERE name = 'Porto Seguro' LIMIT 1), -- Vincula ao grupo
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (username) DO NOTHING;


INSERT INTO app_user_roles (user_id, role)
SELECT id, 'ADMINISTRATOR' FROM app_users WHERE username = 'admin@portal.com'
ON CONFLICT DO NOTHING;

INSERT INTO app_user_roles (user_id, role)
SELECT id, 'RESPONDENT' FROM app_users WHERE username = 'respondent@portal.com'
ON CONFLICT DO NOTHING;