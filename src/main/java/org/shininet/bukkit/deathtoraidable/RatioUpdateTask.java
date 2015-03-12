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
		plugin.debug("updatetask");
		for (Faction faction : FactionColl.get().getAll()) {
			if ((plugin.getFactionRatioRemoved(faction) > 0) && (faction.getMPlayersWhereOnline(true).size() > 0)) {
				plugin.setFactionRatioRemoved(faction, plugin.getFactionRatioRemoved(faction) - DeathToRaidable.timerRemove);
			}
		}
	}
}
