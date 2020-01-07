package org.gersty.grpc.fileupload.client;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.apache.commons.io.IOUtils;
import org.gersty.grpc.fileupload.FileRequest;
import org.gersty.grpc.fileupload.FileResponse;
import org.gersty.grpc.fileupload.FileUploadServiceGrpc;
import org.gersty.grpc.fileupload.server.FileUploadServiceImpl;

import java.io.*;

public class FileUploadClient {

    public static void main(String[] args) throws IOException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();

        byte[] data = IOUtils.toByteArray(new FileInputStream(new File("/home/mpg26/Desktop/hier_flat.nl.2.xml")));

        FileUploadServiceGrpc.FileUploadServiceBlockingStub stub = FileUploadServiceGrpc.newBlockingStub(channel);
        FileUploadServiceGrpc.FileUploadServiceStub asyncStub = FileUploadServiceGrpc.newStub(channel);

        
        FileResponse response = stub.uploadFile(FileRequest.newBuilder().setData(ByteString.copyFrom(data)).build());
        System.out.println(response.getStatus());

        StreamObserver<FileResponse> responseObserver = new StreamObserver<FileResponse>() {

            @Override
            public void onNext(FileResponse value) {
                System.out.println("Client response onNext");
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Client response onError");
            }

            @Override
            public void onCompleted() {
                System.out.println("Client response onCompleted");
            }
        };

        StreamObserver<FileRequest> fileRequestObserver = asyncStub.streamFile(responseObserver);
        
        BufferedInputStream bInputStream = new BufferedInputStream(new FileInputStream(new File("/home/mpg26/Desktop/hier_flat.nl.2.xml")));
        int bufferSize = 512 * 1024; // 1kb
        byte[] buffer = new byte[bufferSize];
        int size = 0;
        while ((size = bInputStream.read(buffer)) > 0) {
            ByteString byteString = ByteString.copyFrom(buffer, 0, size);
            FileRequest req = FileRequest.newBuilder().setData(byteString).build();
            fileRequestObserver.onNext(req);
        }
        
        channel.shutdown();




    }


}
