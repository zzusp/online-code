# online-code
线上编程方案的尝试

## online-code-frontend
### 项目启动
```
cd ./online-code-frontend
npm i -f
npm run start
``` 
### 初始化
![init](online-code-frontend-init.png)

D:\soft\protoc\bin\protoc.exe --plugin=protoc-gen-js=D:\soft\protoc\protobuf-javascript-3.21.2\bin\protoc-gen-js.exe --js_out=import_style=commonjs,binary:. --plugin=protoc-gen-grpc=.\protoc-gen-grpc-web-1.5.0-windows-x86_64.exe  --grpc-web_out=import_style=commonjs,mode=grpcwebtext:. .\process_run.proto

D:\soft\protoc\bin\protoc.exe --plugin=protoc-gen-grpc=.\protoc-gen-grpc-web-1.5.0-windows-x86_64.exe --grpc-web_out=import_style=commonjs,mode=grpcwebtext:. .\process_run.proto

D:\soft\protoc\bin\protoc.exe -I=. .\process_run.proto --js_out=import_style=commonjs:. --grpc-web_out=import_style=commonjs,mode=grpcwebtext:. --proto_path=.

protoc -I=. .\process_run.proto --js_out=import_style=commonjs:. --grpc-web_out=import_style=commonjs,mode=grpcwebtext:.


