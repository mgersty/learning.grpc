package org.gersty.grpc.fileupload.client;

import com.google.protobuf.ByteString;
import com.sun.corba.se.pept.transport.ResponseWaitingRoom;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.gersty.grpc.standalone.fileupload.Request;
import org.gersty.grpc.standalone.fileupload.Response;
import org.gersty.grpc.standalone.fileupload.FileUploadServiceGrpc;


import java.io.*;


import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class FileUploadClient {
    private static final Logger logger = Logger.getLogger(FileUploadClient.class.getName());
    private static final int PORT = 50051;
    public static final String test_file = "test.txt";

    private final ManagedChannel mChannel;
    private final FileUploadServiceGrpc.FileUploadServiceStub mAsyncStub;
    private final FileUploadServiceGrpc.FileUploadServiceBlockingStub blockingStub;

    public FileUploadClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext(true)
                .build());
    }

    FileUploadClient(ManagedChannel channel) {
        this.mChannel = channel;
        mAsyncStub = FileUploadServiceGrpc.newStub(channel);
        blockingStub = FileUploadServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        mChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }


    public void startSingle(){
        Response resp = blockingStub.single(Request.newBuilder().setValue("Hello from the client").build());
        logger.info(resp.getMessage()+" "+resp.getStatus());
    }

    public void startStream(final String filepath) throws FileNotFoundException {
        logger.info("tid: " + Thread.currentThread().getId() + ", Will try to getBlob");

        StreamObserver<Response> responseObserver = new StreamObserver<Response>() {

            @Override
            public void onNext(Response value) {
                logger.info("****** Final Response From Client ****** "+value.getStatus());
            }

            @Override
            public void onError(Throwable t) {
                logger.info("Client response onError");
            }

            @Override
            public void onCompleted() {
                logger.info("Client response onCompleted");
            }
        };

        StreamObserver<Request> requestObserver = mAsyncStub.upload(responseObserver);

        try {
            BufferedInputStream bInputStream = new BufferedInputStream(new FileInputStream(getClass().getClassLoader().getResource(filepath).getFile()));

            int bufferSize = 1 * 1024;
            byte[] buffer = new byte[bufferSize];
            int size = 0;
            while ((size = bInputStream.read(buffer)) > 0) {
                ByteString byteString = ByteString.copyFrom(buffer, 0, size);
                Request req = Request.newBuilder().setValue(filepath).setData(byteString).build();
                requestObserver.onNext(req);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        requestObserver.onCompleted();
}

    public void startDownload(){
        StreamObserver<Response> responseObserver = new StreamObserver<Response>() {

            @Override
            public void onNext(Response value) {

                logger.info("****** Responses From Server ****** "+value.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                logger.info("Client response onError");
            }

            @Override
            public void onCompleted() {
                logger.info("Server has completed sending messages to client");
            }
        };

        Request request = Request.newBuilder().setValue("w").build();
        mAsyncStub.download(request, responseObserver);


    }

    public void startBidirectional(final String filepath) throws FileNotFoundException {
        StreamObserver<Response> responseObserver = new StreamObserver<Response>() {

            @Override
            public void onNext(Response value) {
                logger.info("\n****** Response from server ******\n"+value.getMessage()+"\n"+value.getStatus());
            }

            @Override
            public void onError(Throwable t) {
                logger.info("Client response onError");
            }

            @Override
            public void onCompleted() {
                logger.info("****** All Messages sent from client ****** ");
            }
        };

        StreamObserver<Request> requestObserver = mAsyncStub.bidirectional(responseObserver);

        try {
            BufferedInputStream bInputStream = new BufferedInputStream(new FileInputStream(getClass().getClassLoader().getResource(filepath).getFile()));

            int bufferSize = 1 * 1024;
            byte[] buffer = new byte[bufferSize];
            int size = 0;
            while ((size = bInputStream.read(buffer)) > 0) {
                ByteString byteString = ByteString.copyFrom(buffer, 0, size);
                Request req = Request.newBuilder().setValue(filepath).setData(byteString).build();
                requestObserver.onNext(req);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        requestObserver.onCompleted();
    }


    public static void main(String[] args) throws Exception {
        FileUploadClient client = new FileUploadClient("localhost", PORT);
        try {
 //           client.startSingle();
  //          client.startStream(test_file);
 //           client.startDownload();
            client.startBidirectional(test_file);
        } finally {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            client.shutdown();
        }
    }
}
