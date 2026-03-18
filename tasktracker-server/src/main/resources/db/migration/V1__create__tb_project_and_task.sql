CREATE TABLE tb_projects (
  id           BIGSERIAL    PRIMARY KEY,
  name         VARCHAR(150) NOT NULL,
  description  TEXT,
  technology   VARCHAR(500),
  status       VARCHAR(30)  NOT NULL DEFAULT 'ACTIVE',
  created_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE tb_tasks (
  id            BIGSERIAL    PRIMARY KEY,
  fk_project_id BIGINT       NOT NULL REFERENCES tb_projects(id) ON DELETE CASCADE,
  title         VARCHAR(200) NOT NULL,
  description   TEXT,
  status        VARCHAR(30)  NOT NULL DEFAULT 'PENDING',
  created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_tasks_project ON tb_tasks(fk_project_id);