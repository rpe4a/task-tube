CREATE TABLE tasks
(
    id           UUID PRIMARY KEY,                    -- уникальный идентификатор
    name         varchar(512) NOT NULL,               -- имя задачи
    tube         varchar(512) NOT NULL,               -- очередь, к которой принадлежит
    status       varchar(32)  NOT NULL,               -- статус (можно потом заменить на ENUM)
    input        JSONB,                               -- входные данные (Map<String, Object>)
    is_root      BOOLEAN      NOT NULL DEFAULT FALSE, -- флаг корневой задачи
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(), -- время последнего обновления
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(), -- время создания
    scheduled_at TIMESTAMPTZ,                         -- время планирования
    started_at   TIMESTAMPTZ,                         -- время начала обработки клиентом
    heartbeat_at TIMESTAMPTZ,                         -- время последнего хэлс чека
    finished_at  TIMESTAMPTZ,                         -- время конца обработки клиентом
    locked_at    TIMESTAMPTZ,                         -- когда задача была заблокирована
    locked       BOOLEAN      NULL,                   -- заблокирована ли
    locked_by    varchar(256)                         -- кем заблокирована
);
