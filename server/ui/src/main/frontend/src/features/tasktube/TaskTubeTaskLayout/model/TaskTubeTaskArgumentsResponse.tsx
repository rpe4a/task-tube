export interface TaskTubeTaskArgumentsResponse {
  id: string;
  taskId: string;
  timestamp: string;
  message: string;
  exceptionMessage: string | null;
  exceptionStackTrace: string | null;
}
