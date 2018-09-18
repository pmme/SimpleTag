package com.civservers.simple_tag.simpletag;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;



public final class SimpleTag extends JavaPlugin implements Listener {

	public String pluginName = "SimpleTag";
	public FileConfiguration config = getConfig();
	
	
	@Override
    public void onEnable() {
		
		config.addDefault("cancelPVPDamage", true);
		
		config.options().copyDefaults(true);
	    saveConfig();
	    
	    
		this.getCommand("simpletag").setExecutor(new pluginCommandExecutor(this));
		
		Bukkit.getPluginManager().registerEvents(this, this);

    }
    
    @Override
    public void onDisable() {
		if (config.contains("games")) {
			config.set("games", null);
			saveConfig();
		}
    }
    
    @EventHandler
    public void onHit(EntityDamageByEntityEvent event){    	
    	// Check if damage is pvp
    	if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
    		Player victim = (Player) event.getEntity();
    		Player culprit = (Player) event.getDamager();
    		/*
    		 * if culprit is it
    		 * 		if victim is playing
    		 * 			it = victim
    		 * 			lastit = cuplrit
    		 * 			restart tagback timer
    		 * 
    		 * 
    		 * 
    		 */
    		
    		
    		
    		//Cancel damage
    		if ((boolean) config.get("cancelPVPDamage")) {
    			event.setCancelled(true);
    		}
    	}
    }
    
    public boolean isPlaying(String uuid) {
    	boolean out = false;
    	if (config.contains("games")) {
	    	for (Object key : config.getConfigurationSection("games").getKeys(false).toArray()) {
	    		for (Object playing : config.getConfigurationSection("games." + key + ".players").getKeys(false).toArray()) {
	    			if (playing.equals(uuid)) {
	    				out = true;
	    			}
	    		}
	    	}
    	}
		return out;
    }

}

