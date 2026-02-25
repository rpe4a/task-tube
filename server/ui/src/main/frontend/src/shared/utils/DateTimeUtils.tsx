import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
dayjs.extend(utc);

export const DateTimeFormater = {
  DEFAULT: 'YYYY-MM-DD HH:mm:ss',
  DATE: 'YYYY-MM-DD',
  TIME: 'HH:mm:ss',
  CALENDAR: 'DD/MM/YYYY HH:mm',
};

export const formatDateTime = (
  dateTimeString: string | null,
  formater: string = DateTimeFormater.DEFAULT,
): string => {
  if (!dateTimeString) return '-';
  const date = dayjs.utc(dateTimeString);
  return date.format(formater);
};

export const calculateDuration = (
  createdAt: string,
  completedAt: string | null,
  abortedAt: string | null,
): string => {
  if (!createdAt) return '-';

  const startTime = dayjs(createdAt);
  const endTime = completedAt ? dayjs(completedAt) : abortedAt ? dayjs(abortedAt) : null;

  if (!endTime) return '-';

  const durationMs = endTime.diff(startTime);
  const durationSec = Math.floor(durationMs / 1000);

  const hours = Math.floor(durationSec / 3600);
  const minutes = Math.floor((durationSec % 3600) / 60);
  const seconds = durationSec % 60;

  return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
};
