package net.fabricmc.mante.discordrpc;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.DiscordBuild;
import com.jagrosh.discordipc.entities.RichPresence;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class RPCListener implements IPCListener {
    public static OffsetDateTime timestamp;

    @Override
    public void onReady(IPCClient client) {
        timestamp = OffsetDateTime.now();
        reloadRichPresence(client);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    client.connect(DiscordBuild.ANY);
                } catch (Throwable ignored) {
                }
                reloadRichPresence(client);
            }
        }, 0, 10000);
    }

    public static void reloadRichPresence(IPCClient client) {
        MinecraftClient mc = MinecraftClient.getInstance();

        RichPresence.Builder builder = new RichPresence.Builder()
                .setStartTimestamp(timestamp)
                .setLargeImage("grass_block", "Rich Presence developed by Mante");

        String state = "Idling";
        String details = "In menus";

        if (mc.getCurrentServerEntry() != null && mc.player != null) {
            details = "Playing on " + mc.getCurrentServerEntry().name;
            List<? extends PlayerEntity> players = mc.player.getEntityWorld().getPlayers();
            if (players.size() == 1)
                state = "Lonely";
            else if (players.size() == 2)
                state = "with " + mc.player.getEntityWorld().getPlayers().stream().filter(p -> !p.equals(mc.player)).map(p -> p.getName().getString()).collect(Collectors.joining(", "));
            else
                state = "with " + (players.size() - 1) + " players";
        }
        else if (mc.isInSingleplayer()) {
            state = "Lonely";
            details = "In a singleplayer world";
        }

        try {
            client.sendRichPresence(builder.setDetails(details).setState(state).build());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
