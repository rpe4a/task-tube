import { IconButton } from '@mui/material';
import { JSX, memo } from 'react';
import { Link } from 'react-router';

interface IconLinkProps extends React.PropsWithChildren {
  to: string;
  title: string;
  size: 'small' | 'medium' | 'large';
}

function IconLink(props: IconLinkProps): JSX.Element {
  const { to, title, size, children } = props;

  return (
    <IconButton
      component={Link}
      to={to}
      target="_blank"
      size={size}
      sx={{
        color: 'primary.main',
        '&:hover': {
          bgcolor: 'primary.light',
          color: 'white',
        },
      }}
      title={title}
    >
      {children}
    </IconButton>
  );
}

export default memo(IconLink);
