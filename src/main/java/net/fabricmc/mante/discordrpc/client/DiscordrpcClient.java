package net.fabricmc.mante.discordrpc.client;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.entities.DiscordBuild;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.mante.discordrpc.RPCListener;

public class DiscordrpcClient implements ClientModInitializer {
    public static IPCClient client;

    @Override
    public void onInitializeClient() {
        client = new IPCClient(891345202592219206L);

        try {
            client.setListener(new RPCListener());
            client.connect(DiscordBuild.ANY);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
