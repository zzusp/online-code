import React, {useState} from 'react';
import {Form, Input, Nav, Shell, Box, Button, Card, Message} from '@alifd/next';
import IceTitle from '@icedesign/title';

import { AsObject } from 'universal-request/lib/types';
// 引入相关的依赖
import EventBus from "./bpmn/utils/event-bus.js";
import {bpmnStr} from "./bpmn/utils/processStr";

import CustomModeler from './bpmn'

import {append as svgAppend, attr as svgAttr, create as svgCreate} from 'tiny-svg'
import {query as domQuery} from 'min-dom';

import 'bpmn-js/dist/assets/diagram-js.css';
import 'bpmn-js/dist/assets/bpmn-js.css';
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn.css';
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn-codes.css';
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn-embedded.css';
import './index.scss';
import './bpmn.css';
import './connection.css';
import './context-pad.css';
import './label.css';
import './palette.css';
import './property-panel.css';

import {useParams} from "react-router-dom";
import CodeEditor from "./editor";
import RunCode from "./run-code";
import {createFetch} from "../../fetchHandler";

class BpmnDesign extends React.Component<any, any> {

  state = {
    proc: {
      procCode: '',
      procName: ''
    },
    // bpmn建模器
    bpmnModeler: new CustomModeler(),
    // bpmn字符串
    bpmnStr: bpmnStr,
    // 流程所有节点对象
    procTasks: {},
    taskData: {},
    task: {
      procCode: '',
      taskCode: '',
      taskName: '',
      type: '',
      executeCmd: ''
    },
    collapse: true,
    showCodeEdit: false,
    fullScreen: false,
    editorValue: '',
    runResult: ''
  };

  componentDidMount() {
    this.getProcessInfo(this.props.procId);
  }

  async getProcessInfo(procId: any) {
    await createFetch({url: '/onlinecode-api/process/getInfoWithTaskById?id=' + procId, method: 'GET'})
      .then((res: any) => {
        if (res.status === 200 && res.data && res.data.code === 200) {
          const proc = res.data.data;
          this.setState({proc: proc});
          // 如果bpmn不为空，则渲染
          if (proc.bpmn) {
            this.setState({bpmnStr: proc.bpmn.replace(/Process_1/g, proc.procCode)});
            let data = {};
            for (let i in proc.tasks) {
              const taskCode = proc.tasks[i].taskCode;
              data = Object.assign({}, data, {[taskCode]: proc.tasks[i]})
            }
            this.setState({procTasks: data});
          } else {
            let bpmnStr = this.state.bpmnStr.replace(/Process_1/g, proc.procCode);
            this.setState({bpmnStr: bpmnStr});
          }
        }
        this.init();
      })
      .catch((err: any) => {});
  }

  async saveProcessInfo() {
    let proc: any = this.state.proc;
    let tasks: any[] = [];
    let data: any = this.state.procTasks;
    for (let t in data) {
      tasks.push(data[t]);
    }
    proc.bpmn = this.state.bpmnStr;
    proc.tasks = tasks;
    console.log(proc);
    await createFetch({url: '/onlinecode-api/process/save', method: 'POST', data: proc as AsObject})
      .then((res: any) => {
        console.log(res);
        if (res.status === 200 && res.data && res.data.code === 200) {
          Message.success('成功保存到数据库');
        } else {
          Message.error('保存失败，错误信息：' + res.data.message);
        }
      })
      .catch((err: any) => {});
  }

  labelChange(e: string) {
    console.log(e);
    // 将同步图中的节点名称
    const modeling = (this.state.bpmnModeler as any).get('modeling');
    // 更新task名称
    const task = Object.assign({}, this.state.task, {taskName: e});
    this.setState({task: task});
    const taskCode: string = this.state.task.taskCode;
    // 更新process对象中的task名称
    this.setState({procTasks: Object.assign({}, this.state.procTasks, {[taskCode]: task})});
    // 更新task存储对象中，对应的task名称
    const data = (this.state.taskData as any)[taskCode];
    data.businessObject.name = e;
    this.setState({taskData: {[taskCode]: data}});
    // 更新流程图节点对应名称
    modeling.updateProperties(data, {});
  }

  codeChange(code: string) {
    const taskCode: string = this.state.task.taskCode;
    let task: any = (this.state.procTasks as any)[taskCode];
    if (task.executeCmd === code) {
      return;
    }
    task = Object.assign({}, this.state.task, {executeCmd: code});
    this.setState({task: task});
    // 更新process对象中的task名称
    this.setState({procTasks: Object.assign({}, this.state.procTasks, {[taskCode]: task})});

    if (this.state.task.type === 'bpmn:SequenceFlow') {
      // 将代码更新到表达式中
      const moddle = (this.state.bpmnModeler as any)._moddle;
      const modeling = (this.state.bpmnModeler as any).get('modeling');
      const condition = moddle.create('bpmn:FormalExpression', {body: code});
      modeling.updateProperties((this.state.taskData as any)[taskCode], {conditionExpression: condition});
    }
  }

