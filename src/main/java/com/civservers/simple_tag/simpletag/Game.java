package com.civservers.simple_tag.simpletag;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Game
{
    private final SimpleTag plugin;

    String id;
    List<UUID> playerList = new ArrayList<>();
    UUID it;

    public Game(SimpleTag plugin, String id) {
        this.plugin = plugin;
        this.id = id;
    }

    public Game(SimpleTag plugin, Player player) {
        this.plugin = plugin;
        this.id = ChatColor.stripColor(player.getDisplayName());
        this.it = player.getUniqueId();
        playerList.add(this.it);
    }

    public boolean isIt(UUID playerId) {
        return playerId == it;
    }

    public void setIt(UUID playerId) {
        it = playerId;
    }

    public UUID newIt() {
        if(playerList.size() > 0) {
            Random rng = new Random();
            int n = rng.nextInt(playerList.size());
            it = playerList.get(n);
            return it;
        }
        return null;
    }

    public void addPlayer(UUID playerId)
    {
        playerList.add(playerId);
    }

    public void removePlayer(UUID playerId)
    {
        playerList.remove(playerId);
    }

    public void stopGame()
    {
        for(UUID playerId : playerList) {
            Player player = plugin.getServer().getPlayer(playerId);
            if(player != null) {
                plugin.sendPlayer(player, ChatColor.RED + plugin.msgs.get("stop").toString());
                plugin.removePlayersGameEntry(playerId);
            }
        }
        plugin.games.remove(this.id);
    }

    public void soundGamePlayers()
    {
        for(UUID playerId : playerList) {
            Player player = plugin.getServer().getPlayer(playerId);
            if(player != null) {
                player.playSound(player.getLocation(), Sound.valueOf(plugin.getConfig().getString("tagSound")), 3f, 1f);
            }
        }
    }

    public void sendGamePlayers(final String msg)
    {
        plugin.debug(msg);
        for(UUID playerId : playerList) {
            Player player = plugin.getServer().getPlayer(playerId);
            if(player != null) {
                plugin.sendPlayer(player, msg);
            }
        }
    }
}