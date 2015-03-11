package org.shininet.bukkit.deathtoraidable;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsCreate;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import com.massivecraft.massivecore.xlib.gson.JsonElement;
import com.massivecraft.massivecore.xlib.gson.JsonObject;

public class DeathToRaidable extends JavaPlugin implements Listener {

	static String keyTimeNext = "DeathToRaidable.timeNext";
	static String keyRatioMax = "DeathToRaidable.ratioMax";
	static String keyRatioRemoved = "DeathToRaidable.ratioRemoved";
	static int timeout = 1*60*60; //seconds
	static long timer = 5*60*20; //ticks
	BukkitTask ratioUpdateTask;

	static NumberFormat decimalFormat = DecimalFormat.getInstance();

    @Override
    public void onEnable() {
    	decimalFormat.setMinimumFractionDigits(0);
    	decimalFormat.setMaximumFractionDigits(1);

        for (Faction faction : FactionColl.get().getAll()) {
        	setupFaction(faction);
        }

        ratioUpdateTask = new RatioUpdateTask(this).runTaskTimer(this, 0L, timer);

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    void setupFaction(Faction faction) {
    	ensureFactionTimeNext(faction);
    	updateFactionRatioMax(faction);
    	ensureFactionRatioRemoved(faction);
    	//TODO ensure raidable is set correctly
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

        Faction faction = FactionColl.get().getByName(StringUtils.join(args, ' '));
        if (faction == null) {
        	sender.sendMessage("Faction could not be found");
        	return true;
        } else {
        	sender.sendMessage(faction.getName() + " DTR is " + getFactionDisplayRatio(faction));
        	return true;
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        Player player = event.getEntity();
        if (!(player.hasPermission("deathtoraidable.enable"))) {
        	return;
        }
        MPlayer mplayer = MPlayer.get(player.getUniqueId());
        if (mplayer == null) {
        	return;
        }
        Faction faction = mplayer.getFaction();
        if (faction == Faction.get(MConf.get().defaultPlayerFactionId)) {
        	return;
        }

        int ratioRemoved = getFactionRatioRemoved(faction);
        if (ratioRemoved == 0) {
        	setFactionTimeNext(faction, getTimePlusTimeout());
        }
        ratioRemoved += 2;
        setFactionRatioRemoved(faction, ratioRemoved);
    }

    @EventHandler
    public void onFactionsCreate(EventFactionsCreate event) {
    	setupFaction(FactionColl.get().get(event.getFactionId()));
    }

    @EventHandler
    public void onFactionsMembershipChange(EventFactionsMembershipChange event) {
    	updateFactionRatioMax(event.getNewFaction());
    }

    long getTime() {
    	return System.currentTimeMillis() / 1000L;
    }

    long getTimePlusTimeout() {
    	return getTime() + timeout;
    }

    void ensureFactionTimeNext(Faction faction) {
    	JsonObject customData = faction.getCustomData();

        JsonElement jsonTimeNext = customData.get(keyTimeNext);
        if (jsonTimeNext == null) {
            customData.addProperty(keyTimeNext, 0);
        }
    }

    long getFactionTimeNext(Faction faction) {
    	JsonObject customData = faction.getCustomData();
    	ensureFactionTimeNext(faction);
        return customData.get(keyTimeNext).getAsLong();
    }

    void setFactionTimeNext(Faction faction, long time) {
    	JsonObject customData = faction.getCustomData();

    	customData.remove(keyTimeNext);
        customData.addProperty(keyTimeNext, time);
    }

    void updateFactionRatioMax(Faction faction) {
    	setFactionRatioMax(faction, 2 + faction.getMPlayers().size());
    }

    void ensureFactionRatioMax(Faction faction) {
    	JsonObject customData = faction.getCustomData();

        JsonElement jsonRatioMax = customData.get(keyRatioMax);
        if (jsonRatioMax == null) {
            customData.addProperty(keyRatioMax, 2 + faction.getMPlayers().size());
        }
    }
    
    int getFactionRatioMax(Faction faction) {
    	JsonObject customData = faction.getCustomData();
    	ensureFactionRatioMax(faction);
        return customData.get(keyRatioMax).getAsInt();
    }

    void setFactionRatioMax(Faction faction, int amount) {
    	int pre = getFactionRatioTimesTwo(faction);
        JsonObject customData = faction.getCustomData();

    	customData.remove(keyRatioMax);
        customData.addProperty(keyRatioMax, amount);

    	checkFactionRaidableUpdate(faction, pre, getFactionRatioTimesTwo(faction));
    }

    void ensureFactionRatioRemoved(Faction faction) {
    	JsonObject customData = faction.getCustomData();

        JsonElement jsonRatioRemoved = customData.get(keyRatioRemoved);
        if (jsonRatioRemoved == null) {
            customData.addProperty(keyRatioRemoved, 0);
        }
    }
    
    int getFactionRatioRemoved(Faction faction) {
    	JsonObject customData = faction.getCustomData();
    	ensureFactionRatioRemoved(faction);
        return customData.get(keyRatioRemoved).getAsInt();
    }

    void setFactionRatioRemoved(Faction faction, int amount) {
    	int pre = getFactionRatioTimesTwo(faction);
        JsonObject customData = faction.getCustomData();

    	customData.remove(keyRatioRemoved);
        customData.addProperty(keyRatioRemoved, amount);

    	checkFactionRaidableUpdate(faction, pre, getFactionRatioTimesTwo(faction));
    }

    int getFactionRatioTimesTwo(Faction faction) {
    	return getFactionRatioMax(faction) - getFactionRatioRemoved(faction);
    }

    String getFactionDisplayRatio(Faction faction) {
    	double result = getFactionRatioMax(faction) - getFactionRatioRemoved(faction);
    	return decimalFormat.format(result/2);
    }

    void checkFactionRaidableUpdate(Faction faction, int pre, int post) {
    	if (pre > 0 && post <= 0) {
    		setFactionRaidable(faction);
    	}
    	if (pre <= 0 && post > 0) {
    		setFactionNonRaidable(faction);
    	}
    }

    void setFactionRaidable(Faction faction) {
    	//TODO actually make them raidable
    	Bukkit.broadcastMessage(faction.getName() + " DTR has dropped and are now raidable!");
    }

    void setFactionNonRaidable(Faction faction) {
    	//TODO actually make them non-raidable
    	Bukkit.broadcastMessage(faction.getName() + " DTR has replenished and are no longer raidable.");
    }
}
