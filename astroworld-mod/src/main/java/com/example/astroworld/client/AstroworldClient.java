package com.example.astroworld.client;

import com.example.astroworld.ConcertPackets;
import com.example.astroworld.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class AstroworldClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(AvatarModelLayers.AVATAR, AvatarModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.AVATAR, AvatarRenderer::new);

        ClientPlayNetworking.registerGlobalReceiver(ConcertPackets.START, (client, handler, buf, responseSender) -> {
            int duration = buf.readInt();
            client.execute(() -> ConcertClientState.start(client, duration));
        });

        ClientPlayNetworking.registerGlobalReceiver(ConcertPackets.STOP, (client, handler, buf, responseSender) ->
                client.execute(() -> ConcertClientState.stop(client)));

        ClientPlayNetworking.registerGlobalReceiver(ConcertPackets.BEAT, (client, handler, buf, responseSender) ->
                client.execute(ConcertClientState::beat));

        ClientTickEvents.END_CLIENT_TICK.register(ConcertClientState::tick);

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ConcertClientState.stop(client));
    }
}
