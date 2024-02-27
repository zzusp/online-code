import React, { useRef } from 'react';

import SingleMonacoEditorComponent from '@alilc/lowcode-plugin-base-monaco-editor';

class CodeEditor extends React.Component<any, any> {

  render() {
    return (
      <SingleMonacoEditorComponent
        className={'task-code-editor-pane'}
        language="java"
        defaultValue={this.props?.code}
        supportFullScreen={true}
        onChange={this.props.codeChange}
        options={{ occurrences: true, selectOnLineNumbers: true }}
        // onMount={this.handleEditorDidMount}
      />
    );
  }
}

export default CodeEditor;
