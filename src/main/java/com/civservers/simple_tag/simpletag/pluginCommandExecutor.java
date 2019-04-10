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
			ChatColor.RESET + "" + ChatColor.GREEN + " simpletag start <game> | join <game> | leave | stop | kick <player>",
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
						if (args.length > 1) {
							if (plugin.isPlaying(playerId)) {
								// player is already playing a game of tag.  Cannot create a new one.
								plugin.sendPlayer(player, plugin.msgs.get("already_playing").toString());
							} else {
								// create game and player list
								plugin.newGame(player, args[1]);
								if (plugin.getConfig().getBoolean("broadcastGameStart")) {
									plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&',plugin.msgs.get("prefix").toString() + plugin.msgs.get("started_broadcast").toString().replace("%player%",player.getDisplayName()).replace("%game%",args[1])));
								} else {
									plugin.sendPlayer(player, plugin.msgs.get("started").toString().replace("%game%",args[1]));
								}
								if (plugin.getConfig().getBoolean("setSurvival")) {
									player.setGameMode(GameMode.SURVIVAL);
								}
								if (plugin.getConfig().getBoolean("disableFly")) {
									player.setFlying(false);
								}
							}
						} else {
							plugin.sendPlayer(player, plugin.msgs.get("need_game").toString());
						}
					} else {
						plugin.sendPlayer(player, plugin.msgs.get("no_perm").toString());
					}
				} else if (args[0].equalsIgnoreCase("stop")){
	    			if (sender.hasPermission("simpletag.create")) {
	    				Game game = plugin.findGame(playerId);
	    				if(game != null) {
	    					if (game.starter == playerId) {
								game.stopGame();
							} else {
								plugin.sendPlayer(player, plugin.msgs.get("not_starter").toString());
							}
						} else {
							plugin.sendPlayer(player, plugin.msgs.get("not_in_game").toString());
						}
	    			} else {
	    				plugin.sendPlayer(player, plugin.msgs.get("no_perm").toString());
	    			}
	    		} else if (args[0].equalsIgnoreCase("kick")){
					if (sender.hasPermission("simpletag.create")) {
						if (args.length > 1) {
							Game game = plugin.findGame(playerId);
							if(game != null) {
								if (game.starter == playerId) {
									Player playerToKick = plugin.getOnlinePlayer(args[1]);
									if(playerToKick != null) {
										plugin.leaveGame(playerToKick);
										plugin.sendPlayer(playerToKick, plugin.msgs.get("kicked").toString());
									} else {
										plugin.sendPlayer(player, plugin.msgs.get("not_online").toString());
									}
								} else {
									plugin.sendPlayer(player, plugin.msgs.get("not_starter").toString());
								}
							} else {
								plugin.sendPlayer(player, plugin.msgs.get("not_in_game").toString());
							}
						} else {
							plugin.sendPlayer(player, plugin.msgs.get("need_player").toString());
						}
					} else {
						plugin.sendPlayer(player, plugin.msgs.get("no_perm").toString());
					}
				} else if (args[0].equalsIgnoreCase("join")){
					if (sender.hasPermission("simpletag.play")) {
	    				if (args.length > 1) {
			    			if(plugin.isPlaying(playerId)) {
			    				plugin.sendPlayer(player, plugin.msgs.get("already_playing").toString());
			    			} else {
			    				Game game = plugin.findGame(ChatColor.stripColor(args[1]).toLowerCase());
			    				if (game == null) {
									plugin.sendPlayer(player, plugin.msgs.get("no_game").toString());
			    				} else {
									plugin.debug(game.name);
									game.addPlayer(playerId);
									plugin.addPlayersGameEntry(playerId,game.name);
									game.sendGamePlayers(plugin.msgs.get("joined").toString().replace("%player%",player.getDisplayName()));
									if (plugin.getConfig().getBoolean("setSurvival")) {
										player.setGameMode(GameMode.SURVIVAL);
									}
									if (plugin.getConfig().getBoolean("disableFly")) {
										player.setFlying(false);
									}
			    				}
			    			}
	    				} else {
	    					plugin.sendPlayer(player, plugin.msgs.get("need_game").toString());
	    				}
	    			} else {
	    				plugin.sendPlayer(player, plugin.msgs.get("no_perm").toString());
	    			}
	    		} else if (args[0].equalsIgnoreCase("leave")){
	    			if (sender.hasPermission("simpletag.play")) {
						plugin.leaveGame(player);
	    			} else {
	    				plugin.sendPlayer(player, plugin.msgs.get("no_perm").toString());
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
	    				plugin.sendPlayer(player, plugin.msgs.get("no_perm").toString());
	    			}
	    		} else {
	    			sender.sendMessage(helpMsg);
	    		}
    		} else {
    			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',plugin.msgs.get("no_console").toString()));
    		}
    	}
		return true;
	}
}
