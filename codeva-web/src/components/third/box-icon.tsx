import { IconBaseProps, IconType } from 'react-icons';
const BoxIcon = (props: IconBaseProps) => {
  const { name, size } = props;
  const IconModule = require(`react-icons/bi`);
  const iconNames = Object.keys(IconModule);
  // 判断图标name集合中是否包含name
  if (!name || !iconNames.includes(name)) {
    return <span>{name}</span>;
  }
  const BoxIcon = IconModule[name as keyof typeof IconModule] as IconType;
  return <BoxIcon {...props} />;
}
export default BoxIcon;
