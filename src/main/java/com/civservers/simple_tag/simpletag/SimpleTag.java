package com.civservers.simple_tag.simpletag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
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

	public Map<String,Game> games = new HashMap<>();
	public Map<UUID,String> playersInGames = new HashMap<>();

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

			Game game = this.findGame(culpritId);
			if(game != null) {
				if(game.isIt(culpritId)) {
					if(game.playerList.contains(victim.getUniqueId())) {
						game.soundGamePlayers();
						game.sendGamePlayers(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + victim.getDisplayName().toString() + msgs.get("tagged").toString());
						game.it = victim.getUniqueId();
					}
				}
				//Cancel damage
				if((boolean) getConfig().get("cancelPVPDamage")) {
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
				sendPlayer(player, ChatColor.RED + msgs.get("no_cmds").toString());
				e.setCancelled(true);
			}
    	}
    }

    public Game newGame(Player player, String gameName) {
		UUID playerId = player.getUniqueId();
		Game game = new Game(this, player, gameName);
		games.put(game.name, game);
		this.playersInGames.put(playerId, game.name);
		return game;
	}
    
    public void leaveGame(Player player) {
		UUID playerId = player.getUniqueId();
    	Game game = this.findGame(playerId);
		if (game != null) {
			game.removePlayer(playerId);
			this.removePlayersGameEntry(player.getUniqueId());
			sendPlayer(player,ChatColor.RED + msgs.get("leave").toString());

			if (game.playerList.isEmpty()) {
				sendPlayer(player,ChatColor.RED + msgs.get("stop").toString());
				game.stopGame();
			} else {
				String sMsg = ChatColor.BOLD.toString() + ChatColor.RED.toString() + player.getDisplayName() + msgs.get("has_left").toString();
				game.sendGamePlayers(sMsg);
				if (game.isIt(playerId)) {
					UUID newItPlayerId = game.newIt();
					if(newItPlayerId != null) {
						Player newItPlayer = getServer().getPlayer(newItPlayerId);
						game.soundGamePlayers();
						game.sendGamePlayers(newItPlayer.getDisplayName().toString() + msgs.get("is_it") );
					} else {
						System.out.println(ChatColor.RED + "[SimpleTag] Failed to get new player to be IT on current player leaving.");
						game.stopGame();
					}
				}
			}
		} else {
			sendPlayer(player,ChatColor.RED + msgs.get("not_in_game").toString());
		}
    }

	public boolean isPlaying(UUID playerId) {
		return playersInGames.containsKey(playerId);
	}

	public Game findGame(UUID playerId) {
    	debug(playerId.toString());
		String gameId = playersInGames.get(playerId);
		if(gameId != null) {
			Game game = games.get(gameId);
			if(game != null) {
				return game;
			} else {
				System.out.println(ChatColor.RED + "[SimpleTag] Failed to get game from game ID");
				this.removePlayersGameEntry(playerId);
			}
		}
		return null;
    }

    public Game findGame(String gameId) {
		return games.get(gameId);
	}

    public void removePlayersGameEntry(UUID playerId) {
		playersInGames.remove(playerId);
	}

	public void addPlayersGameEntry(UUID playerId, String gameId) {
		playersInGames.put(playerId, gameId);
	}

    public void sendPlayer(Player p, String msg) {
    	p.sendMessage(ChatColor.YELLOW + msgs.get("prefix").toString() + msg);
    }

    public Player getOnlinePlayer(String username) {
		String usernameLower = username.toLowerCase();
		for(Player player : this.getServer().getOnlinePlayers()) {
			if(usernameLower.equals( ChatColor.stripColor(player.getDisplayName()).toLowerCase())) {
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

