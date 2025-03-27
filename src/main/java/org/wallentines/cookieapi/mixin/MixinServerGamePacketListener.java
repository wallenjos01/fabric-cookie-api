package org.wallentines.cookieapi.mixin;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.ClientboundStoreCookiePacket;
import net.minecraft.network.protocol.cookie.ClientboundCookieRequestPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.wallentines.cookieapi.impl.CookieExtension;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(ServerGamePacketListenerImpl.class)
@Implements(@Interface(iface= CookieExtension.class, prefix = "cookieapi$"))
public class MixinServerGamePacketListener {

    @Unique
    private Map<ResourceLocation, List<CompletableFuture<byte[]>>> cookieapi$requestedCookies;

    @Inject(method="<init>", at=@At("TAIL"))
    private void onInit(MinecraftServer minecraftServer, Connection connection, ServerPlayer serverPlayer, CommonListenerCookie commonListenerCookie, CallbackInfo ci) {
        cookieapi$requestedCookies = new ConcurrentHashMap<>();
    }

    public CompletableFuture<byte[]> cookieapi$requestCookie(ResourceLocation location) {
        CompletableFuture<byte[]> future = new CompletableFuture<>();
        cookieapi$requestedCookies.computeIfAbsent(location, k -> new ArrayList<>()).add(future);
        ((ServerGamePacketListenerImpl) (Object) this).send(new ClientboundCookieRequestPacket(location));
        return future;
    }

    public boolean cookieapi$cookieReceived(ResourceLocation location, byte[] cookie) {
        List<CompletableFuture<byte[]>> futures = cookieapi$requestedCookies.remove(location);
        if(futures == null) return false;

        futures.forEach(future -> future.complete(cookie));
        return true;
    }

    public void cookieapi$storeCookie(ResourceLocation location, byte[] cookie) {
        ((ServerGamePacketListenerImpl) (Object) this).send(new ClientboundStoreCookiePacket(location, cookie));
    }

}
