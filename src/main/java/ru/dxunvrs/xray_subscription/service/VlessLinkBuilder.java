package ru.dxunvrs.xray_subscription.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class VlessLinkBuilder {
    @Value("${xray.server.ip}")
    private String serverIp;

    @Value("${xray.server.port}")
    private int serverPort;

    @Value("${xray.reality.publicKey}")
    private String publicKey;

    @Value("${xray.reality.shortId}")
    private String shortId;

    @Value("${xray.reality.sni}")
    private String sni;

    @Value("${xray.reality.fp}")
    private String fingerprint;

    public String buildVlessLink(String userUuid,
                                 String userEmail) {
        return UriComponentsBuilder.newInstance()
                .scheme("vless")
                .userInfo(userUuid)
                .host(serverIp)
                .port(serverPort)
                .queryParam("type", "tcp")
                .queryParam("security", "reality")
                .queryParam("pbk", publicKey)
                .queryParam("fp", fingerprint)
                .queryParam("sni", sni)
                .queryParam("sid", shortId)
                .queryParam("flow", "xtls-rprx-vision")
                .fragment(userEmail)
                .build()
                .toUriString();
    }
}
