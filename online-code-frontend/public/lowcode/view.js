!function e(t,n){"object"==typeof exports&&"object"==typeof module?module.exports=n():"function"==typeof define&&define.amd?define([],n):"object"==typeof exports?exports.BizComp=n():t.BizComp=n()}(window,(function(){return function(e){var t={};function n(r){if(t[r])return t[r].exports;var o=t[r]={i:r,l:!1,exports:{}};return e[r].call(o.exports,o,o.exports,n),o.l=!0,o.exports}return n.m=e,n.c=t,n.d=function(e,t,r){n.o(e,t)||Object.defineProperty(e,t,{enumerable:!0,get:r})},n.r=function(e){"undefined"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},n.t=function(e,t){if(1&t&&(e=n(e)),8&t)return e;if(4&t&&"object"==typeof e&&e&&e.__esModule)return e;var r=Object.create(null);if(n.r(r),Object.defineProperty(r,"default",{enumerable:!0,value:e}),2&t&&"string"!=typeof e)for(var o in e)n.d(r,o,function(t){return e[t]}.bind(null,o));return r},n.n=function(e){var t=e&&e.__esModule?function t(){return e.default}:function t(){return e};return n.d(t,"a",t),t},n.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)},n.p="",n(n.s=321)}({3:function(e,t){e.exports=window.React},321:function(e,t,n){e.exports=n(407)},322:function(e,t,n){},407:function(e,t,n){"use strict";n.r(t),n.d(t,"default",(function(){return O}));var r={};n.r(r),n.d(r,"default",(function(){return h}));var o=n(3);function u(e){return(u="function"==typeof Symbol&&"symbol"==typeof Symbol.iterator?function(e){return typeof e}:function(e){return e&&"function"==typeof Symbol&&e.constructor===Symbol&&e!==Symbol.prototype?"symbol":typeof e})(e)}function i(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}function f(e,t){for(var n=0;n<t.length;n++){var r=t[n];r.enumerable=r.enumerable||!1,r.configurable=!0,"value"in r&&(r.writable=!0),Object.defineProperty(e,l(r.key),r)}}function c(e,t,n){return t&&f(e.prototype,t),n&&f(e,n),Object.defineProperty(e,"prototype",{writable:!1}),e}function l(e){var t=a(e,"string");return"symbol"===u(t)?t:String(t)}function a(e,t){if("object"!==u(e)||null===e)return e;var n=e[Symbol.toPrimitive];if(void 0!==n){var r=n.call(e,t||"default");if("object"!==u(r))return r;throw new TypeError("@@toPrimitive must return a primitive value.")}return("string"===t?String:Number)(e)}function p(e,t){if("function"!=typeof t&&null!==t)throw new TypeError("Super expression must either be null or a function");e.prototype=Object.create(t&&t.prototype,{constructor:{value:e,writable:!0,configurable:!0}}),Object.defineProperty(e,"prototype",{writable:!1}),t&&s(e,t)}function s(e,t){return(s=Object.setPrototypeOf?Object.setPrototypeOf.bind():function e(t,n){return t.__proto__=n,t})(e,t)}function y(e){var t=m();return function n(){var r=v(e),o;if(t){var u=v(this).constructor;o=Reflect.construct(r,arguments,u)}else o=r.apply(this,arguments);return d(this,o)}}function d(e,t){if(t&&("object"===u(t)||"function"==typeof t))return t;if(void 0!==t)throw new TypeError("Derived constructors may only return object or undefined");return b(e)}function b(e){if(void 0===e)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return e}function m(){if("undefined"==typeof Reflect||!Reflect.construct)return!1;if(Reflect.construct.sham)return!1;if("function"==typeof Proxy)return!0;try{return Boolean.prototype.valueOf.call(Reflect.construct(Boolean,[],(function(){}))),!0}catch(e){return!1}}function v(e){return(v=Object.setPrototypeOf?Object.getPrototypeOf.bind():function e(t){return t.__proto__||Object.getPrototypeOf(t)})(e)}var h=function(e){p(n,e);var t=y(n);function n(e){var r;return i(this,n),(r=t.call(this,e)).state={name:e.name,element:e.element,onRender:e.onRender},r.ref=o.createRef(),r}return c(n,[{key:"componentDidMount",value:function e(){void 0!==this.state.onRender&&this.state.onRender(this.ref)}},{key:"render",value:function e(){return o.createElement("div",{className:"CustomRendererComponent"},o.createElement("div",{ref:this.ref},this.state.element||"no element"))}}]),n}(o.Component),j=n(322),O=h,w={},P="BizComp",g=!0;function S(e,t){return e.default?e.default:e[t]?e[t]:e}}})}));