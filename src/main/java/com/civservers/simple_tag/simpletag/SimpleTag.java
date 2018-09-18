package com.civservers.simple_tag.simpletag;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;



public final class SimpleTag extends JavaPlugin {

	Object playerList;
	public String pluginName = "BlankPlugin";
	
	@Override
    public void onEnable() {
		
		this.getCommand("mycmd").setExecutor(new pluginCommandExecutor(this));
		this.getCommand("mycmd2").setExecutor(new pluginCommandExecutor(this));
		this.getCommand("mycmd3").setExecutor(new pluginCommandExecutor(this));

		BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                // Do something
            	for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            		player.sendMessage("Count");
            	}
            }
        }, 0L, 20L);
		
		
    	for (Player player : Bukkit.getServer().getOnlinePlayers()) {
    		// cycle through online players and do something.

    	    /*pl is an object . put (pname , return of function run on player object )*/
			/*playerList.put(player.getName(), playerData(player));*/
    	}
    	    	
    	Bukkit.getConsoleSender().sendMessage("["+pluginName+"] Loaded");
    	

    }
    
    @Override
    public void onDisable() {
    	Bukkit.getConsoleSender().sendMessage("["+pluginName+"] Unloaded");
    }

}

