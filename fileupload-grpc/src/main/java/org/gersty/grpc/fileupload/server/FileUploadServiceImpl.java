package org.gersty.grpc.fileupload.server;



import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.gersty.grpc.fileupload.FileRequest;
import org.gersty.grpc.fileupload.FileResponse;
import org.gersty.grpc.fileupload.FileUploadServiceGrpc;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.stereotype.Component;
import sun.rmi.transport.StreamRemoteCall;

@Component
@GRpcService
public class FileUploadServiceImpl extends FileUploadServiceGrpc.FileUploadServiceImplBase {

    @Override
    public void uploadFile(FileRequest request, StreamObserver<FileResponse> response){
        ByteString stuff = request.getData();
        System.out.println("*******************************"+new String(stuff.toByteArray()));

        response.onNext(FileResponse.newBuilder().setStatus("success").build());
        response.onCompleted();
    }

    @Override
    public StreamObserver<FileRequest> streamFile(StreamObserver<FileResponse> responseObserver){
        return new StreamObserver<FileRequest>() {
            @Override
            public void onNext(FileRequest fileRequest) {
                ByteString stuff = fileRequest.getData();
                System.out.println("*******************************"+new String(stuff.toByteArray()));
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(FileResponse.newBuilder().setStatus("success").build());
                responseObserver.onCompleted();
            }
        };
    }


}

