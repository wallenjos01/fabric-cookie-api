package org.wallentines.cookieapi.impl;


import net.fabricmc.fabric.api.event.Event;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.wallentines.cookieapi.api.ConfigurationCookieEvents;

import java.util.concurrent.CompletableFuture;

public class ConfigurationCookiesImpl {

    public static CompletableFuture<byte[]> getCookie(ServerConfigurationPacketListener packetListener, ResourceLocation location) {
        return ((CookieExtension) packetListener).requestCookie(location);
    }

    public static void setCookie(ServerConfigurationPacketListener packetListener, ResourceLocation location, byte[] cookie) {
        ((CookieExtension) packetListener).storeCookie(location, cookie);
    }

    public static Event<ConfigurationCookieEvents.RequestCookies> getServerRequestCookiesEvent(MinecraftServer server) {
        return ((ServerExtension) server).getRequestCookiesEvent();
    }
}
