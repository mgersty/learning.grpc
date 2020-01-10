package org.gersty.grpc.fileupload.server;

import java.io.IOException;
import java.util.logging.Logger;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GRPCServer {
    private static final Logger logger = Logger.getLogger(FileUploadService.class.getName());
    private static final int PORT = 50051;
    private io.grpc.Server mServer;

    public static void main(String[] args) throws IOException, InterruptedException {
        final GRPCServer server = new GRPCServer();
        server.start();
        server.blockUntilShutdown();
    }

    private void start() throws IOException, IOException {
        /* The port on which the mServer should run UploadFileServer*/

        mServer = ServerBuilder.forPort(PORT)
                .addService(new FileUploadService())
                .build()
                .start();
        logger.info("Server started, listening on " + PORT);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC mServer since JVM is shutting down");
                GRPCServer.this.stop();
                System.err.println("*** mServer shut down");
            }
        });
    }

    private void stop() {
        if (mServer != null) {
            mServer.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (mServer != null) {
            mServer.awaitTermination();
        }
    }


}