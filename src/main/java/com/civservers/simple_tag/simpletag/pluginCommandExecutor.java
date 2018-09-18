package com.civservers.simple_tag.simpletag;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
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
	
	private String[] noConsole = {ChatColor.RED + "Sorry, you must be a player to run this command."};
	
	private String[] alreadyPlaying = {ChatColor.RED + "You are already playing a gam of tag!"};
	
	
	
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
    					sender.sendMessage(alreadyPlaying);
    					return false;
    				} else {
    					List<String> playerList = new ArrayList<String>();
    					playerList.add(uuid);
    					// create game and player list
    					plugin.config.set("games." + uuid + ".players", playerList);
    					// set initial "it"
    					plugin.config.set("games." + uuid + ".it", uuid);
    					plugin.saveConfig();
    				}
    				
    			} else {
    				sender.sendMessage(noConsole);
    				return false;
    			}
    		} else if (args[0].equalsIgnoreCase("stop")){
    			if (sender instanceof Player) {
    				Player player = (Player) sender;
    				String uuid = player.getUniqueId().toString();
	    			if (plugin.config.contains("games." + uuid)) {
	    				//TODO: Send to each player in game
	    				plugin.config.set("games." + uuid, null);
	    				plugin.saveConfig();
	    			}
    			}

    		} else if (args[0].equalsIgnoreCase("reload")){
    			
//    			boolean rStatus = plugin.reload();
//    			if (rStatus) {
//    				sender.sendMessage(ChatColor.GREEN + plugin.pluginName + " Reloaded!");
//    			} else {
    				sender.sendMessage(ChatColor.RED + plugin.pluginName + " Reload Failed!");
//    			}
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
