import TaskTubeTreeNode from './TaskTubeTreeNode';

interface TaskTubeTreeNodeResponse {
  root: TaskTubeTreeNode;
  children: TaskTubeTreeNode[] | null;
}

export default TaskTubeTreeNodeResponse;
