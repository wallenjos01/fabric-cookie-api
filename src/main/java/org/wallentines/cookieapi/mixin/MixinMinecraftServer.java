package org.wallentines.cookieapi.mixin;

import com.mojang.datafixers.DataFixer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.wallentines.cookieapi.api.ConfigurationCookieEvents;
import org.wallentines.cookieapi.impl.ServerExtension;

import java.net.Proxy;

@Mixin(MinecraftServer.class)
@Implements(@Interface(iface = ServerExtension.class, prefix = "cookieapi$"))
public class MixinMinecraftServer {

    @Unique
    private Event<ConfigurationCookieEvents.RequestCookies> cookieapi$serverEvent;

    @Inject(method="<init>", at=@At("TAIL"))
    private void onInit(Thread thread, LevelStorageSource.LevelStorageAccess levelStorageAccess, PackRepository packRepository, WorldStem worldStem, Proxy proxy, DataFixer dataFixer, Services services, ChunkProgressListenerFactory chunkProgressListenerFactory, CallbackInfo ci) {
        cookieapi$serverEvent = EventFactory.createArrayBacked(ConfigurationCookieEvents.RequestCookies.class, listeners -> (server, packetListener) -> {
            for(ConfigurationCookieEvents.RequestCookies cookie : listeners) {
                cookie.request(server, packetListener);
            }
        });

    }

    public Event<ConfigurationCookieEvents.RequestCookies> cookieapi$getRequestCookiesEvent() {
        return cookieapi$serverEvent;
    }


}
