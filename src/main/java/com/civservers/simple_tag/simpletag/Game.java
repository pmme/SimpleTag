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

    private String name;
    private UUID starter;
    private List<UUID> playerList = new ArrayList<>();
    private UUID it;

    public Game(SimpleTag plugin, Player player, String gameName) {
        this.plugin = plugin;
        this.name = ChatColor.stripColor(gameName).toLowerCase();
        this.starter = player.getUniqueId();
        this.it = this.starter;
        playerList.add(this.starter);
    }

    public String getName() { return name; }

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

    public boolean isStarter(UUID playerId) { return playerId == starter; }

    public void addPlayer(UUID playerId)
    {
        playerList.add(playerId);
    }

    public void removePlayer(UUID playerId)
    {
        playerList.remove(playerId);
    }

    public boolean contains(UUID playerId) { return playerList.contains(playerId); }

    public boolean hasNoPlayers() { return playerList.isEmpty(); }

    public void stopGame()
    {
        for(UUID playerId : playerList) {
            Player player = plugin.getServer().getPlayer(playerId);
            if(player != null) {
                plugin.sendMessage(player, plugin.msgs.get("stop").toString());
                plugin.removeFromPlayersInGames(playerId);
            }
        }
        plugin.removeGame(this);
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
                plugin.sendMessage(player, msg);
            }
        }
    }
}
