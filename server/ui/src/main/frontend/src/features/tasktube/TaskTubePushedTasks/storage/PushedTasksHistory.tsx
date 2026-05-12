const PUSHED_TASKS = 'PUSHED_TASKS';
const MAX_SIZE = 20;

export interface PushedTask {
  id: string;
  name: string;
  correlationId: string;
  tube: string;
}

const getTasks = (): PushedTask[] => {
  const tasks = localStorage.getItem(PUSHED_TASKS);

  return !!tasks ? JSON.parse(tasks) : [];
};

const addTask = (task: PushedTask): void => {
  if (!task) {
    throw new Error('Trying to add "null" to pushed tasks list.');
  }

  const tasks = getTasks();

  if (tasks.length > MAX_SIZE) {
    tasks.pop();
  }

  tasks.unshift(task);

  setTasks(tasks);
};

const removeTask = (task: PushedTask): void => {
  if (!task) {
    throw new Error('Trying to remove "null" from pushed tasks list.');
  }

  let tasks = getTasks();

  tasks = tasks.filter((t) => t.id !== task.id);

  setTasks(tasks);
};

const setTasks = (tasks: PushedTask[]): void => {
  localStorage.setItem(PUSHED_TASKS, JSON.stringify([...tasks]));
};

const PushedTasksHistory = {
  getTasks,
  addTask,
  removeTask,
};

export default PushedTasksHistory;
