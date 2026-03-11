export type LogType = 'CLIENT' | 'SERVER';
export type LogLevel = 'TRACE' | 'DEBUG' | 'INFO' | 'WARN' | 'ERROR';

export interface TaskTubeTaskLog {
  id: string;
  taskId: string;
  type: LogType;
  level: LogLevel;
  timestamp: string;
  message: string;
  exceptionMessage: string | null;
  exceptionStackTrace: string | null;
}
