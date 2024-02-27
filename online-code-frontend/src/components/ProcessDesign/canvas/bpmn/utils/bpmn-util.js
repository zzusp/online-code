// 自定义元素的配置
const customConfig = {
  'create.start-event': {
    'type': 'bpmn:StartEvent',
    'group': 'base',
    'className': 'icon-custom icon-custom-start',
    'title': '开始',
    'url': require('../../../../../assets/round.svg'),
    'attr': {x: 0, y: 0, width: 32, height: 32}
  },
  'create.exclusive-gateway': {
    'type': 'bpmn:ExclusiveGateway',
    'group': 'base',
    'className': 'icon-custom exclusive-gateway',
    'title': '分支',
    'url': require('../../../../../assets/exclusive-gateway.svg'),
    'attr': {x: 0, y: 0, width: 32, height: 32}
  },
  'create.end-event': {
    'type': 'bpmn:EndEvent',
    'group': 'base',
    'className': 'icon-custom icon-custom-end',
    'title': '结束',
    'url': require('../../../../../assets/handle-round.svg'),
    'attr': {x: 0, y: 0, width: 32, height: 32}
  },
  'create.java-task': {
    'type': 'bpmn:Task',
    'group': 'code',
    'className': 'icon-custom java-task',
    'title': 'Java',
    'url': require('../../../../../assets/java-code.svg'),
    'attr': {x: 0, y: 0, width: 32, height: 32, padding: '4px'}
  },
  'create.sql-task': {
    'type': 'bpmn:Task',
    'group': 'code',
    'className': 'icon-custom sql-task',
    'title': 'SQL',
    'url': require('../../../../../assets/sql-code.svg'),
    'attr': {x: 0, y: 0, width: 32, height: 32, padding: '4px'}
  },
  'create.http-task': {
    'type': 'bpmn:Task',
    'group': 'code',
    'className': 'icon-custom http-task',
    'title': 'Http',
    'url': require('../../../../../assets/http-code.svg'),
    'attr': {x: 0, y: 0, width: 32, height: 32, padding: '4px'}
  },
  'create.python-task': {
    'type': 'bpmn:Task',
    'group': 'code',
    'className': 'icon-custom python-task',
    'title': 'Python',
    'url': require('../../../../../assets/python-code.svg'),
    'attr': {x: 0, y: 0, width: 32, height: 32, padding: '4px'}
  },
  'create.email-task': {
    'type': 'bpmn:Task',
    'group': 'message',
    'className': 'icon-custom python-task',
    'title': '邮件',
    'url': require('../../../../../assets/python-code.svg'),
    'attr': {x: 0, y: 0, width: 32, height: 32, padding: '4px'}
  },
  'create.official-task': {
    'type': 'bpmn:Task',
    'group': 'message',
    'className': 'icon-custom python-task',
    'title': '公众号',
    'url': require('../../../../../assets/python-code.svg'),
    'attr': {x: 0, y: 0, width: 32, height: 32, padding: '4px'}
  },
  'create.wecom-task': {
    'type': 'bpmn:Task',
    'group': 'message',
    'className': 'icon-custom python-task',
    'title': '企业微信',
    'url': require('../../../../../assets/python-code.svg'),
    'attr': {x: 0, y: 0, width: 32, height: 32, padding: '4px'}
  },
  'create.ding-task': {
    'type': 'bpmn:Task',
    'group': 'message',
    'className': 'icon-custom python-task',
    'title': '钉钉',
    'url': require('../../../../../assets/python-code.svg'),
    'attr': {x: 0, y: 0, width: 32, height: 32, padding: '4px'}
  }
}

export { customConfig }
