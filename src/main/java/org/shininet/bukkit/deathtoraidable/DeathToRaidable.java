package org.shininet.bukkit.deathtoraidable;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathToRaidable extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

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

    @EventHandler(priority=EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        Player player = event.getEntity();
        if (!(player.hasPermission("deathtoraidable.enable"))) {
        	return;
        }
    	// TODO change DTR if in faction
    }
}
