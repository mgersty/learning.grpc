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
import org.springframework.util.ResourceUtils;

import java.io.*;

public class FileUploadClient {

    private static File testFile;

    static {
        try {
            testFile = ResourceUtils.getFile("classpath:test.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();

        byte[] data = IOUtils.toByteArray(new FileInputStream(testFile));

//        FileUploadServiceGrpc.FileUploadServiceBlockingStub unaryFileUploadStub = FileUploadServiceGrpc.newBlockingStub(channel);
//        FileUploadServiceGrpc.FileUploadServiceStub streamFileUploadStub = FileUploadServiceGrpc.newStub(channel);
        FileUploadServiceGrpc.FileUploadServiceStub bidirectionalStreamFileUploadStub = FileUploadServiceGrpc.newStub(channel);

//        System.out.println("************************************************UNARY FILE UPLOAD************************************************");
//
//        FileResponse response = unaryFileUploadStub.unaryFileUpload(FileRequest.newBuilder().setData(ByteString.copyFrom(data)).build());
//        System.out.println(response.getStatus());
//
//        System.out.println("************************************************STREAM FILE UPLOAD************************************************");
//        StreamObserver<FileResponse> responseObserver = new StreamObserver<FileResponse>() {
//
//            @Override
//            public void onNext(FileResponse value) {
//                System.out.println("Client response onNext");
//            }
//
//            @Override
//            public void onError(Throwable t) {
//                System.out.println("Client response onError");
//            }
//
//            @Override
//            public void onCompleted() {
//                System.out.println("Client response onCompleted");
//            }
//        };
//
//        StreamObserver<FileRequest> fileRequestObserver = streamFileUploadStub.streamFile(responseObserver);
//
        BufferedInputStream bInputStream = new BufferedInputStream(new FileInputStream(testFile));
        int bufferSize =  1 * 1024; // 1kb
        byte[] buffer = new byte[bufferSize];
        int size = 0;
//        System.out.println("******** BEGIN BUFFER READ ********");
//        while ((size = bInputStream.read(buffer)) > 0) {
//          //  Thread.sleep(10);
//            ByteString byteString = ByteString.copyFrom(buffer, 0, size);
//            FileRequest req = FileRequest.newBuilder().setData(byteString).build();
//            fileRequestObserver.onNext(req);
//        }

        System.out.println("************************************************Bi Di STREAM FILE UPLOAD************************************************");

        StreamObserver<FileRequest> fileUploadRequest = bidirectionalStreamFileUploadStub.bidirectionalStreamFile(new StreamObserver<FileResponse>() {
            @Override
            public void onNext(FileResponse fileResponse) {

              System.out.println("*************BI DI STREAM Messages******************"+fileResponse.getMessage());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                System.out.println("File upload attempt complete");
            }
        });



        BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(testFile));

        System.out.println("******** BEGIN Bidi BUFFER READ ********");
        while ((size = fileInputStream.read(buffer)) > 0) {
              Thread.sleep(10);
            ByteString byteString = ByteString.copyFrom(buffer, 0, size);
            FileRequest req = FileRequest.newBuilder().setData(byteString).build();
            fileUploadRequest.onNext(req);
        }
        
        channel.shutdown();




    }


}
