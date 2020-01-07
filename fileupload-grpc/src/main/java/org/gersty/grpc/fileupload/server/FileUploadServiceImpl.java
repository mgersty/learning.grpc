package org.gersty.grpc.fileupload.server;



import org.lognet.springboot.grpc.GRpcService;
import org.gersty.grpc.fileupload.HelloServiceGrpc;
import org.springframework.stereotype.Component;

@Component
@GRpcService
public class FileUploadServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {


}

