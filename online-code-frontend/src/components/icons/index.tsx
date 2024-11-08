import React from "react";
import "./index.scss";
import BoxIcon from "../third/box-icon";

class Icons extends React.Component<any, any> {

  componentDidMount() {
  }

  render() {
    const state = this.state;
    // 所有boxicons的icon name
    const IconModule = require(`react-icons/bi`);
    const iconNames = Object.keys(IconModule);
    return (
      <div className="icons-container">
        {iconNames.map((iconName) => {
          return (
            <div className="icon-item" key={iconName}>
              <BoxIcon className="icon" name={iconName} size={'20'}/>
              <span className="icon-name">{iconName}</span>
            </div>
          );
        })}
      </div>
    );
  }
}

export default Icons;
