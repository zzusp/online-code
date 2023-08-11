/* eslint-disable no-unused-vars */
import inherits from 'inherits'

import BaseRenderer from 'diagram-js/lib/draw/BaseRenderer'
import { append as svgAppend, create as svgCreate } from 'tiny-svg'

import { customConfig } from "../utils/bpmn-util";
import { getLabel } from "bpmn-js/lib/features/label-editing/LabelUtil";
import { getDi } from "bpmn-js/lib/util/ModelUtil.js";

/**
 * A renderer that knows how to render custom elements.
 */
export default function CustomRenderer(eventBus, textRenderer, elementRegistry, elementFactory, canvas) {
  BaseRenderer.call(this, eventBus, 2000)

  this.drawCustomElements = function (parentNode, element) {
    console.log(element)
    const rootId = element.parent.businessObject.id;
    // 获取到类型
    const type = element.type;
    if (type !== 'label') {
      const dataAction = element.businessObject.$attrs.hasOwnProperty('dataAction') ?
        element.businessObject.$attrs.dataAction : '';
      if (customConfig.hasOwnProperty(dataAction)) {
        // or customConfig[type]
        const {url, attr} = customConfig[dataAction];

        const rect = svgCreate('rect', {
          ...{x: -6, y: -6, width: attr.width + 12, height: attr.height + 12, rx: 5, ry: 5, fill: 'white', filter: 'url(#shadow)'}
        });
        svgAppend(parentNode, rect);
        element['width'] = attr.width; // 这里我是取了巧, 直接修改了元素的宽高
        element['height'] = attr.height;

        let customIcon = svgCreate('image', {
            ...attr,
            href: url
          });
        svgAppend(parentNode, customIcon);

        // elementRegistry.get(element.businessObject.id)用来判断拖拽操作是否结束
        if (elementRegistry.get(element.businessObject.id) && !!getDi(element).bounds.width) {
          // 重新绘制label
          const labelDimensions = textRenderer.getExternalLabelBounds({width: 90, height: 20}, getLabel(element));
          let label = elementRegistry.get(element.businessObject.id + '_label');

          if (!label && type === 'bpmn:Task') {
            this.createLabel(element, labelDimensions, rootId);
          } else if (label && type !== 'bpmn:Task') {
            // 先删除
            canvas.removeShape(element.businessObject.id + '_label');
            element.labels.remove(element.labels['0']);
            this.createLabel(element, labelDimensions, rootId);
          }
        }
        return customIcon;
      }
      // return this.bpmnRenderer.drawShape(parentNode, element);
    }
  }

  this.createLabel = function(element, labelDimensions, rootId) {
    const x = Math.round(getDi(element).bounds.x + element.width / 2 - labelDimensions.width / 2);
    const y = Math.round(getDi(element).bounds.y + element.height + 14);
    // 后新增
    let label = elementFactory.createLabel({
      id: element.businessObject.id + '_label',
      labelTarget: element,
      businessObject: element.businessObject,
      x: x,
      y: y,
      width: labelDimensions.width,
      height: labelDimensions.height
    });
    canvas.addShape(label, elementRegistry.get(rootId));
  }
}

inherits(CustomRenderer, BaseRenderer)

CustomRenderer.$inject = ['eventBus', 'textRenderer', 'elementRegistry', 'elementFactory', 'canvas']

CustomRenderer.prototype.canRender = function (element) {
  // ignore labels
  return true
  // return !element.labelTarget;
}

CustomRenderer.prototype.drawShape = function (p, element) {
  return this.drawCustomElements(p, element)
}

CustomRenderer.prototype.getShapePath = function (shape) {
  // console.log(shape)
}
