select * from tasks order by created_at;
select * from tasks order by scheduled_at;
select * from tasks order by started_at;
select * from tasks order by heartbeat_at;

select * from tasks
where correlation_id = '1d3c8b57-1ceb-46d6-a499-84a608637d15'
order by created_at
select * from barriers order by updated_at  asc LIMIT 10



select * from logs
where task_id = '6234fc28-6e1a-4490-aa1d-4554b6bb4948'
order by timestamp

delete from tasks;
delete from barriers;
delete from logs;

UPDATE barriers
SET locked_at = null,
    locked = false,
    locked_by = null
WHERE id = 'f783ffad-95c2-4c1c-bc15-e21e4a425128'

UPDATE tasks
SET locked_at = null,
    locked = false,
    locked_by = null
WHERE id = '0932708d-1f4a-43cb-8f33-2cc11d8a790a'

UPDATE tasks
SET status = 'RUNNING',
    started_at = ?
WHERE id = ?
  AND locked_by = ?
  AND locked = true


select current_timestamp - interval '600 second'

SELECT id,
       name,
       tube,
       status,
       updated_at,
       created_at,
       aborted_at,
       completed_at,
       handled_by,
       COUNT(*) OVER() AS total_count
FROM tasks
WHERE tube = 'sandbox-tube'
ORDER BY created_at DESC
LIMIT 5 OFFSET 0
