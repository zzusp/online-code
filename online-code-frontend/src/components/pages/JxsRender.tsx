import * as React from 'react';

interface ComponentProps {
  // jsx: any
  onRender: Function
  // render: Function
}

export default class JsxRenderer extends React.Component<ComponentProps> {

  constructor(props) {
    super(props);
    this.state = {
      // jsx: props.jsx,
      onRender: props.onRender,
      // render: props.render
    };
    this.ref = React.createRef();
  }

  componentDidMount() {
    if (this.state.onRender !== undefined) {
      this.state.onRender(this.ref);
    }
  }

  render() {
    return (
      <div ref={this.ref}>
        {/*{this.state.jsx || 'no element'}*/}
        <p>321</p>
      </div>
    )
  }

}
