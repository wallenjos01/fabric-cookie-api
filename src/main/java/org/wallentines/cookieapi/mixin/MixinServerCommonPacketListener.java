package org.wallentines.cookieapi.mixin;

import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.wallentines.cookieapi.impl.CookieExtension;

@Mixin(ServerCommonPacketListenerImpl.class)
public class MixinServerCommonPacketListener {

    @Inject(method="handleCookieResponse", at=@At("HEAD"), cancellable = true)
    private void onCookieResponse(ServerboundCookieResponsePacket packet, CallbackInfo ci) {
        ServerCommonPacketListenerImpl self = (ServerCommonPacketListenerImpl) (Object) this;
        if(self instanceof CookieExtension ext) {
            if(ext.cookieReceived(packet.key(), packet.payload())) {
                ci.cancel();
            }
        }
    }

}
