package com.civservers.simple_tag.simpletag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class pluginCommandExecutor implements CommandExecutor, TabCompleter {
	private final SimpleTag plugin;
	
	private String[] helpMsg = {
			ChatColor.RED + "" + ChatColor.BOLD + "--- SimpleTag Help ---" + ChatColor.RESET,
			ChatColor.GREEN + "" + ChatColor.BOLD + "Please try one of these player comamnds:",
			ChatColor.RESET + "" + ChatColor.GREEN + " simpletag start <game> | join <game> | leave | stop | kick <player>",
			ChatColor.YELLOW + "" + ChatColor.BOLD + "or one of these admin commands:",
			ChatColor.RESET + "" + ChatColor.YELLOW + " /simpletag reload"
	};

	private static final String[] firstArguments = {
			"start",
			"stop",
			"kick",
			"join",
			"leave",
			"reload"
	};
	private static final String[] firstArgumentsPermissions = {
			"simpletag.create",
			"simpletag.create",
			"simpletag.create",
			"simpletag.play",
			"simpletag.play",
			"simpletag.admin"
	};

	public pluginCommandExecutor(SimpleTag plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args)
	{
		if(args.length == 1) {
			String arg0lower = args[0].toLowerCase();
			List<String> matchingFirstArguments = new ArrayList<>();
			for(int i = 0; i < firstArguments.length; ++i) {
				if(arg0lower.isEmpty() || firstArguments[i].startsWith(arg0lower)) {
					if(commandSender.hasPermission(firstArgumentsPermissions[i])) {
						matchingFirstArguments.add(firstArguments[i]);
					}
				}
			}
			return matchingFirstArguments;
		}
		if(args.length == 2) {
			String arg0lower = args[0].toLowerCase();
			if(arg0lower.equals("join")) {
				return plugin.getGameNames();
			}
			if(arg0lower.equals("kick")) {
				return null;    // null return lets server display player names.
			}
		}
		return Collections.emptyList();
	}

	@Override	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
    	if (cmd.getName().equalsIgnoreCase("simpletag")) {
			if (args.length < 1) {
				sender.sendMessage(helpMsg);
			} else if (args[0].equalsIgnoreCase("reload")){
				if (sender.hasPermission("simpletag.admin")) {
					boolean rStatus = plugin.reload();
					if (rStatus) {
						sender.sendMessage(ChatColor.GREEN + plugin.pluginName + " Reloaded!");
					} else {
						sender.sendMessage(ChatColor.RED + plugin.pluginName + " Reload Failed!");
					}
				} else {
					plugin.sendMessage(sender, plugin.msgs.get("no_perm").toString());
				}
			} else if (sender instanceof Player) {
    			Player player = (Player) sender;
    			UUID playerId = player.getUniqueId();
    			switch(args[0].toLowerCase())
				{
				case "start":
					if (sender.hasPermission("simpletag.create")) {
						if (args.length > 1) {
							if (plugin.isPlaying(playerId)) {
								// player is already playing a game of tag.  Cannot create a new one.
								plugin.sendMessage(player, plugin.msgs.get("already_playing").toString());
							} else if(plugin.findGameById(args[1]) != null) {
								plugin.sendMessage(player, plugin.msgs.get("game_exists").toString());
							} else {
								// create game and player list
								plugin.newGame(player, args[1]);
								if (plugin.getConfig().getBoolean("broadcastGameStart")) {
									plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&',plugin.msgs.get("prefix").toString() + plugin.msgs.get("started_broadcast").toString().replace("%player%",player.getDisplayName()).replace("%game%",args[1])));
								} else {
									plugin.sendMessage(player, plugin.msgs.get("started").toString().replace("%game%",args[1]));
								}
								if (plugin.getConfig().getBoolean("setSurvival")) {
									player.setGameMode(GameMode.SURVIVAL);
								}
								if (plugin.getConfig().getBoolean("disableFly")) {
									player.setFlying(false);
								}
							}
						} else {
							plugin.sendMessage(player, plugin.msgs.get("need_game").toString());
						}
					} else {
						plugin.sendMessage(player, plugin.msgs.get("no_perm").toString());
					}
					break;
				case "stop":
	    			if (sender.hasPermission("simpletag.create")) {
	    				Game game = plugin.findPlayersGame(playerId);
	    				if(game != null) {
	    					if (game.isStarter(playerId)) {
								game.stopGame();
							} else {
								plugin.sendMessage(player, plugin.msgs.get("not_starter").toString());
							}
						} else {
							plugin.sendMessage(player, plugin.msgs.get("not_in_game").toString());
						}
	    			} else {
	    				plugin.sendMessage(player, plugin.msgs.get("no_perm").toString());
	    			}
	    			break;
				case "kick":
					if (sender.hasPermission("simpletag.create")) {
						if (args.length > 1) {
							Game game = plugin.findPlayersGame(playerId);
							if(game != null) {
								if (game.isStarter(playerId)) {
									Player playerToKick = plugin.getOnlinePlayer(args[1]);
									if(playerToKick != null) {
										plugin.leaveGame(playerToKick);
										plugin.sendMessage(playerToKick, plugin.msgs.get("kicked").toString());
									} else {
										plugin.sendMessage(player, plugin.msgs.get("not_online").toString());
									}
								} else {
									plugin.sendMessage(player, plugin.msgs.get("not_starter").toString());
								}
							} else {
								plugin.sendMessage(player, plugin.msgs.get("not_in_game").toString());
							}
						} else {
							plugin.sendMessage(player, plugin.msgs.get("need_player").toString());
						}
					} else {
						plugin.sendMessage(player, plugin.msgs.get("no_perm").toString());
					}
					break;
				case "join":
					if (sender.hasPermission("simpletag.play")) {
	    				if (args.length > 1) {
			    			if(plugin.isPlaying(playerId)) {
			    				plugin.sendMessage(player, plugin.msgs.get("already_playing").toString());
			    			} else {
			    				Game game = plugin.findGameById(args[1]);
			    				if (game == null) {
									plugin.sendMessage(player, plugin.msgs.get("no_game").toString());
			    				} else {
									plugin.debug(game.getName());
									game.addPlayer(playerId);
									plugin.addPlayersGameEntry(playerId,game.getName());
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
	    					plugin.sendMessage(player, plugin.msgs.get("need_game").toString());
	    				}
	    			} else {
	    				plugin.sendMessage(player, plugin.msgs.get("no_perm").toString());
	    			}
	    			break;
				case "leave":
	    			if (sender.hasPermission("simpletag.play")) {
						plugin.leaveGame(player);
	    			} else {
	    				plugin.sendMessage(player, plugin.msgs.get("no_perm").toString());
	    			}
	    			break;
				default:
	    			sender.sendMessage(helpMsg);
	    			break;
	    		}
    		} else {
    			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',plugin.msgs.get("no_console").toString()));
    		}
    	}
		return true;
	}
}
