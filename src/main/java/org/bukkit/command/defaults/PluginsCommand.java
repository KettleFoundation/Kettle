package org.bukkit.command.defaults;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class PluginsCommand extends BukkitCommand {
    public PluginsCommand(String name) {
        super(name);
        this.description = "Gets a list of plugins running on the server";
        this.usageMessage = "/plugins [load|unload|reload] [name]";
        this.setPermission("bukkit.command.plugins");
        this.setAliases(Arrays.asList("pl"));
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("Plugins " + getPluginList());
            return false;
        }

        switch (args[0].toLowerCase(Locale.ENGLISH))  {
//            case "load":
//                PluginManagers.loadPluginCommand(sender, currentAlias, args);
//                break;
//            case "unload":
//                PluginManagers.unloadPluginCommand(sender, currentAlias, args);
//                break;
//            case "reload":
//                PluginManagers.reloadPluginCommand(sender, currentAlias, args);
//                break;
            default:
                sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
                return false;
        }
//        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> tabs = new ArrayList<String>();
        if (args.length > 1) {
            String action = args[0].toLowerCase();
            if (action.equals("unload")) {
                for (Plugin plugin : Bukkit.getServer().getPluginManager().getPlugins()) {
                    tabs.add(plugin.getName());
                }
            }
            else if (action.equals("reload")) {
                for (Plugin plugin : Bukkit.getServer().getPluginManager().getPlugins()) {
                    tabs.add(plugin.getName());
                }
            }
        }
        return tabs;
    }

    private String getPluginList() {
        // Paper start
        TreeMap<String, ChatColor> plugins = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            plugins.put(plugin.getDescription().getName(), plugin.isEnabled() ? ChatColor.GREEN : ChatColor.RED);
        }

        StringBuilder pluginList = new StringBuilder();
        for (Map.Entry<String, ChatColor> entry : plugins.entrySet()) {
            if (pluginList.length() > 0) {
                pluginList.append(ChatColor.WHITE);
                pluginList.append(", ");
            }

            pluginList.append(entry.getValue());
            pluginList.append(entry.getKey());
        }

        return "(" + plugins.size() + "): " + pluginList.toString();
        // Paper end
    }
}
