CREATE TABLE tasks
(
    id         UUID PRIMARY KEY,                    -- уникальный идентификатор
    name       varchar(512) NOT NULL,               -- имя задачи
    queue      varchar(512) NOT NULL,               -- очередь, к которой принадлежит
    status     varchar(32)  NOT NULL,               -- статус (можно потом заменить на ENUM)
    input      JSONB,                               -- входные данные (Map<String, Object>)
    is_root    BOOLEAN      NOT NULL DEFAULT FALSE, -- флаг корневой задачи
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(), -- время создания
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(), -- время последнего обновления
    locked_at  TIMESTAMPTZ,                         -- когда задача была заблокирована
    locked     BOOLEAN      NULL,                   -- заблокирована ли
    locked_by  varchar(256)                         -- кем заблокирована
);
