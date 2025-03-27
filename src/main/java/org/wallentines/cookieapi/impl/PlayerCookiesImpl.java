package org.wallentines.cookieapi.impl;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.CompletableFuture;

public class PlayerCookiesImpl {


    public static CompletableFuture<byte[]> getCookie(ServerPlayer player, ResourceLocation location) {
        return ((CookieExtension) player.connection).requestCookie(location);
    }

    public static void setCookie(ServerPlayer player, ResourceLocation location, byte[] cookie) {
        ((CookieExtension) player.connection).storeCookie(location, cookie);
    }

}
