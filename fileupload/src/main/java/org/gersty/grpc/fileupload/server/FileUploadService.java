package org.gersty.grpc.fileupload.server;

import io.grpc.stub.StreamObserver;
import org.gersty.grpc.standalone.fileupload.Request;
import org.gersty.grpc.standalone.fileupload.FileUploadServiceGrpc;
import org.gersty.grpc.standalone.fileupload.Response;

import java.io.BufferedOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;

class FileUploadService extends FileUploadServiceGrpc.FileUploadServiceImplBase {
    private int mStatus = 200;
    private String mMessage = "";
    private BufferedOutputStream mBufferedOutputStream = null;
    private static final Logger logger = Logger.getLogger(FileUploadService.class.getName());

    @Override
    public void single(Request request, final StreamObserver<Response> response){
        logger.info("Message from client: "+request.getValue());
        response.onNext(Response.newBuilder().setStatus("Success").setMessage("Message from Server").build());
        response.onCompleted();
    }

    @Override
    public StreamObserver<Request> upload(final StreamObserver<Response> responseObserver) {
        logger.info("****** Initialize Stream from Client to Server ****** ");
        return new StreamObserver<Request>() {
            @Override
            public void onNext(Request file) {
                logger.info("****** Data from client ******\n" + new String(file.getData().toByteArray()));
            }

            @Override
            public void onError(Throwable throwable) {
                logger.info(throwable.getMessage());
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(Response.newBuilder().setStatus("All messages received").build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void download(Request fileRequest, final StreamObserver<Response> responseObserver) {
        logger.info("****** Initialize Stream from Sever to Client ****** ");

        logger.info("****** Filter Names Containing Character****** " + fileRequest.getValue());

        names().stream()
                .map(String::toLowerCase)
                .filter(name-> name.contains(fileRequest.getValue().toLowerCase()))
                .collect(Collectors.toList())
            .forEach(name -> responseObserver.onNext(Response.newBuilder().setMessage(name).build()));

        responseObserver.onCompleted();

    }
    int mmCount = 0;

    @Override
    public StreamObserver<Request> bidirectional(final StreamObserver<Response> responseObserver) {
        logger.info("****** Initialize BiDi Stream for Client & Server ****** ");

        return new StreamObserver<Request>() {
            @Override
            public void onNext(Request file) {
                logger.info("sync counter: " + mmCount +"\n"+"data received from client: " + new String(file.getData().toByteArray()));

                String message = ((new Random().nextInt(10) & 2) == 0) ? "message received":"message not received";
                responseObserver.onNext(Response.newBuilder().setMessage("sync counter: " + mmCount).setStatus(message+": "+new String(file.getData().toByteArray())).build());
                mmCount++;
            }

            @Override
            public void onError(Throwable throwable) {
                logger.info(throwable.getMessage());
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(Response.newBuilder().setStatus("All messages acknowledge").build());
                responseObserver.onCompleted();
            }
        };
    }




    private static List<String> names() {
        return Arrays.asList(
                "Sophia",
                "Jackson",
                "Emma",
                "Aiden",
                "Olivia",
                "Lucas",
                "Ava",
                "Liam",
                "Mia",
                "Noah",
                "Isabella",
                "Ethan",
                "Riley",
                "Mason",
                "Aria",
                "Caden",
                "Zoe",
                "Oliver",
                "Charlotte",
                "Elijah",
                "Lily",
                "Grayson",
                "Layla",
                "Jacob",
                "Amelia",
                "Michael",
                "Emily",
                "Benjamin",
                "Madelyn",
                "Carter",
                "Aubrey",
                "James",
                "Adalyn",
                "Jayden",
                "Madison",
                "Logan",
                "Chloe",
                "Alexander",
                "Harper",
                "Caleb",
                "Abigail",
                "Ryan",
                "Aaliyah",
                "Luke",
                "Avery",
                "Daniel",
                "Evelyn",
                "Jack",
                "Kaylee",
                "William",
                "Ella",
                "Owen",
                "Ellie",
                "Gabriel",
                "Scarlett",
                "Matthew",
                "Arianna",
                "Connor",
                "Hailey",
                "Jayce",
                "Nora",
                "Isaac",
                "Addison",
                "Sebastian",
                "Brooklyn",
                "Henry",
                "Hannah",
                "Muhammad",
                "Mila",
                "Cameron",
                "Leah",
                "Wyatt",
                "Elizabeth",
                "Dylan",
                "Sarah",
                "Nathan",
                "Eliana",
                "Nicholas",
                "Mackenzie",
                "Julian",
                "Peyton",
                "Eli",
                "Maria",
                "Levi",
                "Grace",
                "Isaiah",
                "Adeline",
                "Landon",
                "Elena",
                "David",
                "Anna",
                "Christian",
                "Victoria",
                "Andrew",
                "Camilla",
                "Brayden",
                "Lillian",
                "John",
                "Natalie",
                "Lincoln"
        );
    }

}
