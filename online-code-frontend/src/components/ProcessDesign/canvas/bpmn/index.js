import Modeler from 'bpmn-js/lib/Modeler'

import inherits from 'inherits'

import CustomModule from './custom'

export default function CustomModeler(options) {
    Modeler.call(this, options);
}

inherits(CustomModeler, Modeler);

CustomModeler.prototype._modules = [].concat(
    CustomModeler.prototype._modules, [
        CustomModule
    ]
);
