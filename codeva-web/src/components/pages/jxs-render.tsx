import * as React from 'react';
import ReactDOM from "react-dom";

interface ComponentProps {
  jsx: any
  onRender?: Function
}

export default class JsxRenderer extends React.Component<ComponentProps> {

  constructor(props) {
    super(props);
    this.state = {
      jsx: props.jsx,
      onRender: props.onRender
    };
    this.ref = React.createRef();
  }

  componentDidMount() {
    this.jsxRender();
  }

  componentDidUpdate(prevProps) {
    if (this.props.jsx !== prevProps.jsx) {
      this.state.jsx = this.props.jsx;
      this.jsxRender();
    }
  }

  jsxRender() {
    ReactDOM.render(this.state.jsx, this.ref.current);
    // 渲染回调
    if (this.state.onRender !== undefined) {
      this.state.onRender(this.ref, this.state.jsx);
    }
  }

  render() {
    return (
      <div ref={this.ref}>
      </div>
    )
  }

}
