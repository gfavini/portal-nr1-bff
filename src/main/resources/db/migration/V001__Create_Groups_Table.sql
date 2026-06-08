-- Migration: Create groups table with PostgreSQL persistence
-- Version: 001
-- Description: Initialize groups table with full JPA mapping

CREATE TABLE IF NOT EXISTS groups (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE,
    assigned_questionnaire_id VARCHAR(255),
    assigned_questionnaire_version INTEGER,
    status VARCHAR(50) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE,
    last_invite_sent_at TIMESTAMP WITH TIME ZONE,
    last_invite_scope VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS group_departments (
    group_id UUID NOT NULL,
    department VARCHAR(255) NOT NULL,
    PRIMARY KEY (group_id, department),
    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE
);

-- Indexes
CREATE INDEX idx_groups_name ON groups(name);
CREATE INDEX idx_groups_status ON groups(status);
CREATE INDEX idx_groups_assigned_questionnaire ON groups(assigned_questionnaire_id, assigned_questionnaire_version);
CREATE INDEX idx_group_departments_group_id ON group_departments(group_id);
