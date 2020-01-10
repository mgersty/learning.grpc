package org.gersty.grpc.fileupload.server;

import com.google.protobuf.ByteString;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.gersty.grpc.standalone.fileupload.FileRequest;
import org.gersty.grpc.standalone.fileupload.FileUploadServiceGrpc;
import org.gersty.grpc.standalone.fileupload.FileResponse;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

class FileUploadService extends FileUploadServiceGrpc.FileUploadServiceImplBase {
    private int mStatus = 200;
    private String mMessage = "";
    private BufferedOutputStream mBufferedOutputStream = null;

    @Override
    public StreamObserver<FileRequest> upload(final StreamObserver<FileResponse> responseObserver){

        return new StreamObserver<FileRequest>() {
            @Override
            public void onNext(FileRequest file) {
                ByteString stuff = file.getData();
                System.out.println("-- STREAM FROM CLIENT -->"+new String(stuff.toByteArray()));
                responseObserver.onNext(FileResponse.newBuilder().setStatus("success").build());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println(throwable.getMessage());
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(FileResponse.newBuilder().setStatus("Final Success").build());
                responseObserver.onCompleted();
            }
        };
    }
}
