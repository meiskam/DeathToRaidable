package org.shininet.bukkit.deathtoraidable;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;

public class RatioUpdateTask extends BukkitRunnable {
	DeathToRaidable plugin;

	public RatioUpdateTask(DeathToRaidable plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
        for (Faction faction : FactionColl.get().getAll()) {
        	while ((plugin.getFactionRatioRemoved(faction) > 0) && plugin.getFactionTimeNext(faction) < plugin.getTime()) {
                if (plugin.getFactionRatioTimesTwo(faction) == 0) {
                	//TODO actually make them non-raidable
                	Bukkit.broadcastMessage(faction.getName() + " DTR has replenished and are no longer raidable.");
                }

        		plugin.setFactionRatioRemoved(faction, plugin.getFactionRatioRemoved(faction) - 1);
        		plugin.setFactionTimeNext(faction, plugin.getFactionTimeNext(faction) + DeathToRaidable.timeout);
        	}
        }
	}

}
