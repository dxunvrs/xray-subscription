package ru.dxunvrs.xray_subscription.service;

import com.xray.app.proxyman.command.AddUserOperation;
import com.xray.app.proxyman.command.AlterInboundRequest;
import com.xray.app.proxyman.command.HandlerServiceGrpc;
import com.xray.app.proxyman.command.RemoveUserOperation;
import com.xray.app.stats.command.GetStatsRequest;
import com.xray.app.stats.command.GetStatsResponse;
import com.xray.app.stats.command.StatsServiceGrpc;
import com.xray.common.protocol.User;
import com.xray.common.serial.TypedMessage;
import com.xray.proxy.vless.Account;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class XrayGrpcService {
//    @Value("${xray.grpc.host}")
//    private String grpcHost;
//
//    @Value("${xray.grpc.port}")
//    private int grpcPort;

    @Value("${xray.grpc.target}")
    private String grpcTarget;

    @Value("${xray.inbound.tag}")
    private String inboundTag;

    private ManagedChannel channel;
    private HandlerServiceGrpc.HandlerServiceBlockingStub handlerStub;
    private StatsServiceGrpc.StatsServiceBlockingStub statsStub;

    @PostConstruct
    public void init() {
        this.channel = Grpc.newChannelBuilder(
                grpcTarget,
                InsecureChannelCredentials.create()
        ).build();
        this.handlerStub = HandlerServiceGrpc.newBlockingStub(channel);
        this.statsStub = StatsServiceGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }

    public void addUser(String email,
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

    public long getUserUplink(String email) {
        return getStatValue("user>>>" + email + ">>>traffic>>>uplink");
    }

    public long getUserDownlink(String email) {
        return getStatValue("user>>>" + email + ">>>downlink");
    }

    private long getStatValue(String statName) {
        try {
            GetStatsRequest request = GetStatsRequest.newBuilder()
                    .setName(statName)
                    .setReset(false)
                    .build();

            GetStatsResponse response = statsStub.getStats(request);
            return response.getStat().getValue();
        } catch (Exception e) {
            return 0L;
        }
    }
}