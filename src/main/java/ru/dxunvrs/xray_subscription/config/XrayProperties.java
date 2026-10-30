package ru.dxunvrs.xray_subscription.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "xray")
@Getter
@Setter
public class XrayProperties {

    private List<ServerConfig> servers = new ArrayList<>();

    private ServerDefaults server = new ServerDefaults();
    private RealityDefaults reality = new RealityDefaults();
    private InboundDefaults inbound = new InboundDefaults();
    private HappDefaults happ = new HappDefaults();

    @Getter
    @Setter
    public static class ServerDefaults {
        private String ip;
        private int port = 443;
    }

    @Getter
    @Setter
    public static class RealityDefaults {
        private String publicKey;
        private String shortId;
        private String sni;
        private String fp;
    }

    @Getter
    @Setter
    public static class InboundDefaults {
        private String tag = "vless-inbound";
    }

    @Getter
    @Setter
    public static class HappDefaults {
        private RoutingDefaults routing = new RoutingDefaults();

        @Getter
        @Setter
        public static class RoutingDefaults {
            private String rule;
        }
    }

    @Getter
    @Setter
    public static class ServerConfig {
        private String name;
        private String tag;
        private String ip;
        private Integer port;
        private String network; // tcp, xhttp
        private String flow;    // xtls-rprx-vision
        private String path;    // for xhttp
        private String mode;    // for xhttp (auto)
        private String publicKey;
        private String shortId;
        private String sni;
        private String fp;

        public String resolveIp(String defaultIp) {
            return ip != null && !ip.isBlank() ? ip : defaultIp;
        }

        public int resolvePort(int defaultPort) {
            return port != null ? port : defaultPort;
        }

        public String resolveTag(String defaultTag) {
            return tag != null && !tag.isBlank() ? tag : defaultTag;
        }

        public String resolveNetwork() {
            return network != null && !network.isBlank() ? network : "tcp";
        }

        public String resolveFlow() {
            if (flow != null) {
                return flow;
            }
            return "tcp".equalsIgnoreCase(resolveNetwork()) ? "xtls-rprx-vision" : "";
        }

        public String resolvePublicKey(String defaultPublicKey) {
            return publicKey != null && !publicKey.isBlank() ? publicKey : defaultPublicKey;
        }

        public String resolveShortId(String defaultShortId) {
            return shortId != null && !shortId.isBlank() ? shortId : defaultShortId;
        }

        public String resolveSni(String defaultSni) {
            return sni != null && !sni.isBlank() ? sni : defaultSni;
        }

        public String resolveFp(String defaultFp) {
            return fp != null && !fp.isBlank() ? fp : defaultFp;
        }
    }

    public List<ServerConfig> getEffectiveServers() {
        if (servers != null && !servers.isEmpty()) {
            return servers;
        }
        ServerConfig fallback = new ServerConfig();
        fallback.setName("Default");
        fallback.setTag(inbound.getTag());
        fallback.setPort(server.getPort());
        fallback.setIp(server.getIp());
        fallback.setNetwork("tcp");
        fallback.setFlow("xtls-rprx-vision");
        return List.of(fallback);
    }
}
