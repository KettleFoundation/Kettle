package org.bukkit.command.defaults;

import com.google.common.base.Charsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraftforge.common.ForgeVersion;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.StringUtil;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class VersionCommand extends BukkitCommand {
    private static final String BRANCH = "KettleJava";
    private final ReentrantLock versionLock = new ReentrantLock();
    private final Set<CommandSender> versionWaiters = new HashSet<>();
    private boolean hasVersion = false;
    private String versionMessage = null;
    private boolean versionTaskStarted = false;
    private long lastCheck = 0;

    public VersionCommand(String name) {
        super(name);

        this.description = "Gets the version of this server including any plugins in use";
        this.usageMessage = "/version [plugin name]";
        this.setPermission("bukkit.command.version");
        this.setAliases(Arrays.asList("ver", "about"));
    }

    // Taken from Paper / Modified by GMatrixGames
    private static int getDistance(String repo, String verInfo) {
        try {
            int currentVer = Integer.decode(verInfo);
            return getFromJenkins(currentVer);
        } catch (NumberFormatException ex) {
            verInfo = verInfo.replace("\"", "");
            return getFromRepo("KettleFoundation/Kettle", verInfo);
        }
    }

    private static int getFromJenkins(int currentVer) {
        try {
            try (BufferedReader reader = Resources.asCharSource(
                    new URL("https://ci.openprocesses.ml/job/Kettle/lastSuccessfulBuild/buildNumber"),
                    Charsets.UTF_8
            ).openBufferedStream()) {
                int newVer = Integer.decode(reader.readLine());
                return newVer - currentVer;
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                return -2;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static int getFromRepo(String repo, String hash) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://api.github.com/repos/" + repo + "/compare/" + BRANCH + "..." + hash).openConnection();
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) return -2; // Unknown commit
            try (
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charsets.UTF_8))
            ) {
                JSONObject obj = (JSONObject) new JSONParser().parse(reader);
                String status = (String) obj.get("status");
                switch (status) {
                    case "identical":
                        return 0;
                    case "behind":
                        return ((Number) obj.get("behind_by")).intValue();
                    default:
                        return -1;
                }
            } catch (ParseException | NumberFormatException e) {
                e.printStackTrace();
                return -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        if (args.length == 0) {
            sender.sendMessage("This server is running " + Bukkit.getName() + " version " + Bukkit.getVersion() + " with Forge version " + ForgeVersion.getVersion() + " (Implementing API version " + Bukkit.getBukkitVersion() + ")");
            sendVersion(sender);
        } else {
            StringBuilder name = new StringBuilder();

            for (String arg : args) {
                if (name.length() > 0) {
                    name.append(' ');
                }

                name.append(arg);
            }

            String pluginName = name.toString();
            Plugin exactPlugin = Bukkit.getPluginManager().getPlugin(pluginName);
            if (exactPlugin != null) {
                describeToSender(exactPlugin, sender);
                return true;
            }

            boolean found = false;
            pluginName = pluginName.toLowerCase(java.util.Locale.ENGLISH);
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                if (plugin.getName().toLowerCase(java.util.Locale.ENGLISH).contains(pluginName)) {
                    describeToSender(plugin, sender);
                    found = true;
                }
            }

            if (!found) {
                sender.sendMessage("This server is not running any plugin by that name.");
                sender.sendMessage("Use /plugins to get a list of plugins.");
            }
        }
        return true;
    }

    private void describeToSender(Plugin plugin, CommandSender sender) {
        PluginDescriptionFile desc = plugin.getDescription();
        sender.sendMessage(ChatColor.GREEN + desc.getName() + ChatColor.WHITE + " version " + ChatColor.GREEN + desc.getVersion());

        if (desc.getDescription() != null) {
            sender.sendMessage(desc.getDescription());
        }

        if (desc.getWebsite() != null) {
            sender.sendMessage("Website: " + ChatColor.GREEN + desc.getWebsite());
        }

        if (!desc.getAuthors().isEmpty()) {
            if (desc.getAuthors().size() == 1) {
                sender.sendMessage("Author: " + getAuthors(desc));
            } else {
                sender.sendMessage("Authors: " + getAuthors(desc));
            }
        }
    }

    private String getAuthors(final PluginDescriptionFile desc) {
        StringBuilder result = new StringBuilder();
        List<String> authors = desc.getAuthors();

        for (int i = 0; i < authors.size(); i++) {
            if (result.length() > 0) {
                result.append(ChatColor.WHITE);

                if (i < authors.size() - 1) {
                    result.append(", ");
                } else {
                    result.append(" and ");
                }
            }

            result.append(ChatColor.GREEN);
            result.append(authors.get(i));
        }

        return result.toString();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            String toComplete = args[0].toLowerCase(java.util.Locale.ENGLISH);
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                if (StringUtil.startsWithIgnoreCase(plugin.getName(), toComplete)) {
                    completions.add(plugin.getName());
                }
            }
            return completions;
        }
        return ImmutableList.of();
    }

    private void sendVersion(CommandSender sender) {
        if (hasVersion) {
            if (System.currentTimeMillis() - lastCheck > 21600000) {
                lastCheck = System.currentTimeMillis();
                hasVersion = false;
            } else {
                sender.sendMessage(versionMessage);
                return;
            }
        }
        versionLock.lock();
        try {
            if (hasVersion) {
                sender.sendMessage(versionMessage);
                return;
            }
            versionWaiters.add(sender);
            sender.sendMessage("Checking version, please wait...");
            if (!versionTaskStarted) {
                versionTaskStarted = true;
                new Thread(this::obtainVersion).start();
            }
        } finally {
            versionLock.unlock();
        }
    }

    private void obtainVersion() {
        String version = Bukkit.getVersion();
        if (version == null) version = "Custom";
        if (version.startsWith("git-Kettle-")) {
            String[] parts = version.substring("git-Kettle-".length()).split("[-\\s]");
            int distance = getDistance(null, parts[0]);
            switch (distance) {
                case -1:
                    setVersionMessage("Error obtaining version information");
                    break;
                case 0:
                    setVersionMessage("You are running the latest version");
                    break;
                case -2:
                    setVersionMessage("Unknown version");
                    break;
                default:
                    setVersionMessage("You are " + distance + " version(s) behind");
            }
        } else if (version.startsWith("git-Bukkit-")) {
            version = version.substring("git-Bukkit-".length());
            int cbVersions = getDistance("craftbukkit", version.substring(0, version.indexOf(' ')));
            if (cbVersions == -1) {
                setVersionMessage("Error obtaining version information");
            } else {
                if (cbVersions == 0) {
                    setVersionMessage("You are running the latest version");
                } else {
                    setVersionMessage("You are " + cbVersions + " version(s) behind");
                }
            }
        } else {
            setVersionMessage("Unknown version, custom build?");
        }
    }

    private void setVersionMessage(String msg) {
        lastCheck = System.currentTimeMillis();
        versionMessage = msg;
        versionLock.lock();
        try {
            hasVersion = true;
            versionTaskStarted = false;
            for (CommandSender sender : versionWaiters) {
                sender.sendMessage(versionMessage);
            }
            versionWaiters.clear();
        } finally {
            versionLock.unlock();
        }
    }

    // Kettle End
}