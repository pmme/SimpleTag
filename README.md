#
# bukkit-SimpleTag
**Description:** SimpleTag is a very simple, tag mini-game for bukkit/spigot minecraft servers.  In order to keep things simple, there are no arenas.  Players can start, stop, join and leave tag games anywhere in the server.  By default, the player that starts the game of tag has access to the admin commands for that game. *i.e. /stag kick JoeUser

**Tested On:** Spigot 1.13.1

## Commands

Aliases: /simpletag or /stag
 - /stag reload	-	Reloads the configuration file.
 - /stag start	-	Starts a new game of tag with you as the game owner/admin.
 - /stag stop	-	Stops your running game of tag if you have one.
 - /stag join [playerName]	-	Joins a game that another player is playing.
 - /stag leave - Leave the tag game that you are in.

## Permissions

 - simpletag.create - Allows the use of start / end / kick commands to create and manage games.
-  simpletag.play - Allows the use of join / leave commands to play existing games.
- simpletag.admin - Allows use of reload and endAll commands.

## Default Config File

```
	#------------------------------------------------------------------------------
	#      SimpleTag by Civalo
	#
	# Spigot address: https://www.spigotmc.org/resources/simpletag.60986/
	# Github: https://github.com/johnelder/SimpleTag
	#
	# License: GNU Lesser General Public License v3
	#
	#
	#   cancelPVPDamage: true (Disables pvp for tag players when set to true)
	#   tagSound: BLOCK_NOTE_BLOCK_CHIME (Played when a player gets tagged. See https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html for options)
	#   debug: false (Shows debug info in console when set to true)
	#   messages: (Various messages to palyers duting game)
	#      prefix: '[SimpleTag] '
	#      no_console: 'Sorry, you must be a player to run this command.'
	#      already_playing: 'You are already playing a game of tag!'
	#      leave: 'You have left the game!'
	#      stop: 'The game has stopped!'
	#      no_game: 'Cannot find the game you requested.'
	#      not_online: 'That player is not online!'
	#      joined: 'You have joined the game!'
	#      started: 'Your tag game has started! Invite players to join! /stag join <yourname>'
	#      tagged: ' has been tagged!'
	#      is_it: ' is now it!'
	#      has_left: ' has left he game!'
	#      no_perm: 'You need to have permission for that command!'
	#      kicked: 'You have been kicked from the game of tag!'
	#
	#
	#   
	#
	#------------------------------------------------------------------------------
	
	cancelPVPDamage: true
	tagSound: BLOCK_NOTE_BLOCK_CHIME
	debug: false
	messages:
	   prefix: '[SimpleTag] '
	   no_console: 'Sorry, you must be a player to run this command.'
	   already_playing: 'You are already playing a game of tag!'
	   leave: 'You have left the game!'
	   stop: 'The game has stopped!'
	   no_game: 'Cannot find the game you requested.'
	   not_online: 'That player is not online!'
	   joined: 'You have joined the game!'
	   started: 'Your tag game has started! Invite players to join! /stag join <yourname>'
	   tagged: ' has been tagged!'
	   is_it: ' is now it!'
	   has_left: ' has left he game!'
	   no_perm: 'You need to have permission for that command!'
	   kicked: 'You have been kicked from the game of tag!'
```


## Support
We try to be available whenever possible on Discord at [https://discord.gg/dYRcb3f](https://discord.gg/dYRcb3f)
## Contributing
We appreciate any contributions.  
- Please make submissions via pull requests on GitHub. 
- Please licensed submissions under GNU Lesser General Public License v3.
## Links
**Spigot Resource Listing:** [https://www.spigotmc.org/resources/simpletag.60986/](https://www.spigotmc.org/resources/simpletag.60986/)

**Issues:** [https://github.com/johnelder/SimpleTag/issues](https://github.com/johnelder/SimpleTag/issues)

**Testing:** Join in game at techunlimitedgroup.com:25560 or pubcraft.civservers.com
