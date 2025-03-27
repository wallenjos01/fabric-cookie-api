package org.wallentines.cookieapi.impl;

import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public interface CookieExtension {

    CompletableFuture<byte[]> requestCookie(ResourceLocation location);

    boolean cookieReceived(ResourceLocation location, byte[] cookie);

    void storeCookie(ResourceLocation location, byte[] cookie);

}
