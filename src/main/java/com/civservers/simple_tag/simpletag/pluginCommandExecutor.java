package com.civservers.simple_tag.simpletag;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class pluginCommandExecutor implements CommandExecutor {
	private final SimpleTag plugin;
	private String[] helpMsg = {
			ChatColor.RED + "" + ChatColor.BOLD + "--- SimpleTag Help ---" + ChatColor.RESET,
			ChatColor.GREEN + "" + ChatColor.BOLD + "Please try one of these player comamnds:",
			ChatColor.RESET + "" + ChatColor.GREEN + " simpletag start | join <player> | leave | leaveall | end | kick <player>",
			ChatColor.YELLOW + "" + ChatColor.BOLD + "or one of these admin commands:",
			ChatColor.RESET + "" + ChatColor.YELLOW + " /simpletag endall | reload"
	};
	
	public pluginCommandExecutor(SimpleTag plugin) {
		this.plugin = plugin;
	}
	
	
	
	@Override	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

    	if (cmd.getName().equalsIgnoreCase("simpletag")) {
    		if (args.length < 1) {
    			sender.sendMessage(helpMsg);
    			return false;
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
	}
	
}
