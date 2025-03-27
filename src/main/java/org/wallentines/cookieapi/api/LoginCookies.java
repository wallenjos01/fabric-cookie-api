package org.wallentines.cookieapi.api;

import net.minecraft.network.protocol.login.ServerLoginPacketListener;
import net.minecraft.resources.ResourceLocation;
import org.wallentines.cookieapi.impl.LoginCookiesImpl;

import java.util.concurrent.CompletableFuture;

public interface LoginCookies {

    static CompletableFuture<byte[]> getCookie(ServerLoginPacketListener packetListener, ResourceLocation location) {
        return LoginCookiesImpl.getCookie(packetListener, location);
    }

    static void setCookie(ServerLoginPacketListener packetListener, ResourceLocation location, byte[] cookie) {
        LoginCookiesImpl.setCookie(packetListener, location, cookie);
    }

    static void clearCookie(ServerLoginPacketListener packetListener, ResourceLocation location) {
        setCookie(packetListener, location, new byte[0]);
    }

}
