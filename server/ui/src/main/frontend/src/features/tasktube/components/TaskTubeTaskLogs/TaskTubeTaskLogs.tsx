interface TaskTubeTaskLogsProps {
  correlationId: string;
  taskId: string;
}

function TaskTubeTaskLogs(prop: TaskTubeTaskLogsProps) {
  const { correlationId, taskId } = prop;

  return <div></div>;
}

export default TaskTubeTaskLogs;
