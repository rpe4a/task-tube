select * from tasks order by created_at;
select * from tasks order by scheduled_at;
select * from tasks order by started_at;
select * from tasks order by heartbeat_at;

select * from barriers

delete from tasks;
delete from barriers;


UPDATE tasks
SET status = 'RUNNING',
    started_at = ?
WHERE id = ?
AND locked_by = ?
AND locked = true
