package com.civservers.simple_tag.simpletag;



import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;




public final class SimpleTag extends JavaPlugin implements Listener {

	public String pluginName = "SimpleTag";
	public FileConfiguration config = getConfig();

	public Map<String, Object> msgs = config.getConfigurationSection("messages").getValues(true);
	
	
	
	@Override
    public void onEnable() {
		
		config.options().copyDefaults(true);
	    saveConfig();
	    
		this.getCommand("simpletag").setExecutor(new pluginCommandExecutor(this));
		
		Bukkit.getPluginManager().registerEvents(this, this);
		reload();
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
    		String v_uuid = victim.getUniqueId().toString();
    		String c_uuid = culprit.getUniqueId().toString();
    		
    		if (isPlaying(v_uuid) && isPlaying(c_uuid)) {
    			String gameuuid_v = findGame(v_uuid);
        		String gameuuid_c = findGame(c_uuid);
    			if (gameuuid_v.equals(gameuuid_c)) {
    				if (config.getString("games."+ gameuuid_c +".it").equals(c_uuid)) {
    					soundGamePlayers(gameuuid_c);
    					sendGamePlayers(gameuuid_c, ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + victim.getDisplayName().toString() + msgs.get("tagged").toString());
    					config.set("games." + gameuuid_c + ".it", v_uuid);
    					saveConfig();
    				}

    	    		//Cancel damage
    	    		if ((boolean) config.get("cancelPVPDamage")) {
    	    			event.setCancelled(true);
    	    		}
    			}
    		}
    	}
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
    	Player quitter = event.getPlayer();
    	String q_uuid = quitter.getUniqueId().toString();
    	if (isPlaying(q_uuid)) {
    		leaveGame(q_uuid);
    	}
    }
    
    public void leaveGame(String uuid) {
		Player player = Bukkit.getPlayer(UUID.fromString(uuid));
    	String gameUuid = findGame(uuid);    	
		if (gameUuid != "") {
			List<String> playerList = config.getStringList("games." + gameUuid + ".players");
			playerList.remove(uuid);    					
			saveConfig();
			sendPlayer(player,ChatColor.RED + msgs.get("leave").toString());
			
			
			if (playerList.isEmpty()) {
				config.set("games." + uuid, null);
				saveConfig();
				sendPlayer(player,ChatColor.RED + msgs.get("stop").toString());
			} else {
				String sMsg = ChatColor.BOLD.toString() + ChatColor.RED.toString() + player.getDisplayName().toString() + msgs.get("has_left").toString();
				sendGamePlayers(gameUuid, sMsg);
				config.set("games." + gameUuid + ".players", playerList);
				if (config.getString("games." + gameUuid + ".it").equals(uuid)) {
					Object[] stillPlaying = playerList.toArray();
					Player newIt = Bukkit.getPlayer(UUID.fromString(stillPlaying[0].toString()));
					config.set("games." + gameUuid + ".it", stillPlaying[0].toString());
					saveConfig();
					
					sendGamePlayers(gameUuid,newIt.getCustomName() + msgs.get("is_it") );
					soundGamePlayers(gameUuid);
				}	
			}
		} else {
			sendPlayer(player,ChatColor.RED + msgs.get("no_game").toString());
			
		}
    }
    
    public boolean isPlaying(String uuid) {
    	boolean out = false;
    	if (config.contains("games")) {
	    	for (Object key : config.getConfigurationSection("games").getKeys(false).toArray()) {
	    		List<String> playerList = config.getStringList("games." + key + ".players");

    			if (playerList.contains(uuid)) {
    				out = true;
    			}
    		
	    	}
    	}
		return out;
    }
    public String findGame(String uuid) {
    	debug(uuid);
    	String out = "";
    	if (config.contains("games")) {
	    	for (Object key : config.getConfigurationSection("games").getKeys(false).toArray()) {
	    		debug("Key: "+key);
	    		
	    		List<String> playerList = config.getStringList("games." + key + ".players");

    			if (playerList.contains(uuid) || uuid.equals(key.toString())) {
    				out = (String) key;
    			}
	    	}
    	}
		return out;
    }
    
    public void soundGamePlayers(String gameuuid) {
    	List<String> playerList = config.getStringList("games." + gameuuid + ".players");
    	playerList.forEach(new Consumer<String>() {
    		public void accept(String playeruuid ) {
    			Player cPlayer = Bukkit.getPlayer(UUID.fromString(playeruuid));
    			cPlayer.playSound(cPlayer.getLocation(), Sound.valueOf(config.getString("tagSound")), 3f, 1f);
    		}
    	});
    }    
    public void sendGamePlayers(String gameuuid, final String msg) {
    	debug(msg.toString());
    	List<String> playerList = config.getStringList("games." + gameuuid + ".players");
    	playerList.forEach(new Consumer<String>() {
    		public void accept(String playeruuid ) {
    			sendPlayer(Bukkit.getPlayer(UUID.fromString(playeruuid)),msg);
    		}
    	});
    }
    public void sendPlayer(Player p, String msg) {
    	p.sendMessage(ChatColor.YELLOW + msgs.get("prefix").toString() + msg);
    }
    public String senderUUID(CommandSender sender) {
    	Player player = (Player) sender;
		String uuid = player.getUniqueId().toString();
		return uuid;
    }
    public String getOnlineUUID(String username) {
    	String out = "not found";
    	Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
		for (Player opl : onlinePlayers) {
			if (opl.getName().equalsIgnoreCase(username)) {
				out = opl.getUniqueId().toString();
			}
		}
    	
    	return out;
    	
    }
    public void debug(String dString) {
    	if (config.getBoolean("debug")) {
    		Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "["+pluginName+" DEBUG]" + " " + dString);
    	}
    }
    public boolean reload() {
		reloadConfig();
		config = getConfig();
		msgs = config.getConfigurationSection("messages").getValues(true);
		return true;     
    }
}

