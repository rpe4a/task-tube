import { TableRow, TableCell, Skeleton } from '@mui/material';
import { memo } from 'react';

const TableSkeleton = ({ rowsNum = 5, colsNum = 4 }) => {
  return (
    <>
      {[...Array(rowsNum)].map((_, rowIndex) => (
        <TableRow key={rowIndex}>
          {[...Array(colsNum)].map((_, colIndex) => (
            <TableCell key={colIndex}>
              <Skeleton variant="text" animation="wave" height={50} />
            </TableCell>
          ))}
        </TableRow>
      ))}
    </>
  );
};

export default memo(TableSkeleton);
