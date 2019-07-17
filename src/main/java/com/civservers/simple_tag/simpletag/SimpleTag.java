package com.civservers.simple_tag.simpletag;

import java.util.*;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleTag extends JavaPlugin implements Listener {

	public String pluginName = "SimpleTag";
	public Map<String, Object> msgs;
	List<String> allowedCommands;

	private Map<String,Game> games = new HashMap<>();
	private Map<UUID,String> playersInGames = new HashMap<>();

	private void loadConfig() {
		msgs = getConfig().getConfigurationSection("messages").getValues(true);
		allowedCommands = getConfig().getStringList("allowedCommands");
		allowedCommands.add("simplettag");
		allowedCommands.add("stag");
	}

	@Override
    public void onEnable() {
		saveDefaultConfig();
		loadConfig();

		this.getCommand("simpletag").setExecutor(new pluginCommandExecutor(this));
		
		getServer().getPluginManager().registerEvents(this, this);
		reload();
    }
    
    @Override
    public void onDisable() {
    }
    
    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
    	// Check if damage is pvp
    	if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player victim = (Player)event.getEntity();
			UUID culpritId = ((Player)event.getDamager()).getUniqueId();

			Game game = this.findPlayersGame(culpritId);
			if(game != null) {
				if(game.isIt(culpritId)) {
					if(game.contains(victim.getUniqueId())) {
						game.soundGamePlayers();
						game.sendGamePlayers(msgs.get("tagged").toString().replace("%player%",victim.getDisplayName()));
						game.setIt(victim.getUniqueId());
					}
				}
				//Cancel damage
				if(getConfig().getBoolean("cancelPVPDamage")) {
					event.setCancelled(true);
				}
			}
		}
	}
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
    	if(isPlaying(event.getPlayer().getUniqueId())) {
			leaveGame(event.getPlayer());
		}
    }
    
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
    	Player player = e.getPlayer();
    	if (!player.isOp() && isPlaying(player.getUniqueId()) && getConfig().getBoolean("blockCommands")) {
			String cmd = "";
			if (e.getMessage().indexOf(" ") >= 0) {
				cmd = e.getMessage().substring(1, e.getMessage().indexOf(" "));
			} else {
				cmd = e.getMessage().substring(1, e.getMessage().length());
			}

			if (!allowedCommands.contains(cmd)) {
				sendMessage(player, msgs.get("no_cmds").toString());
				e.setCancelled(true);
			}
    	}
    }

    public Game newGame(Player player, String gameName) {
		UUID playerId = player.getUniqueId();
		Game game = new Game(this, player, gameName);
		this.games.put(game.getName(), game);
		this.playersInGames.put(playerId, game.getName());
		return game;
	}
    
    public void leaveGame(Player player) {
		UUID playerId = player.getUniqueId();
    	Game game = this.findPlayersGame(playerId);
		if (game != null) {
			game.removePlayer(playerId);
			this.removeFromPlayersInGames(playerId);
			sendMessage(player,msgs.get("leave").toString());

			if (game.isStarter(playerId)) {
				game.sendGamePlayers(msgs.get("has_left").toString().replace("%player%",player.getDisplayName()));
				game.stopGame();
			} else if (game.hasNoPlayers()) {
				sendMessage(player,msgs.get("stop").toString());
				game.stopGame();
			} else {
				game.sendGamePlayers(msgs.get("has_left").toString().replace("%player%",player.getDisplayName()));
				if (game.isIt(playerId)) {
					UUID newItPlayerId = game.newIt();
					if(newItPlayerId != null) {
						Player newItPlayer = getServer().getPlayer(newItPlayerId);
						game.soundGamePlayers();
						game.sendGamePlayers(msgs.get("is_it").toString().replace("%player%",newItPlayer.getDisplayName()));
					} else {
						System.out.println(ChatColor.RED + "[SimpleTag] Failed to get new player to be IT on current player leaving.");
						game.stopGame();
					}
				}
			}
		} else {
			sendMessage(player,msgs.get("not_in_game").toString());
		}
    }

	public boolean isPlaying(UUID playerId) {
		return playersInGames.containsKey(playerId);
	}

	public Game findPlayersGame(UUID playerId) {
    	debug(playerId.toString());
		String gameId = playersInGames.get(playerId);
		if(gameId != null) {
			Game game = games.get(gameId);
			if(game != null) {
				return game;
			} else {
				System.out.println(ChatColor.RED + "[SimpleTag] Failed to get game from game ID");
				this.removeFromPlayersInGames(playerId);
			}
		}
		return null;
    }

    public Game findGameById(String gameId) {
		return games.get(ChatColor.stripColor(gameId).toLowerCase());
	}

	public void removeGame(Game game) {
		games.remove(game.getName());
	}

	public List<String> getGameNames() {
		return new ArrayList<>(games.keySet());
	}

    public void removeFromPlayersInGames(UUID playerId) {
		playersInGames.remove(playerId);
	}

	public void addPlayersGameEntry(UUID playerId, String gameId) {
		playersInGames.put(playerId, gameId);
	}

    public void sendMessage(CommandSender p, String msg) {
    	p.sendMessage(ChatColor.translateAlternateColorCodes('&',msgs.get("prefix").toString() + msg));
    }

    public Player getOnlinePlayer(String username) {
		String usernameLower = ChatColor.stripColor(username).toLowerCase();
		for(Player player : this.getServer().getOnlinePlayers()) {
			if(usernameLower.equals(ChatColor.stripColor(player.getDisplayName()).toLowerCase())) {
				return player;
			}
		}
		return null;
    }

    public void debug(String dString) {
    	if (getConfig().getBoolean("debug")) {
			this.getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "["+pluginName+" DEBUG]" + " " + dString);
    	}
    }

    public boolean reload() {
		reloadConfig();
		loadConfig();
		playersInGames.clear();
		games.clear();
		return true;     
    }

}

