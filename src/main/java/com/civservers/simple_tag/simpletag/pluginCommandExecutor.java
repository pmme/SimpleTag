package com.civservers.simple_tag.simpletag;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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
    			UUID playerId = player.getUniqueId();
	    		if (args.length < 1) {
	    			player.sendMessage(helpMsg);
	    		} else if (args[0].equalsIgnoreCase("test")){
	    			plugin.debug(player.getDisplayName().toString());
	    		} else if (args[0].equalsIgnoreCase("start")){
	    			if (sender.hasPermission("simpletag.create")) {
	    				if (plugin.isPlaying(playerId)) {
	    					// player is already playing a game of tag.  Cannot create a new one.
	    					plugin.sendPlayer(player, ChatColor.RED + plugin.msgs.get("already_playing").toString());
	    				} else {
	    					// create game and player list
							plugin.newGame(player);
	    					plugin.sendPlayer(player, ChatColor.GREEN + plugin.msgs.get("started").toString());
	    					if (plugin.getConfig().getBoolean("setSurvival")) {
	    						player.setGameMode(GameMode.SURVIVAL);
	    					}
	    					if (plugin.getConfig().getBoolean("disableFly")) {
	    						player.setFlying(false);
	    					}
	    				}
	    			} else {
	    				plugin.sendPlayer(player, ChatColor.RED + plugin.msgs.get("no_perm").toString());
	    			}
	    		} else if (args[0].equalsIgnoreCase("stop")){
	    			if (sender.hasPermission("simpletag.create")) {
	    				Game game = plugin.findGame(playerId);
	    				if(game != null) {
	    					game.stopGame();
						} else {
							plugin.sendPlayer(player,ChatColor.RED + plugin.msgs.get("not_in_game").toString());
						}
	    			} else {
	    				plugin.sendPlayer(player, ChatColor.RED + plugin.msgs.get("no_perm").toString());
	    			}
	    		} else if (args[0].equalsIgnoreCase("kick")){
	    			if (args.length > 1) {
		    			if (sender.hasPermission("simpletag.create")) {
		    				Game game = plugin.findGame(playerId);
		    				if(game != null) {
								Player playerToKick = plugin.getOnlinePlayer(args[1]);
								plugin.leaveGame(playerToKick);
								plugin.sendPlayer(playerToKick,ChatColor.RED + plugin.msgs.get("kicked").toString());
							} else {
								plugin.sendPlayer(player,ChatColor.RED + plugin.msgs.get("not_in_game").toString());
							}
		    			} else {
		    				plugin.sendPlayer(player, ChatColor.RED + plugin.msgs.get("no_perm").toString());
		    			}
	    			} else {
    					plugin.sendPlayer(player, ChatColor.RED + plugin.msgs.get("more_args").toString());
    				}
	    		} else if (args[0].equalsIgnoreCase("join")){
	    			if (sender.hasPermission("simpletag.play")) {
	    				if (args.length > 1) {
			    			if(plugin.isPlaying(playerId)) {
			    				plugin.sendPlayer(player, ChatColor.RED + plugin.msgs.get("already_playing").toString());
			    			} else {
			    				Game game = plugin.findGame(ChatColor.stripColor(args[1]).toLowerCase());
			    				if (game == null) {
									plugin.sendPlayer(player, ChatColor.RED + plugin.msgs.get("no_game").toString());
			    				} else {
									plugin.debug(game.id);
									game.addPlayer(playerId);
									plugin.addPlayersGameEntry(playerId,game.id);
									game.sendGamePlayers(ChatColor.GREEN + player.getDisplayName() + plugin.msgs.get("joined").toString());
									if (plugin.getConfig().getBoolean("setSurvival")) {
										player.setGameMode(GameMode.SURVIVAL);
									}
									if (plugin.getConfig().getBoolean("disableFly")) {
										player.setFlying(false);
									}
			    				}
			    			}
	    				} else {
	    					plugin.sendPlayer(player, ChatColor.RED + plugin.msgs.get("more_args").toString());
	    				}
	    			} else {
	    				plugin.sendPlayer(player, ChatColor.RED + plugin.msgs.get("no_perm").toString());
	    			}
	    		} else if (args[0].equalsIgnoreCase("leave")){
	    			if (sender.hasPermission("simpletag.play")) {
						plugin.leaveGame(player);
	    			} else {
	    				plugin.sendPlayer(player, ChatColor.RED + plugin.msgs.get("no_perm").toString());
	    			}
	    		} else if (args[0].equalsIgnoreCase("reload")){
	    			if (sender.hasPermission("simpletag.admin")) {
		    			boolean rStatus = plugin.reload();
		    			if (rStatus) {
		    				sender.sendMessage(ChatColor.GREEN + plugin.pluginName + " Reloaded!");
		    			} else {
		    				sender.sendMessage(ChatColor.RED + plugin.pluginName + " Reload Failed!");
		    			}
	    			} else {
	    				plugin.sendPlayer(player, ChatColor.RED + plugin.msgs.get("no_perm").toString());
	    			}
	    		} else {
	    			sender.sendMessage(helpMsg);
	    		}
    		} else {
    			sender.sendMessage(ChatColor.RED + plugin.msgs.get("no_console").toString());
    		}
    	}
		return true;
	}
}
