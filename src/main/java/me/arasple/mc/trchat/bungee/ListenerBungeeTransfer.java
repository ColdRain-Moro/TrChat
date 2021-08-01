package me.arasple.mc.trchat.bungee;

import me.arasple.mc.trchat.TrChat;
import me.arasple.mc.trchat.TrChatFiles;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Arasple
 * @date 2019/8/16 18:40
 */
public class ListenerBungeeTransfer implements Listener {

    @EventHandler
    public void onTransfer(PluginMessageEvent e) {
        try {
            ByteArrayInputStream byteArray = new ByteArrayInputStream(e.getData());
            DataInputStream in = new DataInputStream(byteArray);

            String subChannel = in.readUTF();
            String type = in.readUTF();

            if ("TrChat".equals(subChannel)) {
                if ("SendRaw".equals(type)) {
                    String to = in.readUTF();
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayers().stream().filter(p -> p.getName().equalsIgnoreCase(to)).findFirst().orElse(null);

                    if (player != null && player.isConnected()) {
                        String raw = in.readUTF();
                        player.sendMessage(ComponentSerializer.parse(raw));
                    }
                }
                if ("BroadcastRaw".equals(type)) {
                    List<String> avaliableChannels = Arrays.asList(in.readUTF().split(","));
                    String raw = in.readUTF();
                    // port,sever
                    Map<String,String> servers = ProxyServer.getInstance().getServers().entrySet().stream().collect(Collectors.toMap(entry -> String.valueOf(entry.getValue().getAddress().getPort()),Map.Entry<String, ServerInfo>::getKey));
                    avaliableChannels.forEach(s -> {
                        List<String> strs = Arrays.asList(s.split(";"));
                        strs.forEach(po -> {
                            if (servers.containsKey(po)) {
                                // 向该服玩家广播
                                ProxyServer.getInstance().getServerInfo(servers.get(po)).getPlayers().forEach(p -> p.sendMessage(ComponentSerializer.parse(raw)));
                                // 从map中删除该服务器防止重复发送
                                servers.remove(s);
                            }
                        });
                    });
                }
                if ("SendRawPerm".equals(type)) {
                    String raw = in.readUTF();
                    String perm = in.readUTF();

                    ProxyServer.getInstance().getPlayers().stream().filter(p -> p.hasPermission(perm)).forEach(p -> {
                        p.sendMessage(ComponentSerializer.parse(raw));
                    });
                }
            }
        } catch (IOException ignored) {
        }
    }

}
