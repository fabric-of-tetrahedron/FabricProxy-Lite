package one.oktw.mixin.hack;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import one.oktw.FabricProxyLite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.SocketAddress;

@Mixin(ServerLoginNetworkHandler.class)
public class ServerLoginNetworkHandler_SkipKeyPacket {
    @Redirect(method = "onHello", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;isLocal()Z"))
    private boolean checkIsLocal(ClientConnection connection) {
        // To make local address (Velocity) be able to connect the server. 
        // WARN: This also allows local players to join the server without login.
        if (FabricProxyLite.config.getAllowLocalOffline()) {
            String ip = getAddressAsString(connection.getAddress(), true);
            if (ip.startsWith(FabricProxyLite.config.getLocalIp())) {
                return true;
            }
            return false;
        }
        return connection.isLocal();
    }

    @Redirect(method = "onHello", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;isOnlineMode()Z"))
    private boolean skipKeyPacket(MinecraftServer minecraftServer) {
        if (!FabricProxyLite.config.getallowBypassProxy())
            return false;
        return minecraftServer.isOnlineMode();
    }


    private String getAddressAsString(SocketAddress address, boolean logIps) {
        if (address == null) {
            return "local";
        } else {
            return logIps ? address.toString() : "IP hidden";
        }
    }
}
