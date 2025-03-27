package org.wallentines.cookieapi.mixin;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.ClientboundStoreCookiePacket;
import net.minecraft.network.protocol.cookie.ClientboundCookieRequestPacket;
import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.wallentines.cookieapi.impl.CookieExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(ServerLoginPacketListenerImpl.class)
@Implements(@Interface(iface= CookieExtension.class, prefix = "cookieapi$"))
public class MixinServerLoginPacketListener {

    @Shadow @Final
    Connection connection;

    @Unique
    private Map<ResourceLocation, List<CompletableFuture<byte[]>>> cookieapi$requestedCookies;

    @Inject(method="<init>", at=@At("TAIL"))
    private void onInit(MinecraftServer minecraftServer, Connection connection, boolean bl, CallbackInfo ci) {
        cookieapi$requestedCookies = new ConcurrentHashMap<>();
    }

    public CompletableFuture<byte[]> cookieapi$requestCookie(ResourceLocation location) {
        CompletableFuture<byte[]> future = new CompletableFuture<>();
        cookieapi$requestedCookies.computeIfAbsent(location, k -> new ArrayList<>()).add(future);
        connection.send(new ClientboundCookieRequestPacket(location));
        return future;
    }

    public boolean cookieapi$cookieReceived(ResourceLocation location, byte[] cookie) {

        List<CompletableFuture<byte[]>> futures = cookieapi$requestedCookies.remove(location);
        if(futures == null) return false;

        futures.forEach(future -> future.complete(cookie));
        return true;
    }

    public void cookieapi$storeCookie(ResourceLocation location, byte[] cookie) {
        connection.send(new ClientboundStoreCookiePacket(location, cookie));
    }

    @Inject(method="handleCookieResponse", at=@At("HEAD"), cancellable = true)
    private void handleCookieResponse(ServerboundCookieResponsePacket packet, CallbackInfo ci) {
        cookieapi$cookieReceived(packet.key(), packet.payload());
        ci.cancel();
    }

}
