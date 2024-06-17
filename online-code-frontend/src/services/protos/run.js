/*eslint-disable block-scoped-var, id-length, no-control-regex, no-magic-numbers, no-prototype-builtins, no-redeclare, no-shadow, no-var, sort-vars*/
"use strict";

var $protobuf = require("protobufjs/light");

var $root = ($protobuf.roots["default"] || ($protobuf.roots["default"] = new $protobuf.Root()))
.addJSON({
  com: {
    nested: {
      onlinecode: {
        nested: {
          admin: {
            nested: {
              proto: {
                options: {
                  java_package: "com.onlinecode.admin.proto",
                  java_outer_classname: "RunProto"
                },
                nested: {
                  Run: {
                    fields: {
                      procCode: {
                        type: "string",
                        id: 1
                      },
                      taskCode: {
                        type: "string",
                        id: 2
                      },
                      cmd: {
                        type: "string",
                        id: 3
                      },
                      vars: {
                        type: "string",
                        id: 4
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
});

module.exports = $root;
