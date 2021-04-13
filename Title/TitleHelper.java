/*
 * MIT License
 *
 * Copyright (c) 2021 Matix Media, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package net.matixmedia.utils;

import net.matixmedia.blockclutch.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class TitleHelper {
    private static final Map<Player, BukkitTask> PENDING_MESSAGES = new HashMap<>();

    /**
     * Send a title to player
     * @param player Player to send the title to
     * @param text The text displayed in the title
     * @param fadeInTime The time the title takes to fade in
     * @param showTime The time the title is displayed
     * @param fadeOutTime The time the title takes to fade out
     * @param color The color of the title
     */
    public void sendTitle(Player player, String text, int fadeInTime, int showTime, int fadeOutTime, ChatColor color)
    {
        try
        {
            Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\": \"" + text + "\",color:" + color.name().toLowerCase() + "}");

            Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
            Object packet = titleConstructor.newInstance(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null), chatTitle, fadeInTime, showTime, fadeOutTime);

            sendPacket(player, packet);
        }

        catch (Exception ex)
        {
            //Do something
        }
    }

    /**
     * Sends a title to the player.
     *
     * @param player the player to send the title to.
     * @param title the title to send.
     * @param subtitle the subtitle to send.
     * @param fadein the fade in delay.
     * @param stay the time on the screen
     * @param fadeout the fade out delay.
     */
    public void sendTitle(Player player, String title, String subtitle, int fadein, int stay, int fadeout) {
        title = ChatColor.translateAlternateColorCodes('&', title);
        subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);

        Class<?> chatSerializer = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0];
        Class<?> chatComponent = getNMSClass("IChatBaseComponent");
        Class<?> packetTitle = getNMSClass("PacketPlayOutTitle");
        try {
            Object enumTitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
            Constructor<?> constructorTitle = packetTitle.getDeclaredConstructor(packetTitle.getDeclaredClasses()[0], chatComponent, int.class, int.class, int.class);
            Object chatTitle = chatSerializer.getMethod("a", String.class).invoke(chatSerializer, "{\"text\": \"" + title + "\"}");
            Object packet = constructorTitle.newInstance(enumTitle, chatTitle, fadein, stay, fadeout);
            sendPacket(player, packet);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            Object enumSubtitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
            Constructor<?> constructorSubtitle = packetTitle.getDeclaredConstructor(packetTitle.getDeclaredClasses()[0], chatComponent, int.class, int.class, int.class);
            Object chatSubtitle = chatSerializer.getMethod("a", String.class).invoke(chatSerializer, "{\"text\": \"" + subtitle + "\"}");
            Object packet = constructorSubtitle.newInstance(enumSubtitle, chatSubtitle, fadein, stay, fadeout);
            sendPacket(player, packet);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Sends a message to the player's action bar.
     * <p/>
     * The message will appear above the player's hot bar for 2 seconds and then fade away over 1 second.
     *
     * @param player the player to send the message to.
     * @param message the message to send.
     */
    public void sendActionBar(Player player, String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);

        Class<?> chatSerializer = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0];
        Class<?> chatComponent = getNMSClass("IChatBaseComponent");
        Class<?> packetActionbar = getNMSClass("PacketPlayOutChat");
        try {
            Constructor<?> ConstructorActionbar = packetActionbar.getDeclaredConstructor(chatComponent, byte.class);
            Object actionbar = chatSerializer.getMethod("a", String.class).invoke(chatSerializer, "{\"text\": \"" + message + "\"}");
            Object packet = ConstructorActionbar.newInstance(actionbar, (byte) 2);
            sendPacket(player, packet);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Sends a message to the player's action bar that lasts for an extended duration.
     * <p/>
     * The message will appear above the player's hot bar for the specified duration and fade away during the last
     * second of the duration.
     * <p/>
     * Only one long duration message can be sent at a time per player. If a new message is sent via this message
     * any previous messages still being displayed will be replaced.
     *
     * @param player the player to send the message to.
     * @param message the message to send.
     * @param duration the duration the message should be visible for in seconds.
     * @param plugin the plugin sending the message.
     */
    public void sendActionBar(final Player player, final String message,
                                            final int duration, Plugin plugin) {
        cancelPendingMessages(player);
        final BukkitTask messageTask = new BukkitRunnable() {
            private int count = 0;
            @Override
            public void run() {
                if (count >= (duration - 3)) {
                    this.cancel();
                }
                new TitleHelper().sendActionBar(player, message);
                count++;
            }
        }.runTaskTimer(plugin, 0L, 20L);
        PENDING_MESSAGES.put(player, messageTask);
    }



    private static void cancelPendingMessages(Player bukkitPlayer) {
        if (PENDING_MESSAGES.containsKey(bukkitPlayer)) {
            PENDING_MESSAGES.get(bukkitPlayer).cancel();
        }
    }

    private void sendPacket(Player player, Object packet)
    {
        try
        {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        }
        catch(Exception ex)
        {
            //Do something
        }
    }

    /**
     * Get NMS class using reflection
     * @param name Name of the class
     * @return Class
     */
    private Class<?> getNMSClass(String name)
    {
        try
        {
            return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        }
        catch(ClassNotFoundException ex)
        {
            Main.logger.info("Could not get NMS class!");
            //Do something
        }
        return null;
    }
}
