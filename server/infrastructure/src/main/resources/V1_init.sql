CREATE TABLE barriers
(
    id          UUID PRIMARY KEY,                   -- уникальный идентификатор
    task_id     UUID        NOT NULL,               -- уникальный идентификатор задачи
    wait_for    UUID[]      NOT NULL,               -- задачи, которые надо ждать
    type        varchar(32) NOT NULL,               -- тип
    status      varchar(32) NOT NULL,               -- статус барьера
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(), -- время последнего обновления
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(), -- время создания
    released_at TIMESTAMPTZ,                        -- время достижения барьера
    locked_at   TIMESTAMPTZ,                        -- когда задача была заблокирована
    locked      BOOLEAN     NULL,                   -- заблокирована ли
    locked_by   varchar(256)                        -- кем заблокирована
);
CREATE TABLE tasks
(
    id             UUID PRIMARY KEY,                     -- уникальный идентификатор
    name           varchar(1024) NOT NULL,               -- имя задачи
    tube           varchar(512)  NOT NULL,               -- очередь, к которой принадлежит
    status         varchar(32)   NOT NULL,               -- статус
    correlation_id varchar(256)  NOT NULL,               -- идентификатор корреляции
    parent_id      UUID,                                 -- id родительской таски
    input          JSONB,                                -- входные данные (Map<String, Object>)
    output         JSONB,                                -- результат выполнения (Map<String, Object>)
    is_root        BOOLEAN       NOT NULL DEFAULT FALSE, -- флаг корневой задачи
    updated_at     TIMESTAMPTZ   NOT NULL DEFAULT NOW(), -- время последнего обновления
    created_at     TIMESTAMPTZ   NOT NULL DEFAULT NOW(), -- время создания
    canceled_at    TIMESTAMPTZ,                          -- время отмены задачи
    scheduled_at   TIMESTAMPTZ,                          -- время планирования
    started_at     TIMESTAMPTZ,                          -- время начала обработки клиентом
    heartbeat_at   TIMESTAMPTZ,                          -- время последнего хэлс чека
    finished_at    TIMESTAMPTZ,                          -- время конца обработки клиентом
    failed_at      TIMESTAMPTZ,                          -- время последней ошибки
    aborted_at     TIMESTAMPTZ,                          -- время прекращения задачи
    completed_at   TIMESTAMPTZ,                          -- время завершения
    failures       INT,                                  -- количество отказов
    failed_reason  VARCHAR,                              -- причина последнего отказа
    locked_at      TIMESTAMPTZ,                          -- когда задача была заблокирована
    locked         BOOLEAN       NULL,                   -- заблокирована ли
    locked_by      varchar(256),                         -- кем заблокирована
    settings       JSONB,                                -- настройки
    logs           JSONB,                                -- логи задачи
    handled_by     varchar(256)                          -- кем обработана
);
