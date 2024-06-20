package com.onlinecode.admin.grpc;

import io.grpc.stub.StreamObserver;

public class ProcessServiceGrpcImpl extends ProcessServiceGrpc.ProcessServiceImplBase {

    @Override
    public void processRun(ProcessRunProto.RunRequest request, StreamObserver<ProcessRunProto.RunResponse> responseObserver) {

        ProcessRunProto.RunResponse response = ProcessRunProto.RunResponse.newBuilder().setCode(0)
                .setMessage("success").build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
