package org.wallentines.cookieapi.mixin;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.ClientboundStoreCookiePacket;
import net.minecraft.network.protocol.cookie.ClientboundCookieRequestPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.wallentines.cookieapi.api.ConfigurationCookieEvents;
import org.wallentines.cookieapi.impl.AwaitCookiesTask;
import org.wallentines.cookieapi.impl.ConfigurationCookieExtension;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(ServerConfigurationPacketListenerImpl.class)
@Implements(@Interface(iface = ConfigurationCookieExtension.class, prefix = "cookieapi$"))
public abstract class MixinServerConfigurationPacketListener extends ServerCommonPacketListenerImpl {

    @Shadow @Final private Queue<ConfigurationTask> configurationTasks;

    public MixinServerConfigurationPacketListener(MinecraftServer minecraftServer, Connection connection, CommonListenerCookie commonListenerCookie) {
        super(minecraftServer, connection, commonListenerCookie);
    }

    @Shadow protected abstract void finishCurrentTask(ConfigurationTask.Type type);

    @Unique
    private Map<ResourceLocation, List<CompletableFuture<byte[]>>> cookieapi$requestedCookies;

    @Unique
    private AtomicBoolean cookieapi$awaitingCookies;

    @Inject(method="<init>", at=@At("TAIL"))
    private void onInit(MinecraftServer minecraftServer, Connection connection, CommonListenerCookie commonListenerCookie, CallbackInfo ci) {
        cookieapi$requestedCookies = new ConcurrentHashMap<>();
        cookieapi$awaitingCookies = new AtomicBoolean(false);
    }

    @Inject(method="addOptionalTasks", at=@At("TAIL"))
    private void addCookieTask(CallbackInfo ci) {
        ServerConfigurationPacketListenerImpl listener = (ServerConfigurationPacketListenerImpl) (Object) this;
        ConfigurationCookieExtension ext = (ConfigurationCookieExtension) listener;
        configurationTasks.add(new AwaitCookiesTask(ext));
    }

    public CompletableFuture<byte[]> cookieapi$requestCookie(ResourceLocation location) {
        CompletableFuture<byte[]> future = new CompletableFuture<>();
        cookieapi$requestedCookies.computeIfAbsent(location, k -> new ArrayList<>()).add(future);
        this.send(new ClientboundCookieRequestPacket(location));
        return future;
    }

    public boolean cookieapi$cookieReceived(ResourceLocation location, byte[] cookie) {

        if(!cookieapi$awaitingCookies.get()) return false;

        List<CompletableFuture<byte[]>> futures = cookieapi$requestedCookies.remove(location);
        if(futures == null) return false;

        futures.forEach(future -> future.complete(cookie));

        if(cookieapi$requestedCookies.isEmpty()) {
            cookieapi$awaitingCookies.set(false);
            finishCurrentTask(AwaitCookiesTask.TYPE);
        }

        return true;
    }

    public void cookieapi$storeCookie(ResourceLocation location, byte[] cookie) {
        send(new ClientboundStoreCookiePacket(location, cookie));
    }

    public void cookieapi$requestCookies() {
        cookieapi$awaitingCookies.set(true);

        ServerConfigurationPacketListenerImpl self = (ServerConfigurationPacketListenerImpl) (Object) this;

        ConfigurationCookieEvents.REQUEST_COOKIES.invoker().request(server, self);
        ConfigurationCookieEvents.getServerRequestCookiesEvent(server).invoker().request(server, self);

        if(cookieapi$requestedCookies.isEmpty()) {
            cookieapi$awaitingCookies.set(false);
            finishCurrentTask(AwaitCookiesTask.TYPE);
        }
    }
}
