package com.civservers.simple_tag.simpletag;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class pluginCommandExecutor implements CommandExecutor {
	private final SimpleTag plugin;
	
	public pluginCommandExecutor(SimpleTag plugin) {
		this.plugin = plugin;
	}
	
	
	
	@Override	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
/* SAMPLE COMMANDS */
    	if (cmd.getName().equalsIgnoreCase("mycmd")) { // If the player typed /basic then do the following, note: If you only registered this executor for one command, you don't need this
    		// doSomething

    		
    		for (World world : Bukkit.getWorlds()) {
    			for (Entity entity : world.getEntities()) {
        			Bukkit.getConsoleSender().sendMessage(world.getName() + ": " + entity.getType() + " > " + entity.getName());    				
    			}

    			
    		}
    		

    		
    		return true;   		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("mycmd2")) {
// THIS COMMAND MUST BE RUN BY A PLAYER
    		if (!(sender instanceof Player)) {
    			sender.sendMessage("This command can only be run by a player.");
    		} else {
    			Player player = (Player) sender;
    			sender.sendMessage("Command completed sucsessfully!");
    		}
    		return true;
    		
    		
    		
// THIS CMD MUST HAVE THE RIGHT NUMBER OF ARGUMENTS
    	} else if (cmd.getName().equalsIgnoreCase("mycmd3")) {
    		if (args.length > 1) {
    			sender.sendMessage("Too many arguments");
    			return false;
    		} else if (args.length < 1) {
    			String reMsg[] = {"Not enough arguments!","Try again..."};
    			sender.sendMessage(reMsg);
    		} else {
    			Player target = (Bukkit.getServer().getPlayer(args[0]));
    			if (target == null) {
    				sender.sendMessage(args[0] + " is not online!");
    				return false;
    			} else {
    				sender.sendMessage("We've found " + args[0]);
    				return true;
    			}
    		}
    	} else {
// THESE COMMANDS DO NOT EXIST
    		sender.sendMessage("I really don't understand what you want me to do.");
    		return false;
    	}
    	return false;
	}
	
}
