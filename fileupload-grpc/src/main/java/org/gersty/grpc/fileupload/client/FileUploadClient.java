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
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FileUploadClient {
    private final ManagedChannel channel;
    private final File testFile;
    private final BufferedInputStream bInputStream;
    private final int bufferSize; // 1kb
    private final byte[] buffer;



    public FileUploadClient() throws FileNotFoundException {
        channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();
        testFile = ResourceUtils.getFile("classpath:test.txt");
        bInputStream = new BufferedInputStream(new FileInputStream(testFile));
        bufferSize = 1*1024;
        buffer = new byte[bufferSize];
    }

    public ManagedChannel getChannel(){ return this.channel; }

    public void runUnaryFileUpload() throws IOException {
        FileUploadServiceGrpc.FileUploadServiceBlockingStub unaryFileUploadStub = FileUploadServiceGrpc.newBlockingStub(channel);
        byte[] data = IOUtils.toByteArray(new FileInputStream(testFile));
        FileResponse response = unaryFileUploadStub.unaryFileUpload(FileRequest.newBuilder().setData(ByteString.copyFrom(data)).build());
        System.out.println(response.getStatus());
    }

    public void runStreamFileUpload() throws IOException, InterruptedException {
        final CountDownLatch finishLatch = new CountDownLatch(1);

        FileUploadServiceGrpc.FileUploadServiceStub streamFileUploadStub = FileUploadServiceGrpc.newStub(channel);

        StreamObserver<FileResponse> fileResponseStreamObserver = new StreamObserver<FileResponse>() {
            @Override
            public void onNext(FileResponse value) {
                System.out.println(value.getStatus());
                System.out.println("Client response onNext");
            }

            @Override
            public void onError(Throwable t) {

                System.out.println("Client response onError");
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {

                System.out.println("Client response onCompleted");
                finishLatch.countDown();
            }
        };

        StreamObserver<FileRequest> fileRequestObserver = streamFileUploadStub.streamFile(fileResponseStreamObserver);
        int size;
        while ((size = bInputStream.read(buffer)) > 0) {

            ByteString byteString = ByteString.copyFrom(buffer, 0, size);
            FileRequest req = FileRequest.newBuilder().setData(byteString).build();
            fileRequestObserver.onNext(req);
            Thread.sleep(1000);
            if (finishLatch.getCount() == 0) {
                System.out.println("HIT FINISH LATCH ERROR");
                // RPC completed or errored before we finished sending.
                // Sending further requests won't error, but they will just be thrown away.
                return;
            }

        }

        fileRequestObserver.onCompleted();

        if (!finishLatch.await(1, TimeUnit.MINUTES)) {
            System.out.println("recordRoute can not finish within 1 minutes");
        }

    }

    public void runBidirectionalStreamFileUpload() throws IOException, InterruptedException {
        FileUploadServiceGrpc.FileUploadServiceStub bidirectionalStreamFileUploadStub = FileUploadServiceGrpc.newStub(channel);

        StreamObserver<FileRequest> fileRequestObserver = bidirectionalStreamFileUploadStub.bidirectionalStreamFile(new StreamObserver<FileResponse>() {
            @Override
            public void onNext(FileResponse fileResponse) {
                System.out.println("*************MESSAGE FROM SERVER******************"+fileResponse.getMessage());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println(throwable.getMessage());
                throwable.printStackTrace();

            }

            @Override
            public void onCompleted() {

            }
        });

        uploadFile(fileRequestObserver);

    }


    private void uploadFile(StreamObserver<FileRequest> fileRequestObserver) throws IOException, InterruptedException {

    }

    public static void main(String[] args) throws IOException, InterruptedException {

        FileUploadClient client =  new FileUploadClient();

        //client.runUnaryFileUpload();
        client.runStreamFileUpload();
    //    Thread.sleep(500);
        //client.runBidirectionalStreamFileUpload();
        client.getChannel().shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }


}
