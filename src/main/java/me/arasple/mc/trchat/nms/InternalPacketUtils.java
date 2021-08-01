package me.arasple.mc.trchat.nms;

import io.izzel.taboolib.module.lite.SimpleReflection;
import me.arasple.mc.trchat.filter.ChatFilter;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Arasple
 * @date 2019/11/30 11:16
 */
public class InternalPacketUtils extends AbstractPacketUtils {

    static {
        SimpleReflection.checkAndSave(IChatBaseComponent.class);
        SimpleReflection.checkAndSave(NonNullList.class);
        SimpleReflection.checkAndSave(PacketPlayOutChat.class);
        SimpleReflection.checkAndSave(PacketPlayOutWindowItems.class);
        SimpleReflection.checkAndSave(PacketPlayOutSetSlot.class);
    }

    @Override
    public Object filterIChatComponent(Object component) {
        try {
            String raw = IChatBaseComponent.ChatSerializer.a((IChatBaseComponent) component);
            String filtered = ChatFilter.filter(raw).getFiltered();
            return IChatBaseComponent.ChatSerializer.a(filtered);
        } catch (Throwable throwable) {
            return component;
        }
    }

    @Override
    public void filterItem(Object item) {
        ItemStack itemStack = CraftItemStack.asCraftMirror((net.minecraft.server.v1_16_R3.ItemStack) item);
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return;

        }
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            if (meta.hasLore() && meta.getLore() != null && meta.getLore().size() > 0) {
                List<String> lore = new ArrayList<>();
                meta.getLore().forEach(l -> lore.add(ChatFilter.filter(l).getFiltered()));
                meta.setLore(lore);
            }
            if (meta.hasDisplayName()) {
                String tran = ChatFilter.filter(meta.getDisplayName()).getFiltered();
                meta.setDisplayName(meta.getDisplayName().charAt(0) != ChatColor.COLOR_CHAR ? ChatColor.RESET + tran : tran);
            }
            itemStack.setItemMeta(meta);
        }
    }

    @Override
    public void filterItemList(Object items) {
        try {
            ((List<ItemStack>) items).forEach(this::filterItem);
        } catch (Throwable e) {
            try {
                ((NonNullList) items).forEach(this::filterItem);
            } catch (Throwable e2) {
                Arrays.asList((ItemStack[]) items).forEach(this::filterItem);
            }
        }
    }

    @Override
    public boolean isAvailable() {
        return !SimpleReflection.getFields(PacketPlayOutChat.class).isEmpty() && SimpleReflection.getFields(PacketPlayOutChat.class).containsKey("a");
    }

}
