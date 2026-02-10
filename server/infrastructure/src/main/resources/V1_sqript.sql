select * from tasks order by created_at;
select * from tasks order by scheduled_at;
select * from tasks order by started_at;
select * from tasks order by heartbeat_at;

select * from barriers order by status, created_at  desc

select * from logs
where task_id = '82644423-05b2-4d2e-adae-b324a5c4cb59'
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