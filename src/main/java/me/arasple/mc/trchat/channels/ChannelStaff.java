package me.arasple.mc.trchat.channels;

import com.google.common.collect.Lists;
import io.izzel.taboolib.module.tellraw.TellrawJson;
import io.izzel.taboolib.util.chat.ComponentSerializer;
import me.arasple.mc.trchat.TrChatFiles;
import me.arasple.mc.trchat.chat.ChatFormats;
import me.arasple.mc.trchat.chat.obj.ChatType;
import me.arasple.mc.trchat.utils.Bungees;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Arasple
 * @date 2019/8/16 17:02
 */
public class ChannelStaff {

    private static List<UUID> staffs = Lists.newArrayList();

    public static void execute(Player player, String message) {
        if (player.hasPermission("trchat.staff")) {
            TellrawJson format = ChatFormats.getFormat(ChatType.STAFF, player).apply(player, message);
            if (Bungees.isEnable()) {
                String raw = ComponentSerializer.toString(format.getComponentsAll());
                Bungees.sendBungeeData(player,"TrChat", "SendRawPerm", raw, "trchat.staff");
            } else {
                Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("trchat.staff")).forEach(format::send);
            }
        }
    }

    public static boolean switchStaff(Player player) {
        if (!staffs.contains(player.getUniqueId())) {
            staffs.add(player.getUniqueId());
        } else {
            staffs.remove(player.getUniqueId());
        }
        return staffs.contains(player.getUniqueId());
    }

    public static boolean isInStaffChannel(Player player) {
        return staffs.contains(player.getUniqueId());
    }

}
