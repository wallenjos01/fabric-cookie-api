package org.wallentines.cookieapi.impl;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ConfigurationTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class AwaitCookiesTask implements ConfigurationTask {

    private final ConfigurationCookieExtension extension;

    public AwaitCookiesTask(ConfigurationCookieExtension extension) {
        this.extension = extension;
    }

    @Override
    public void start(Consumer<Packet<?>> consumer) {
        extension.requestCookies();
    }

    @Override
    public @NotNull Type type() {
        return TYPE;
    }

    public static final Type TYPE = new Type("await_cookies");
}
