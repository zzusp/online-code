import React, {useState} from 'react';
import {Form, Shell, Box, Button, Search, Tab, Menu} from '@alifd/next';

import './index.scss';

import {useParams} from "react-router-dom";
import {ProcessCanvas} from "./canvas";
import {createFetch} from "../../fetchHandler";

class BpmnDesign extends React.Component<any, any> {

  state = {
    panes: [],
    activeKey: '',
    procData: [],
    searchData: [],
    searchValue: '',
    refs: {}
  };

  componentDidMount() {
    let procId = this.props.procId;
    this.setState({activeKey: procId});
    this.getProcessList();
  }

  onSearch = (value: any, filterValue: any) => {
    console.log("onSearch", value, filterValue);
    this.handleSearchData(value);
  }

  onSearchChange = (value: any, type: any, e: any) => {
    console.log("onChange", value, type, e);
    this.setState({ searchValue: value });
    this.handleSearchData(value);
  }
  handleSearchData(value: any) {
    const data: { label: any; value: any; }[] = [];
    const paneKeys = this.state.panes.map((item: any) => {return item.key});
    this.state.procData.forEach((item: any) => {
      if (this.containsIgnoreCase(item.menuCode, value)
        || this.containsIgnoreCase(item.procCode, value)
        || this.containsIgnoreCase(item.procName, value)) {
        // 判断是否已经选择过
        if (!paneKeys.includes(item.id)) {
          data.push({
            label: item.procName,
            value: item.id
          });
        }
      }
    });
    this.setState({ searchData: data });
  }
  resetSearchData() {
    const searchData: any[] = [];
    const paneKeys = this.state.panes.map((item: any) => {return item.key});
    this.state.procData.forEach((item: any) => {
      if (!paneKeys.includes(item.id)) {
        searchData.push({
          label: item.procName,
          value: item.id
        });
      }
    });
    this.setState({ searchData: searchData });
  }
  containsIgnoreCase(a: string, b: string) {
    // 创建不区分大小写的正则表达式
    let regex = new RegExp('.*' + b.toLowerCase() + '.*', 'i');
    // 测试字符串是否与正则表达式匹配
    return regex.test(a);
  }
  onMenuSelect = (value: any) => {
    this.state.procData.forEach((item: any) => {
      if (item.id == value) {
        this.addTabItem(item.procName, item.id);
      }
    });
    this.setState({ activeKey: value });
    this.resetSearchData();
    this.setState({ searchValue: '' });
  }
  async getProcessList() {
    await createFetch({url: '/onlinecode-api/process/list', method: 'POST', data: {
        pageNum: 1, pageSize: -1
      }}).then((res: any) => {
      if (res.status === 200 && res.data && res.data.code === 200) {
        const data = res.data.data;
        data.rows.forEach((item: any) => {
          if (item.id == this.state.activeKey) {
            this.addTabItem(item.procName, item.id);
          }
        });
        this.setState({ procData: data.rows });
        this.resetSearchData();
      }
    }).catch((err: any) => {});
  }
  onClose = (targetKey: any) => {
    this.removeTabItem(targetKey);
  };
  onChange = (activeKey: any) => {
    this.setState({ activeKey: activeKey });
  };
  addTabItem = (tab: string, key: number) => {
    let panes: any[] = this.state.panes;
    panes.push({ tab: tab, key: key, closeable: true })
    this.setState({ panes: panes });
  };
  removeTabItem(targetKey: any) {
    let activeKey = this.state.activeKey;
    const panes: any[] = [];
    this.state.panes.forEach((pane: any) => {
      if (pane.key != targetKey) {
        panes.push(pane);
      }
    });
    if (panes.length === 0) {
      activeKey = '';
    } else if (targetKey == activeKey) {
      activeKey = panes[0].key;
    }
    this.setState({ panes, activeKey });
  }
  onRef = (ref: any, procId: any) => {
    let refs: any = this.state.refs;
    refs[procId + ''] = ref;
    this.setState({ refs: refs });
  }
  save = () => {
    const key: string = this.state.activeKey;
    const refs: any = this.state.refs;
    if (refs && refs[key]) {
      const ref: any = refs[key];
      ref.saveProcessInfo();
    }
  }

  render() {
    const state = this.state;

    return (
      <Shell
        device={'desktop'}
        style={{ height: '100%', border: "1px solid #eee", position: 'fixed', left: 0, right: 0, bottom: 0, top: 0 }}
      >
        <Shell.Branding>
          <div className="rectangular"></div>
          <span style={{ marginLeft: 10 }}>流程编排</span>
        </Shell.Branding>
        <Shell.Action>
          <Box direction="row" spacing={10}>
            <Search type="normal" shape="simple" placeholder="请输入" style={{ width: "200px" }}
                    popupContent={
                        <Menu
                          onSelect={this.onMenuSelect}
                          selectMode="single"
                        >
                          {state.searchData.length > 0 ? state.searchData.map((item: any) => {
                            return (
                              <Menu.Item key={item.value}>
                                {item.label}
                              </Menu.Item>
                            );
                          }) : <Menu.Item key={0}>
                            无选项
                          </Menu.Item>}
                        </Menu>
                    }
                    dataSource={state.searchData}
                    value={state.searchValue}
                    onChange={this.onSearchChange}
                    onSearch={this.onSearch} />
            <Button type="primary" onClick={this.save}>保存</Button>
          </Box>
        </Shell.Action>

        <Shell.Content style={{ padding: 0, height: '100%' }}>
          <Tab
            shape="capsule"
            activeKey={state.activeKey}
            onChange={this.onChange}
            onClose={this.onClose}
            className="custom-tab"
            size={'small'}
            style={{height: '100%', width: '100%'}}
            navStyle={{border: '1px solid #ddd', backgroundColor: '#fff'}}
            contentStyle={{height: 'calc(100% - 36px)', width: '100%'}}
          >
            {state.panes.map((item: any) => {
              return (
                <Tab.Item
                  title={item.tab}
                  key={item.key}
                  closeable={state.panes.length > 1 ? item.closeable : false}
                >
                  {item && item.key ? <ProcessCanvas onRef={this.onRef} procId={item.key} /> : ''}
                </Tab.Item>)
            })
            }
          </Tab>
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
