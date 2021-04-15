package net.matixmedia.utils.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Map;

public class CommandHandler implements CommandExecutor {

    private String name;
    private String label;
    private String prefix;
    private ICommand baseCommand;
    private Map<String, ICommand> commands;
    private Map<String, String> permissions;

    public CommandHandler(String name, String label, String prefix) {
        this.name = name;
        this.label = label;
        this.prefix = prefix;
    }

    public void addCommand(String label, ICommand command, String permission) {
        commands.put(label, command);
        permissions.put(label, permission);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (args.length == 0 && baseCommand == null) {
            sender.sendMessage(prefix + ChatColor.GRAY + " ====== " + ChatColor.GREEN + name + ChatColor.GRAY + " ======");
            for (Map.Entry<String, ICommand> entry : commands.entrySet()) {
                if (!sender.hasPermission(permissions.get(entry.getKey())))
                    continue;
                if (entry.getValue().arguments() == null) {
                    sender.sendMessage(prefix + " " + ChatColor.YELLOW + "/" + label + " " + ChatColor.BOLD + entry.getKey() +
                            ChatColor.DARK_GRAY + " - " +
                            ChatColor.GRAY + entry.getValue().help());
                } else {
                    sender.sendMessage(prefix + " " + ChatColor.YELLOW + "/" + label + " " + ChatColor.BOLD + entry.getKey() +
                            ChatColor.YELLOW + " " + entry.getValue().arguments() + ChatColor.DARK_GRAY + " - " +
                            ChatColor.GRAY + entry.getValue().help());
                }
            }
            sender.sendMessage(" ");
            return true;
        } else if (args.length == 0) {
            baseCommand.execute(sender, new String[0], prefix);
            return true;
        } else {
            if (!commands.containsKey(args[0])) {
                sender.sendMessage(prefix + ChatColor.RED + " Unknown command.");
                return true;
            }
            String[] subCommandArgs = Arrays.copyOfRange(args, label.split(" ").length, args.length);
            commands.get(args[0]).execute(sender, subCommandArgs, prefix);
            return true;
        }
    }
}
