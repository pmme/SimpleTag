package com.civservers.simple_tag.simpletag;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;



public final class SimpleTag extends JavaPlugin {

	public String pluginName = "SimpleTag";
	
	@Override
    public void onEnable() {
		
		this.getCommand("simpletag").setExecutor(new pluginCommandExecutor(this));

    }
    
    @Override
    public void onDisable() {
    	
    }

}

