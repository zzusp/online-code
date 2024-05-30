import * as React from 'react';

interface ComponentProps {
  name: string,
  element: any,
  onRender: Function|undefined
}

export default class CustomRendererComponent extends React.Component<ComponentProps> {

  constructor(props) {
    super(props);
    this.state = {
      name: props.name,
      element: props.element,
      onRender: props.onRender
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
      <div className="CustomRendererComponent">
        <div ref={this.ref}>
          {this.state.element || 'no element'}
        </div>
      </div>
    )
  }

}
