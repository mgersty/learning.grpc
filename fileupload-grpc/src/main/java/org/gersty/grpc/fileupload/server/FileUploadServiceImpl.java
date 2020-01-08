package org.gersty.grpc.fileupload.server;



import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.gersty.grpc.fileupload.FileRequest;
import org.gersty.grpc.fileupload.FileResponse;
import org.gersty.grpc.fileupload.FileUploadServiceGrpc;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.stereotype.Component;
import java.util.Random;


@Component
@GRpcService
public class FileUploadServiceImpl extends FileUploadServiceGrpc.FileUploadServiceImplBase {

    @Override
    public void unaryFileUpload(FileRequest request, StreamObserver<FileResponse> response){
        ByteString stuff = request.getData();
        System.out.println("*************UNIARY******************"+new String(stuff.toByteArray()));

        response.onNext(FileResponse.newBuilder().setStatus("success").build());
        response.onCompleted();
    }

    @Override
    public StreamObserver<FileRequest> streamFile(StreamObserver<FileResponse> responseObserver){
        return new StreamObserver<FileRequest>() {
            @Override
            public void onNext(FileRequest fileRequest) {
                ByteString stuff = fileRequest.getData();
                System.out.println("*************STREAM******************"+new String(stuff.toByteArray()));
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

    @Override
    public StreamObserver<FileRequest> bidirectionalStreamFile(StreamObserver<FileResponse> responseStreamObserver){



        return new StreamObserver<FileRequest>() {
            @Override
            public void onNext(FileRequest fileRequest) {
                String message = ((new Random().nextInt(10) & 2) == 0) ? "message received":"message not received :(";
                FileResponse response = FileResponse.newBuilder().setMessage(message).build();
                responseStreamObserver.onNext(response);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                FileResponse response = FileResponse.newBuilder().setStatus("file upload complete").build();
                responseStreamObserver.onNext(response);
            }
        };
    }

}

