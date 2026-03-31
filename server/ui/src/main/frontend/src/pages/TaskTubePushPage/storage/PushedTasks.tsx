const PUSHED_TASKS = 'PUSHED_TASKS';
const MAX_SIZE = 20;
export interface PushedTask {
  id: string;
  name: string;
  correlationId: string;
}

export const getPushedTasks = (): PushedTask[] => {
  const tasks = localStorage.getItem(PUSHED_TASKS);
  return !!tasks ? JSON.parse(tasks) : [];
};

export const addPushedTask = (task: PushedTask): void => {
  const tasks = getPushedTasks();

  tasks.unshift(task);

  if (tasks.length > MAX_SIZE) {
    tasks.pop();
  }

  localStorage.setItem(PUSHED_TASKS, JSON.stringify([...tasks]));
};

export const removePushedTask = (task: PushedTask): void => {
  let tasks = getPushedTasks();

  tasks = tasks.filter((t) => t.id !== task.id);

  localStorage.setItem(PUSHED_TASKS, JSON.stringify([...tasks]));
};
