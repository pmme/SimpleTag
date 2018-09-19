package com.civservers.simple_tag.simpletag;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class pluginCommandExecutor implements CommandExecutor {
	private final SimpleTag plugin;
	
	private String[] helpMsg = {
			ChatColor.RED + "" + ChatColor.BOLD + "--- SimpleTag Help ---" + ChatColor.RESET,
			ChatColor.GREEN + "" + ChatColor.BOLD + "Please try one of these player comamnds:",
			ChatColor.RESET + "" + ChatColor.GREEN + " simpletag start | join <player> | leave | leaveall | stop | kick <player>",
			ChatColor.YELLOW + "" + ChatColor.BOLD + "or one of these admin commands:",
			ChatColor.RESET + "" + ChatColor.YELLOW + " /simpletag endall | reload"
	};
	
	private String msg_noConsole = ChatColor.RED + "Sorry, you must be a player to run this command.";
	private String msg_alreadyPlaying = ChatColor.RED + "You are already playing a game of tag!";
	private String msg_leave = ChatColor.RED + "You have left the game!";
	private String msg_stop = ChatColor.RED + "The game has stopped!";
	private String msg_noGame = ChatColor.RED + "Cannot find the game you requested.";
	private String msg_notOnline = ChatColor.RED + "That player is not online!";
	private String msg_joined = ChatColor.GREEN + "You have joined the game!";
	private String msg_started = ChatColor.GREEN + "Your tag game has started! Invite players to join! /stag join <yourname>";
	
	
	public pluginCommandExecutor(SimpleTag plugin) {
		this.plugin = plugin;
	}
	
	
	
	@Override	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
    	if (cmd.getName().equalsIgnoreCase("simpletag")) {
    		if (args.length < 1) {
    			sender.sendMessage(helpMsg);
    			return false;
    		} else if (args[0].equalsIgnoreCase("start")){
    			if (sender instanceof Player) {
    				Player player = (Player) sender;
    				String uuid = player.getUniqueId().toString();
    				if (plugin.config.contains("games." + uuid) || plugin.isPlaying(uuid)) {
    					// player is already playing a game of tag.  Cannot create a new one.
    					sender.sendMessage(msg_alreadyPlaying);
    					return false;
    				} else {
    					List<String> playerList = new ArrayList<String>();
    					playerList.add(uuid);
    					// create game and player list
    					plugin.config.set("games." + uuid + ".players", playerList);
    					// set initial "it"
    					plugin.config.set("games." + uuid + ".it", uuid);
    					plugin.saveConfig();
    					sender.sendMessage(msg_started);
    				}
    				
    			} else {
    				sender.sendMessage(msg_noConsole);
    				return false;
    			}
    			
    			
    			
    			
    			
    		} else if (args[0].equalsIgnoreCase("stop")){
    			if (sender instanceof Player) {
    				Player player = (Player) sender;
    				String uuid = player.getUniqueId().toString();
	    			if (plugin.config.contains("games." + uuid)) {
	    				plugin.sendGamePlayers(uuid, new String[] {msg_leave,msg_stop});
	    				
	    					plugin.config.set("games." + uuid, null);
		    				plugin.saveConfig();
	    				
	    				
	    				
	    			}
    			}
    			
    			
    			
    			
    			
    		} else if (args[0].equalsIgnoreCase("join")){
    			String uuid = plugin.senderUUID(sender);
    			if(plugin.isPlaying(uuid)) {
    				sender.sendMessage(msg_alreadyPlaying);
    			} else {
    				String juuid = plugin.getOnlineUUID(args[1]);
    				if (juuid.equals("not found")) {
    					sender.sendMessage(msg_notOnline);
    				} else {
    					if (plugin.config.contains("games." + juuid)) {
    						String gameUuid = plugin.findGame(juuid);
    						plugin.debug(gameUuid);
    						Player joining = (Player) sender;
    						List<String> playerList = plugin.config.getStringList("games." + gameUuid + ".players");
    						playerList.add(joining.getUniqueId().toString());
    						plugin.config.set("games." + gameUuid + ".players", playerList);
    						plugin.saveConfig();
    						sender.sendMessage(msg_joined);
    					} else {
    						sender.sendMessage(msg_noGame);
    					}
    				}
    				
    				
    				
  
    			}
    			
    			
    			
    			
    			
    			
    			
    			
    			
    		} else if (args[0].equalsIgnoreCase("leave")){
    			Player player = (Player) sender;
				String uuid = player.getUniqueId().toString();
				if (plugin.isPlaying(uuid)) {
					String gameUuid = plugin.findGame(uuid);
					if (gameUuid != "") {
						List<String> playerList = plugin.config.getStringList("games." + gameUuid + ".players");
						playerList.remove(uuid);    					
						plugin.saveConfig();
						sender.sendMessage(msg_leave);
						
    					if (playerList.isEmpty()) {
    						plugin.config.set("games." + uuid, null);
		    				plugin.saveConfig();
		    				sender.sendMessage(msg_stop);
    					} else {
    						String[] sMsg = {ChatColor.BOLD + player.getDisplayName().toString() + " has left the tag game!"};
    						plugin.sendGamePlayers(gameUuid, sMsg);
    						plugin.config.set("games." + gameUuid + ".players", playerList);
    						if (plugin.config.getString("games." + gameUuid + ".it").equals(uuid)) {
        						Object[] stillPlaying = playerList.toArray();
        						Player newIt = Bukkit.getPlayer(UUID.fromString(stillPlaying[0].toString()));
        						plugin.config.set("games." + gameUuid + ".it", stillPlaying[0].toString());
        						plugin.saveConfig();
        						String[] rMsg = {newIt.getCustomName() + " is now it!"};
        						plugin.sendGamePlayers(gameUuid,rMsg );
        						plugin.soundGamePlayers(gameUuid);
        					}	
    					}
    					
					} else {
						sender.sendMessage(msg_noGame);
					}
				}
				
				
				
				
				
				
				
				
    		} else if (args[0].equalsIgnoreCase("reload")){
//    			sender.sendMessage(ChatColor.RED + plugin.pluginName + " Reload command not activated!");
    			boolean rStatus = plugin.reload();
    			if (rStatus) {
    				sender.sendMessage(ChatColor.GREEN + plugin.pluginName + " Reloaded!");
    			} else {
    				sender.sendMessage(ChatColor.RED + plugin.pluginName + " Reload Failed!");
    			}
    			return true;
    		} else {
    			sender.sendMessage(helpMsg);
        		return false;
    		}   		
    		
    	} else {
    		return false;
    	}
		return false;
	}
	
}
