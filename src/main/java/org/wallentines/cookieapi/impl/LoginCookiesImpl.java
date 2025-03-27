package org.wallentines.cookieapi.impl;

import net.minecraft.network.protocol.login.ServerLoginPacketListener;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public class LoginCookiesImpl {

    public static CompletableFuture<byte[]> getCookie(ServerLoginPacketListener packetListener, ResourceLocation location) {
        return ((CookieExtension) packetListener).requestCookie(location);
    }

    public static void setCookie(ServerLoginPacketListener packetListener, ResourceLocation location, byte[] cookie) {
        ((CookieExtension) packetListener).storeCookie(location, cookie);
    }
}
