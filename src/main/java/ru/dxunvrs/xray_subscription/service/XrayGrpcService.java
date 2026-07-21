package ru.dxunvrs.xray_subscription.service;

import com.xray.app.proxyman.command.AddUserOperation;
import com.xray.app.proxyman.command.AlterInboundRequest;
import com.xray.app.proxyman.command.HandlerServiceGrpc;
import com.xray.app.proxyman.command.RemoveUserOperation;
import com.xray.common.protocol.User;
import com.xray.common.serial.TypedMessage;
import com.xray.proxy.vless.Account;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class XrayGrpcService {
    @Value("${xray.grpc.host}")
    private String grpcHost;

    @Value("${xray.grpc.port}")
    private int grpcPort;

    @Value("${xray.inbound.tag}")
    private String inboundTag;

    private ManagedChannel channel;
    private HandlerServiceGrpc.HandlerServiceBlockingStub handlerStub;

    @PostConstruct
    public void init() {
        this.channel = ManagedChannelBuilder.forAddress(grpcHost, grpcPort)
                .usePlaintext()
                .build();
        this.handlerStub = HandlerServiceGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }

    public void addVlessUser(String email,
                             String uuid) {
        Account vlessAccount = Account.newBuilder()
                .setId(uuid)
                .setFlow("xtls-rprx-vision")
                .build();

        TypedMessage typedAccount = TypedMessage.newBuilder()
                .setType("xray.proxy.vless.Account")
                .setValue(vlessAccount.toByteString())
                .build();

        User user = User.newBuilder()
                .setEmail(email)
                .setLevel(0)
                .setAccount(typedAccount)
                .build();

        AddUserOperation addUserOperation = AddUserOperation.newBuilder()
                .setUser(user)
                .build();

        TypedMessage typedOperation = TypedMessage.newBuilder()
                .setType("xray.app.proxyman.command.AddUserOperation")
                .setValue(addUserOperation.toByteString())
                .build();

        AlterInboundRequest request = AlterInboundRequest.newBuilder()
                .setTag(inboundTag)
                .setOperation(typedOperation)
                .build();

        handlerStub.alterInbound(request);
    }

    public void removeUser(String email) {
        RemoveUserOperation removeUserOperation = RemoveUserOperation.newBuilder()
                .setEmail(email)
                .build();

        TypedMessage typedOperation = TypedMessage.newBuilder()
                .setType("xray.app.proxyman.command.RemoveUserOperation")
                .setValue(removeUserOperation.toByteString())
                .build();

        AlterInboundRequest request = AlterInboundRequest.newBuilder()
                .setTag(inboundTag)
                .setOperation(typedOperation)
                .build();

        handlerStub.alterInbound(request);
    }
}
