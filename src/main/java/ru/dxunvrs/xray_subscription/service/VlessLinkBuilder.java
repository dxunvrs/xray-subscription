package ru.dxunvrs.xray_subscription.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import ru.dxunvrs.xray_subscription.config.XrayProperties;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VlessLinkBuilder {

    private final XrayProperties xrayProperties;

    public String buildSingleVlessLink(XrayProperties.ServerConfig server,
                                       String userUuid,
                                       String userEmail) {
        String ip = server.resolveIp(xrayProperties.getServer().getIp());
        int port = server.resolvePort(xrayProperties.getServer().getPort());
        String network = server.resolveNetwork();
        String flow = server.resolveFlow();
        String publicKey = server.resolvePublicKey(xrayProperties.getReality().getPublicKey());
        String shortId = server.resolveShortId(xrayProperties.getReality().getShortId());
        String sni = server.resolveSni(xrayProperties.getReality().getSni());
        String fp = server.resolveFp(xrayProperties.getReality().getFp());

        UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
                .scheme("vless")
                .userInfo(userUuid)
                .host(ip)
                .port(port)
                .queryParam("type", network)
                .queryParam("security", "reality")
                .queryParam("pbk", publicKey)
                .queryParam("fp", fp)
                .queryParam("sni", sni)
                .queryParam("sid", shortId);

        if (flow != null && !flow.isBlank()) {
            builder.queryParam("flow", flow);
        }
        if (server.getPath() != null && !server.getPath().isBlank()) {
            builder.queryParam("path", server.getPath());
        }
        if (server.getMode() != null && !server.getMode().isBlank()) {
            builder.queryParam("mode", server.getMode());
        }

        String fragment = userEmail;
        if (server.getName() != null && !server.getName().isBlank()) {
            fragment += " - " + server.getName();
        }
        builder.fragment(fragment);

        return builder.build().toUriString();
    }

    public String buildAllVlessLinks(String userUuid, String userEmail) {
        List<XrayProperties.ServerConfig> servers = xrayProperties.getEffectiveServers();
        return servers.stream()
                .map(s -> buildSingleVlessLink(s, userUuid, userEmail))
                .collect(Collectors.joining("\n"));
    }

    public String buildVlessLink(String userUuid, String userEmail) {
        return buildAllVlessLinks(userUuid, userEmail);
    }

    public String buildHappSubscription(String userUuid, String userEmail) {
        String vlessLinks = buildAllVlessLinks(userUuid, userEmail);
        String routingRule = xrayProperties.getHapp().getRouting().getRule();

        String combined = vlessLinks;
        if (routingRule != null && !routingRule.isBlank()) {
            combined += "\n" + routingRule;
        }

        return Base64.getEncoder().encodeToString(combined.getBytes(StandardCharsets.UTF_8));
    }
}
