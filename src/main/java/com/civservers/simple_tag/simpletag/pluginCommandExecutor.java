package com.civservers.simple_tag.simpletag;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;



public class pluginCommandExecutor implements CommandExecutor {
	private final SimpleTag plugin;
	
	private String[] helpMsg = {
			ChatColor.RED + "" + ChatColor.BOLD + "--- SimpleTag Help ---" + ChatColor.RESET,
			ChatColor.GREEN + "" + ChatColor.BOLD + "Please try one of these player comamnds:",
			ChatColor.RESET + "" + ChatColor.GREEN + " simpletag start | join <player> | leave | stop | kick <player>",
			ChatColor.YELLOW + "" + ChatColor.BOLD + "or one of these admin commands:",
			ChatColor.RESET + "" + ChatColor.YELLOW + " /simpletag reload"
	};
	


	
	
	public pluginCommandExecutor(SimpleTag plugin) {
		this.plugin = plugin;
	}
	
	@Override	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
    	if (cmd.getName().equalsIgnoreCase("simpletag")) {
    		if (sender instanceof Player) {
    			Player player = (Player) sender;
    			String uuid = player.getUniqueId().toString();
	    		if (args.length < 1) {
	    			player.sendMessage(helpMsg);
	    			return false;
	    		} else if (args[0].equalsIgnoreCase("test")){
	    			plugin.debug(player.getDisplayName().toString());
	    		} else if (args[0].equalsIgnoreCase("start")){
	    			if (sender.hasPermission("simpletag.create")) {
	    				if (plugin.config.contains("games." + uuid) || plugin.isPlaying(uuid)) {
	    					// player is already playing a game of tag.  Cannot create a new one.
	    					plugin.sendPlayer(player, ChatColor.RED + plugin.msgs.get("already_playing").toString());
	    					return false;
	    				} else {
	    					// create game and player list
	    					List<String> playerList = new ArrayList<String>();
	    					playerList.add(uuid);
	    					plugin.config.set("games." + uuid + ".players", playerList);
	    					// set initial "it"
	    					plugin.config.set("games." + uuid + ".it", uuid);
	    					plugin.saveConfig();
	    					plugin.sendPlayer(player, ChatColor.GREEN + plugin.msgs.get("started").toString());
	    				}
	    			} else {
	    				plugin.sendPlayer(player, ChatColor.RED + plugin.msgs.get("no_perm").toString());
	    			}
	    		} else if (args[0].equalsIgnoreCase("stop")){
	    			if (sender.hasPermission("simpletag.create")) {
		    			if (plugin.config.contains("games." + uuid)) {
		    				plugin.sendGamePlayers(uuid, ChatColor.RED + plugin.msgs.get("leave").toString());
		    				plugin.sendGamePlayers(uuid, ChatColor.RED + plugin.msgs.get("stop").toString());
	    					plugin.config.set("games." + uuid, null);
		    				plugin.saveConfig();
		    			}
	    			} else {
	    				plugin.sendPlayer(player, ChatColor.RED + plugin.msgs.get("no_perm").toString());
	    			}
	    		} else if (args[0].equalsIgnoreCase("kick")){
	    			plugin.debug("Starting kick script:");
	    			if (sender.hasPermission("simpletag.create")) {
	    				String t_uuid = plugin.getOnlineUUID(args[1]);
	    				plugin.debug(t_uuid);
						if (plugin.isPlaying(t_uuid)) {
							plugin.leaveGame(t_uuid);
							plugin.sendPlayer(Bukkit.getPlayer(UUID.fromString(t_uuid)),ChatColor.RED + plugin.msgs.get("kicked").toString());
						}
	    			} else {
	    				plugin.sendPlayer(player, ChatColor.RED + plugin.msgs.get("no_perm").toString());
	    			}
	    		} else if (args[0].equalsIgnoreCase("join")){
	    			if (sender.hasPermission("simpletag.play")) {
		    			if(plugin.isPlaying(uuid)) {
		    				plugin.sendPlayer(player, ChatColor.RED + plugin.msgs.get("already_playing").toString());
		    			} else {
		    				String juuid = plugin.getOnlineUUID(args[1]);
		    				if (juuid.equals("not found")) {
		    					plugin.sendPlayer(player, ChatColor.RED + plugin.msgs.get("not_online").toString());
		    				} else {
		    					if (plugin.config.contains("games." + juuid)) {
		    						String gameUuid = plugin.findGame(juuid);
		    						plugin.debug(gameUuid);
		    						Player joining = player;
		    						List<String> playerList = plugin.config.getStringList("games." + gameUuid + ".players");
		    						playerList.add(joining.getUniqueId().toString());
		    						plugin.config.set("games." + gameUuid + ".players", playerList);
		    						plugin.saveConfig();
		    						plugin.sendPlayer(player, ChatColor.GREEN + plugin.msgs.get("joined").toString());
		    					} else {
		    						plugin.sendPlayer(player, ChatColor.RED + plugin.msgs.get("no_game").toString());
		    					}
		    				}
		    			}
	    			} else {
	    				plugin.sendPlayer(player, ChatColor.RED + plugin.msgs.get("no_perm").toString());
	    			}
	    		} else if (args[0].equalsIgnoreCase("leave")){
	    			if (sender.hasPermission("simpletag.play")) {
						
						if (plugin.isPlaying(uuid)) {
							plugin.leaveGame(uuid);
						}
						
	    			} else {
	    				plugin.sendPlayer(player, ChatColor.RED + plugin.msgs.get("no_perm").toString());
	    				return false;
	    			}
	    		} else if (args[0].equalsIgnoreCase("reload")){
	    			if (sender.hasPermission("simpletag.admin")) {
		    			boolean rStatus = plugin.reload();
		    			if (rStatus) {
		    				sender.sendMessage(ChatColor.GREEN + plugin.pluginName + " Reloaded!");
		    			} else {
		    				sender.sendMessage(ChatColor.RED + plugin.pluginName + " Reload Failed!");
		    			}
		    			return true;
	    			} else {
	    				plugin.sendPlayer(player, ChatColor.RED + plugin.msgs.get("no_perm").toString());
	    				return false;
	    			}
	    		} else {
	    			sender.sendMessage(helpMsg);
	        		return false;
	    		}
    		} else {
    			sender.sendMessage(ChatColor.RED + plugin.msgs.get("no_console").toString());
    			return false;
    		}
    	} else {
    		return false;
    	}
		return false;
	}
}
