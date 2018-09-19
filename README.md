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

    #prevent players from hurting each other while playing a game of tag.
    cancelPVPDamage: true #
    
    #enable debugging output
    debug: false 

	#sound played when someone gets tagged
    tagSound: BLOCK_ANVIL_PLACE

## Support
We try to be available whenever possible on Discord at [https://discord.gg/dYRcb3f](https://discord.gg/dYRcb3f)
## Contributing
We appreciate any contributions.  
- Please make submissions via pull requests on GitHub. 
- Please licensed submissions under GNU Lesser General Public License v3.
## Links
**Spigot Resource Listing:** Coming Soon

**Issues:** [https://github.com/johnelder/SimpleTag/issues](https://github.com/johnelder/SimpleTag/issues)

**Testing:** Join in game at pubcraft.civservers.com
