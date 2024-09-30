import React from 'react';
import 'boxicons';

const BoxIcon = (props: any) => {
  const {name, color} = props;
  return (
    <div>
      <box-icon name="name" color="color"></box-icon>
    </div>
  );
};

export default BoxIcon;
