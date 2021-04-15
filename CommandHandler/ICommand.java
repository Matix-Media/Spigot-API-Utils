package net.matixmedia.utils.command;

import org.bukkit.command.CommandSender;

public interface ICommand {

    void execute(CommandSender sender, String[] args, String prefix);

    String help();

    String arguments();
}
