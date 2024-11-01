import { IconBaseProps, IconType } from 'react-icons';
const BoxIcon = (props: IconBaseProps) => {
  const { name, size } = props;
  const IconModule = require(`react-icons/bi`);
  const BoxIcon = IconModule[name as keyof typeof IconModule] as IconType;
  console.log(props);
  return <BoxIcon {...props} />;
}
export default BoxIcon;
