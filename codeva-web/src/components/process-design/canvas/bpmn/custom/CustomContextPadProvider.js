import { assign } from 'min-dash';
import { customConfig } from "../utils/bpmn-util";
import EventBus from "../utils/event-bus.js";

export default function ContextPadProvider(contextPad, injector, translate, modeling, connect, create, bpmnFactory, elementFactory) {
  this.modeling = modeling
  this.translate = translate
  this.connect = connect
  this.create = create
  this.bpmnFactory = bpmnFactory
  this.elementFactory = elementFactory
  contextPad.registerProvider(this)
}

ContextPadProvider.$inject = [
  'contextPad',
  'injector',
  'translate',
  'modeling',
  'connect',
  'create',
  'bpmnFactory',
  'elementFactory'
]

ContextPadProvider.prototype.getContextPadEntries = function (element) {
  const {
    translate,
    modeling,
    connect,
    create,
    bpmnFactory,
    elementFactory
  } = this;

  // 编辑功能
  function edit() {
    // 调用全局Vue实例中的$EventBus事件总线中的$emit属性，发送事件"editNode",并携带e
    EventBus.publish("editNode", element);
  }

  // 复制功能
  function copy(event, e) {
    const type = e.type;
    const businessObject = bpmnFactory.create(type, {
      name: e.businessObject['name']
    });
    businessObject.$attrs.dataAction = e.businessObject.$attrs.dataAction
    const shape = elementFactory.createShape({type: type, businessObject: businessObject});
    const {attr} = customConfig[e.businessObject.$attrs.dataAction]
    shape.width = attr.width;
    shape.height = attr.height;
    create.start(event, shape, e);
  }

  // 删除功能
  function remove() {
    modeling.removeElements([element]);
  }

  // 连线功能
  function link(e) {
    connect.start(e, element, true)
  }

  const baseActions = {
    'delete': {
      group: 'model',
      className: 'icon-custom icon-custom-delete',
      title: translate('删除'),
      action: {
        click: remove
      }
    }
  };
  if (element.type === 'label') {
    return baseActions;
  }

  if (element.type === 'bpmn:SequenceFlow') {
    const editAction = {
      'edit': {
        group: 'model',
        className: 'icon-custom-edit',
        title: translate('编辑'),
        action: {
          click: edit
        }
      }
    }
    assign(editAction, baseActions);
    return editAction;
  }
  const actions = {
    'edit': {
      group: 'model',
      className: 'icon-custom-edit',
      title: translate('编辑'),
      action: {
        click: edit
      }
    },
    'connect': {
      group: 'model',
      className: 'icon-custom icon-custom-connection-multi',
      title: translate('连线'),
      action: {
        click: link
      }
    },
    'copy': {
      group: 'model',
      className: 'icon-custom-copy',
      title: translate('复制'),
      action: {
        click: copy
      }
    }
  };
  assign(actions, baseActions);
  return actions
}
