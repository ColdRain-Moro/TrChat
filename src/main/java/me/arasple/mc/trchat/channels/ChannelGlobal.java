package me.arasple.mc.trchat.channels;

import com.google.gson.JsonObject;
import io.izzel.taboolib.module.tellraw.TellrawJson;
import io.izzel.taboolib.util.chat.ComponentSerializer;
import me.arasple.mc.trchat.TrChatFiles;
import me.arasple.mc.trchat.chat.ChatFormats;
import me.arasple.mc.trchat.chat.obj.ChatType;
import me.arasple.mc.trchat.utils.Bungees;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Arasple
 * @date 2019/8/17 23:06
 */
public class ChannelGlobal {

    public static void execute(Player from, String message) {
        TellrawJson format = ChatFormats.getFormat(ChatType.GLOBAL, from).apply(from, message);
        String raw = ComponentSerializer.toString(format.getComponentsAll());
        List<String> avaliableChannels = TrChatFiles.getChannels().getStringList("CHANNELS").stream().filter(s -> Arrays.asList(s.split(";")).contains(String.valueOf(Bukkit.getPort()))).collect(Collectors.toList());
        Bungees.sendBungeeData(from, "TrChat", "BroadcastRaw", String.join(",",avaliableChannels), raw);
        format.send(Bukkit.getConsoleSender());
    }

}
