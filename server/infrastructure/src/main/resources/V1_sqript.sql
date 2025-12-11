select * from tasks order by created_at;
select * from tasks order by scheduled_at;
select * from tasks order by started_at;
select * from tasks order by heartbeat_at;

select * from barriers

delete from tasks;
delete from barriers;

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