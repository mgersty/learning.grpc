package org.gersty.grpc.fileupload.client;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.gersty.grpc.standalone.fileupload.FileRequest;
import org.gersty.grpc.standalone.fileupload.FileResponse;
import org.gersty.grpc.standalone.fileupload.FileUploadServiceGrpc;


import java.io.*;


import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class FileUploadClient {
    private static final Logger logger = Logger.getLogger(FileUploadClient.class.getName());
    private static final int PORT = 50051;

    private final ManagedChannel mChannel;
    private final FileUploadServiceGrpc.FileUploadServiceStub mAsyncStub;

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
    }

    public void shutdown() throws InterruptedException {
        mChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }


    public void startStream(final String filepath) throws FileNotFoundException {
        logger.info("tid: " + Thread.currentThread().getId() + ", Will try to getBlob");

        StreamObserver<FileResponse> responseObserver = new StreamObserver<FileResponse>() {

            @Override
            public void onNext(FileResponse value) {
                logger.info("Client response onNext");
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

        StreamObserver<FileRequest> requestObserver = mAsyncStub.upload(responseObserver);

        try {
            BufferedInputStream bInputStream = new BufferedInputStream(new FileInputStream(getClass().getClassLoader().getResource("test.txt").getFile()));

            int bufferSize = 1 * 1024; // 512k
            byte[] buffer = new byte[bufferSize];
            int size = 0;
            while ((size = bInputStream.read(buffer)) > 0) {
                ByteString byteString = ByteString.copyFrom(buffer, 0, size);
                FileRequest req = FileRequest.newBuilder().setName(filepath).setData(byteString).build();
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
            client.startStream("airplane_sky_flight_clouds.jpg");
            logger.info("Done with startStream");
        } finally {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            client.shutdown();
        }
    }
}
