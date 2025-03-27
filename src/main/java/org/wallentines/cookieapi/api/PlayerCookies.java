package org.wallentines.cookieapi.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.wallentines.cookieapi.impl.PlayerCookiesImpl;

import java.util.concurrent.CompletableFuture;

public interface PlayerCookies {

    static CompletableFuture<byte[]> getCookie(ServerPlayer player, ResourceLocation location) {
        return PlayerCookiesImpl.getCookie(player, location);
    }

    static void setCookie(ServerPlayer player, ResourceLocation location, byte[] cookie) {
        PlayerCookiesImpl.setCookie(player, location, cookie);
    }

    static void clearCookie(ServerPlayer packetListener, ResourceLocation location) {
        setCookie(packetListener, location, new byte[0]);
    }


}
