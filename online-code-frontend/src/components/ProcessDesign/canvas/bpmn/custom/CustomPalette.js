import { assign } from 'min-dash';
import { customConfig } from "../utils/bpmn-util";
import { getDi } from "bpmn-js/lib/util/ModelUtil.js";

/**
 * A palette that allows you to create BPMN _and_ custom elements.
 */
export default function PaletteProvider(palette, elementFactory, globalConnect, modeling, create) {
  this.elementFactory = elementFactory
  this.globalConnect = globalConnect
  this.modeling = modeling
  this.create = create

  palette.registerProvider(this)
}

PaletteProvider.$inject = [
  'palette',
  'elementFactory',
  'globalConnect',
  'modeling',
  'create'
]

PaletteProvider.prototype.getPaletteEntries = function (element) {
  const {
    modeling,
    create,
    elementFactory
  } = this;

  function createAction(dataAction, type, group, className, title, options) {

    function createListener(event) {
      const shape = elementFactory.createShape(assign({type: type}, options));
      if (options) {
        getDi(shape).isExpanded = options.isExpanded;
      }
      shape.businessObject.name = title;
      const {attr} = customConfig[dataAction]
      shape.width = attr.width;
      shape.height = attr.height;
      modeling.updateProperties(shape, { dataAction: dataAction });
      create.start(event, shape);
    }

    const shortType = type.replace(/^bpmn:/, '');
    return {
      group: group,
      className: className,
      title: title || 'Create ' + shortType,
      action: {
        dragstart: createListener,
        click: createListener
      }
    };
  }

  const actions = {};
  for (const key in customConfig) {
    actions[key] = createAction(key, customConfig[key]['type'], customConfig[key]['group'],
      customConfig[key]['className'], customConfig[key]['title'], {});
  }
  return actions;
}
