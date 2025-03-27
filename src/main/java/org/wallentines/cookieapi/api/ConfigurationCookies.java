package org.wallentines.cookieapi.api;

import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.resources.ResourceLocation;
import org.wallentines.cookieapi.impl.ConfigurationCookiesImpl;

import java.util.concurrent.CompletableFuture;

public interface ConfigurationCookies {

    static CompletableFuture<byte[]> getCookie(ServerConfigurationPacketListener packetListener, ResourceLocation location) {
        return ConfigurationCookiesImpl.getCookie(packetListener, location);
    }

    static void setCookie(ServerConfigurationPacketListener packetListener, ResourceLocation location, byte[] cookie) {
        ConfigurationCookiesImpl.setCookie(packetListener, location, cookie);
    }

    static void clearCookie(ServerConfigurationPacketListener packetListener, ResourceLocation location) {
        setCookie(packetListener, location, new byte[0]);
    }

}
