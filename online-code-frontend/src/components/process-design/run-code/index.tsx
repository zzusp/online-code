import React, { useRef } from 'react';

import {Form, Input, Nav, Shell, Box, Button, ResponsiveGrid, Radio, Card} from '@alifd/next';
import IceTitle from '@icedesign/title';
import { JsonView, allExpanded, darkStyles, defaultStyles } from 'react-json-view-lite';
import 'react-json-view-lite/dist/index.css';
import {createFetch} from "../../../fetchHandler";

class RunCode extends React.Component<any, any> {

  state = {
    inputVal: '{}',
    outputVal: ''
  }

  async runCmd() {
    console.log(this.props?.code);
    const param = {
      cmd: this.props?.code,
      vars: JSON.parse(this.state.inputVal)
    }
    await createFetch({url: '/onlinecode-api/process/runCmd', method: 'POST', data: param})
      .then((res: any) => {
        console.log(res);
        if (res.status === 200 && res.data) {
          this.setState({outputVal: res.data});
        } else {
          this.setState({outputVal: res});
        }
      })
      .catch((err: any) => {});
  }

  render() {
    return (
      <div style={{margin:'0 20px 0 20px'}}>
        <IceTitle text="测试" style={{margin:'20px 0 20px 0'}} />
        <div style={{margin:'0 0 8px 0'}}>
          <Button type="primary" onClick={() => this.runCmd()}>执行</Button>
        </div>
        <div>
          <Card free style={{margin:'0 0 8px 0'}}>
            <Card.Content style={{height: 100, overflow: 'auto'}}>
              <Input.TextArea
                style={{ width: '100%' }}
                placeholder="输入"
                maxLength={8000}
                rows={4}
                showLimitHint
                aria-label="input max length 100"
                value={this.state.inputVal}
                onChange={(value, e) => {this.setState({inputVal: value})}}
              />
            </Card.Content>
          </Card>
          <Card free>
            <Card.Content style={{height: 200, overflow: 'auto'}}>
              {/*<Input.TextArea*/}
              {/*  style={{ width: '100%'}}*/}
              {/*  readOnly={true}*/}
              {/*  placeholder="输出"*/}
              {/*  maxLength={8000}*/}
              {/*  rows={11}*/}
              {/*  showLimitHint*/}
              {/*  aria-label="input max length 100"*/}
              {/*  value={this.state.outputVal}*/}
              {/*/>*/}
              <React.Fragment>
                <JsonView data={this.state.outputVal} shouldExpandNode={allExpanded} style={defaultStyles} />
              </React.Fragment>
            </Card.Content>
          </Card>
        </div>
      </div>
    );
  }
}

export default RunCode;
