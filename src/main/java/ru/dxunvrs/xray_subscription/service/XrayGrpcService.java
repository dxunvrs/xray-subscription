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
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.unix.DomainSocketAddress;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.dxunvrs.xray_subscription.config.XrayProperties;

@Service
@RequiredArgsConstructor
public class XrayGrpcService {
    @Value("${xray.grpc.target}")
    private String grpcTarget;

    private final XrayProperties xrayProperties;

    private ManagedChannel channel;
    private HandlerServiceGrpc.HandlerServiceBlockingStub handlerStub;
    private StatsServiceGrpc.StatsServiceBlockingStub statsStub;

    @PostConstruct
    public void init() {
        this.channel = NettyChannelBuilder.forAddress(new DomainSocketAddress(grpcTarget))
                .channelType(EpollDomainSocketChannel.class)
                .eventLoopGroup(new EpollEventLoopGroup())
                .usePlaintext()
                .build();
        this.handlerStub = HandlerServiceGrpc.newBlockingStub(channel);
        this.statsStub = StatsServiceGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }

    public void addUser(String email, String uuid) {
        for (XrayProperties.ServerConfig server : xrayProperties.getEffectiveServers()) {
            String tag = server.resolveTag(xrayProperties.getInbound().getTag());
            String flow = server.resolveFlow();
            addUserToInbound(email, uuid, tag, flow);
        }
    }

    private void addUserToInbound(String email, String uuid, String tag, String flow) {
        Account.Builder accountBuilder = Account.newBuilder().setId(uuid);
        if (flow != null && !flow.isBlank()) {
            accountBuilder.setFlow(flow);
        }

        TypedMessage typedAccount = TypedMessage.newBuilder()
                .setType("xray.proxy.vless.Account")
                .setValue(accountBuilder.build().toByteString())
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
                .setTag(tag)
                .setOperation(typedOperation)
                .build();

        handlerStub.alterInbound(request);
    }

    public void removeUser(String email) {
        for (XrayProperties.ServerConfig server : xrayProperties.getEffectiveServers()) {
            String tag = server.resolveTag(xrayProperties.getInbound().getTag());
            removeUserFromInbound(email, tag);
        }
    }

    private void removeUserFromInbound(String email, String tag) {
        RemoveUserOperation removeUserOperation = RemoveUserOperation.newBuilder()
                .setEmail(email)
                .build();

        TypedMessage typedOperation = TypedMessage.newBuilder()
                .setType("xray.app.proxyman.command.RemoveUserOperation")
                .setValue(removeUserOperation.toByteString())
                .build();

        AlterInboundRequest request = AlterInboundRequest.newBuilder()
                .setTag(tag)
                .setOperation(typedOperation)
                .build();

        handlerStub.alterInbound(request);
    }

    public long getUserUplink(String email, boolean reset) {
        return getStatValue("user>>>" + email + ">>>traffic>>>uplink", reset);
    }

    public long getUserDownlink(String email, boolean reset) {
        return getStatValue("user>>>" + email + ">>>traffic>>>downlink", reset);
    }

    private long getStatValue(String statName, boolean reset) {
        try {
            GetStatsRequest request = GetStatsRequest.newBuilder()
                    .setName(statName)
                    .setReset(reset)
                    .build();

            GetStatsResponse response = statsStub.getStats(request);
            return response.getStat().getValue();
        } catch (Exception e) {
            return 0L;
        }
    }
}
