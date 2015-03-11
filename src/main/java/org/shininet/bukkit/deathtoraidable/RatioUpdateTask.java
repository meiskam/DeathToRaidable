package org.shininet.bukkit.deathtoraidable;

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
        		plugin.setFactionRatioRemoved(faction, plugin.getFactionRatioRemoved(faction) - 1);
        		plugin.setFactionTimeNext(faction, plugin.getFactionTimeNext(faction) + DeathToRaidable.timeout);
        	}
        }
	}

}
