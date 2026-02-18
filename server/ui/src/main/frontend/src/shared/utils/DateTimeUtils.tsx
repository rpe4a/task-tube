import dayjs from 'dayjs';

export const DateTimeFormater = {
  DEFAULT: 'YYYY-MM-DD HH:mm:ss',
  DATE: 'YYYY-MM-DD',
  TIME: 'HH:mm:ss',
};

export const formatDateTime = (
  dateTimeString: string | null,
  formater: string = DateTimeFormater.DEFAULT,
): string => {
  if (!dateTimeString) return '-';
  const date = dayjs(dateTimeString);
  return date.format(formater);
};