  init() {
    // 建模
    this.state.bpmnModeler = new CustomModeler({
      container: '#canvas',
      //添加控制板
      propertiesPanel: {
        parent: '#js-properties-panel'
      },
      additionalModules: []
    })
    // 创建画板及图形
    this.createNewDiagram(this.state.bpmnStr);
    // 监听事件总线中的editNode事件
    EventBus.subscribe('editNode', (e: any) => {
      this.setState({collapse: false})
      this.setState({showCodeEdit: false})
      console.log("editNode event:", e)
      if (typeof e !== 'undefined') {
        const taskCode = e.id;
        this.setState({taskData: {[taskCode]: e}});
        // 分支节点不显示代码
        if (e.type !== 'bpmn:ExclusiveGateway') {
          this.setState({showCodeEdit: true})
        }
        const procTasks: any = this.state.procTasks;
        let task = {procCode: this.state.proc.procCode, taskCode: taskCode, taskName: e.businessObject.name, type: e.type, executeCmd: ''};
        if (!procTasks.hasOwnProperty(taskCode)) {
          this.setState({procTasks: Object.assign({}, this.state.procTasks, {[taskCode]: task})});
        } else {
          task = procTasks[taskCode];
        }
        this.setState({task: task})
      }
    });
  }

  async createNewDiagram(bpmn: string) {
    // 将字符串转换成图显示出来
    try {
      await (this.state.bpmnModeler as any).importXML(bpmn);
      // 重绘左侧面板
      let paletteEntries: Element = document.getElementsByClassName("djs-palette-entries")[0];
      if (paletteEntries) {
        for (let val of (paletteEntries.getElementsByClassName("group") as any)) {
          const dataGroup = val.getAttribute("data-group");
          let groupName = document.createElement("span");
          groupName.setAttribute("class", "title");
          if ("base" === dataGroup) {
            groupName.innerText = "基础";
          } else if ("code" === dataGroup) {
            groupName.innerText = "代码&脚本";
          } else if ("message" === dataGroup) {
            groupName.innerText = "消息通知";
          } else {
            groupName.innerText = "其他";
          }
          val.insertBefore(groupName, val.firstChild);
          for (let child of val.getElementsByClassName("entry")) {
            let label = document.createElement("span");
            label.innerText = child.getAttribute("title");
            label.setAttribute("class", "label");
            child.appendChild(label);
          }
        }
      }
      // 这里是成功之后, 可以在这里做一系列事情
      this.reRenderSvg();
      this.addBpmnListener();
      this.addEventBusListener();
    } catch (err) {
      console.log(err.message, err.warnings);
    }
  }

  reRenderSvg() {
    let defs: any = domQuery('defs');
    if (!defs) {
      defs = svgCreate('defs');
      svgAppend(domQuery('[data-element-id="' + this.state.proc.procCode + '"]') as any, defs);
    }
    // 创建阴影
    this.createShadow(defs);
    // 创建自定义连线
    this.createMakerAndPath(defs, 'sequenceflow-arrow-normal');
    this.createMakerAndPath(defs, 'sequenceflow-arrow-activities');
  }

  // 创建自定义连线的箭头
  createMakerAndPath(defs: Element, id: string) {
    const marker = svgCreate('marker');
    svgAttr(marker, {
      id: id,
      viewBox: '0 0 20 20',
      refX: '10',
      refY: '10',
      markerWidth: '10',
      markerHeight: '10',
      orient: 'auto'
    });
    const path = svgCreate('path');
    svgAttr(path, {
      d: 'M 1 5 L 10 10 L 1 15 Z',
      style: 'stroke-width: 1px; stroke-linecap: round; stroke-dasharray: 10000, 1; fill: #dddddd;'
    });
    svgAppend(marker, path);
    svgAppend(defs, marker);
  }

  createShadow(defs: Element) {
    // 创建阴影
    const f1 = svgCreate('<filter id="shadow" height="130%">' +
      '<feGaussianBlur in="SourceAlpha" stdDeviation="3"/>' +
      '<feOffset dx="1" dy="3" result="offsetblur"/>' +
      '<feComponentTransfer>' +
      '<feFuncA type="linear" slope="0.2"/>' +
      '</feComponentTransfer>' +
      '<feMerge>' +
      '<feMergeNode/>' +
      '<feMergeNode in="SourceGraphic"/>' +
      '</feMerge>' +
      '</filter>');
    svgAppend(defs, f1);
  }

