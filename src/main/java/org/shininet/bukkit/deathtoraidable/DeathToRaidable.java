package org.shininet.bukkit.deathtoraidable;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathToRaidable extends JavaPlugin {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("DeathToRaidable")) {
            return false;
        }
        if (args.length == 0) {
        	sender.sendMessage("Usage: /" + label + " <faction>"); //TODO .. own faction without params?
            return true;
        }
    	sender.sendMessage("{Faction}'s DTR is {DTR}"); //TODO
    	return true;
    }
}
