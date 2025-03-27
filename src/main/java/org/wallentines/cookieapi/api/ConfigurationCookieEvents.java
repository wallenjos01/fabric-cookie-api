package org.wallentines.cookieapi.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.server.MinecraftServer;
import org.wallentines.cookieapi.impl.ConfigurationCookiesImpl;

public interface ConfigurationCookieEvents {

    Event<RequestCookies> REQUEST_COOKIES = EventFactory.createArrayBacked(RequestCookies.class, listeners -> (server, packetListener) -> {
        for(RequestCookies cookie : listeners) {
            cookie.request(server, packetListener);
        }
    });

    static Event<RequestCookies> getServerRequestCookiesEvent(MinecraftServer server) {
        return ConfigurationCookiesImpl.getServerRequestCookiesEvent(server);
    }

    interface RequestCookies {
        void request(MinecraftServer server, ServerConfigurationPacketListener packetListener);
    }

}