  addEventBusListener() {
    // 监听 element
    const eventBus = (this.state.bpmnModeler as any).get('eventBus')
    const eventTypes = ['element.click', 'element.changed']

    // 重绘操作板
    eventBus.on('contextPad.open', function (e: any) {
      // 重绘选中节点后展示的右侧菜单
      const nodes = e.current.pad.html.childNodes[0].childNodes;
      nodes.forEach(function (node: any) {
        let span = document.createElement("span");
        span.className = 'item';
        span.innerText = node.title;
        node.appendChild(span)
        node.title = '';
      });
    });

    eventTypes.forEach(function (eventType) {
      eventBus.on(eventType, function (e: any) {
        if (!e || e.element.type === 'bpmn:Process') return
        if (eventType === 'element.changed') {
          // console.log(document.getElementsByClassName('.djs-context-pad.open'))
        } else if (eventType === 'element.click') {
          // console.log('点击了element', e.element)
        }
      })
    })
  }

  toggleCollapse() {
    this.setState({collapse: !this.state.collapse})
  }

  addBpmnListener() {
    // 给图绑定事件，当图有发生改变就会触发这个事件
    const modeler = this.state.bpmnModeler as any;
    modeler.on('commandStack.changed', () => {
      this.saveDiagram();
    })
  }

  // 下载为bpmn格式,done是个函数，调用的时候传入的
  async saveDiagram() {
    // 把传入的done再传给bpmn原型的saveXML函数调用
    try {
      const result = await (this.state.bpmnModeler as any).saveXML({format: true});
      const { xml } = result;
      this.setState({bpmnStr: xml});
      console.log(xml);
    } catch (err) {
      console.log(err);
    }
  }

  run() {
  }

  render() {
    const FormItem = Form.Item;

    const formItemLayout = {
      labelCol: {
        fixedSpan: 4
      },
      wrapperCol: {
        span: 18
      }
    };
    return (
      <Shell
        device={'desktop'}
        style={{ height: '100%', border: "1px solid #eee", position: 'fixed', left: 0, right: 0, bottom: 0, top: 0 }}
      >
        <Shell.Branding>
          <div className="rectangular"></div>
          <span style={{ marginLeft: 10 }}>{this.state.proc?.procName + '流程'}</span>
        </Shell.Branding>
        <Shell.Action>
          <Box direction="row" spacing={10}>
            <Button type="primary" onClick={() => this.saveProcessInfo()}>保存</Button>
          </Box>
        </Shell.Action>

        <Shell.Content style={{ padding: 0, height: '100%' }}>
          <div className="containers">
            <div className="canvas" id="canvas"></div>
            <div id="js-properties-panel" className="panel"></div>
            <div className={['property-panel', this.state.collapse ? 'collapse' : 'expand'].join(" ")}>
              {
                this.state.collapse ? null :
                  <IceTitle text={this.state.task.type === 'bpmn:SequenceFlow' ? '连线' : "节点"}
                            style={{position:'absolute', margin:'20px 0 0 20px'}} />
              }
              <div className={this.state.collapse ? 'full-screen' : 'off-screen'}
                   onClick={() => this.toggleCollapse()}/>
              <Form style={{padding: '60px 0 0 0', width: '100%'}} {...formItemLayout} colon>
                <FormItem
                  name="name"
                  label="名称"
                  required
                  requiredMessage=""
                >
                  <Input value={this.state.task.taskCode} style={{display: 'none'}}/>
                  <Input value={this.state.task.type} style={{display: 'none'}}/>
                  <Input value={this.state.task.taskName} onChange={(e) => this.labelChange(e)}/>
                </FormItem>
                {
                  !this.state.showCodeEdit ? null :
                    <FormItem
                      name="code"
                      label="代码"
                      required
                      requiredMessage=""
                    >
                      <div className={this.state.fullScreen ? 'full-screen-code' : 'off-screen-code'}>
                        <CodeEditor code={this.state.task.executeCmd} codeChange={(e: string) => this.codeChange(e)} />
                      </div>
                    </FormItem>
                }
              </Form>

              <RunCode code={this.state.task.executeCmd}/>

            </div>
          </div>
        </Shell.Content>
      </Shell>
    )
  };
}

const ProcessDesign = () => {
  const {id: procId} = useParams();
  return (
    <BpmnDesign procId={procId} />
  );
}

export default ProcessDesign;
