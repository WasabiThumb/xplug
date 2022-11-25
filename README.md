# <!-- XPlug --> <img src="https://wasabicodes.xyz/cdn/b1c680ff86f27f18d4c896f3af5049e5/banner.png" alt="XPlug" style="height: 5em">
**A LUA Platform for Spigot**

![Build Status](https://img.shields.io/github/workflow/status/WasabiThumb/xplug/Java%20CI%20with%20Maven?logo=github&style=for-the-badge)
![Download Count](https://img.shields.io/github/downloads/WasabiThumb/xclaim/total?label=DOWNLOADS&logo=github&style=for-the-badge)
![Issues](https://img.shields.io/github/issues/WasabiThumb/xplug?style=for-the-badge&logo=github)\
![License](https://img.shields.io/github/license/WasabiThumb/xplug?style=for-the-badge)
![Release](https://img.shields.io/github/v/release/WasabiThumb/xplug?include_prereleases&style=for-the-badge)
![Minecraft Version](https://img.shields.io/badge/MINECRAFT-1.8%20--%201.19-informational?style=for-the-badge)

<strong>THIS PROJECT IS IN HEAVY DEVELOPMENT! DO NOT EXPECT IT TO RESEMBLE A FINISHED PRODUCT</strong>

## Usage
When the plugin starts for the first time, a folder called ``scripts`` in ``plugins/XPlug/`` will be filled with the [provided sample scripts](https://github.com/WasabiThumb/xplug/tree/master/src/main/resources/examples).\
You can run these scripts with ``/xp run <scriptName>``, or create your own by simply adding a .lua file to the scripts folder.\
You can also make a project by adding a folder to the scripts folder with the name of your project, and adding an index.lua to that folder.\
You can run your project with ``/xp run <projectName>``.

### Sample Scripts
Script Name | Description
--: | :--
hello_world | Sends a message to every player and the console with the text "Hello World!" colored rainbow, a good example of chat APIs.
halos | Adds a glowing golden halo to every player. A good example of the hook and timer library. You can stop this with ``/xp exec stopHalo()``
swap | Swaps the position of every player with the position of another player. A good example of entity APIs.
whack | Adds a command ``/whack [player] [times]`` that repeatedly damages a target player in quick succession. A good example of command APIs. It is a known issue that commands added with LUA scripts don't reliably tab complete, however they will execute.
materials | Places some blocks of different materials around the player. Only executes when there is 1 player online, and that player is an operator. This is meant for testing purposes and may be removed later.

### Commands
Note that ``xplug`` has a shorter alias ``xp``
Command Name | Description | Permission
--: | :-: | :--
xplug | XPlug Main Command, an alias of /xplug help | xplug.xplug
xplug help | Shows basic help for XPlug, | xplug.xplug
xplug info | Shows some debug info for XPlug | xplug.info
xplug exec <LUA code> | Runs some LUA code in the XPlug interpreter | xplug.exec
xplug run <package> | Runs a LUA package as described [here](#usage) | xplug.run

### Scripting API
Documentation for this flavor of LUA is still in progress, however it is inspired in many ways by GMod Lua and some of the libraries intentionally mirror their GMod Lua equivalents.\
By far the most useful library is the "server" library, for example ``server.GetPlayers()`` and ``server.GetWorld()``.\
See the experimental API wiki here: [https://wasabithumb.github.io/xplug/](https://wasabithumb.github.io/xplug/)

#### Events supported by hook.Add
Hooks called with hook.Add are called in priority order for each event, until a callback returns a non-nil value.\
If the value is falsy, the event is cancelled. If the value is truthy, the event is guaranteed to pass as long as another plugin does not cancel it.\
The priority set in hook.Add only affects when the hook is called in relation to other XPlug hooks, other plugin events can fire before or after all XPlug events.
Event Name | Arguments | Description
--: | :-: | :--
Think | nil | Runs every server tick
PlayerJoin | Player player, String joinMessage | Called when a player joins
PlayerLeave | Player player, String quitMessage | Called when a player quits
PlayerChangeWorld | Player player, World a, World b | Called when a player moves between worlds
PlayerMove | Player player, Location a, Location b | Called when a player moves within a world.
PlayerChat | Player player, String message | Called when a player chats
PlayerChangeGameMode | Player player, int previousMode, int newMode | Called when a player changes their game mode
PlayerPlaceBlock | Player player, Block block, Block placedAgainst, ItemStack itemInHand | Called when a player places a block
PlayerBreakBlock | Player player, Block block, int expToDrop, Function<int> setExpToDrop | Called when a player breaks a block
PlayerInteract | Player player, ItemStack itemInHand, boolean leftClick, Block blockClicked, Entity entityClicked, Location clickLocation | Called when a plyer interacts with a block or entity (cannot be both)
PlayerInteractPhysical | Player player, Block block | Called when a player interacts with a block physically (pressure plate, trample, etc)
PlayerDropItem | Player player, Entity itemEntity, ItemStack item | Called when a player drops an item
EntityDamage | Entity entity, String cause, double damage, Function<double> setDamage | Called when an entity takes damage for any reason
EntityDamageByBlock | Entity entity, Block block, String cause, double damage, Function<double> setDamage | Called when an entity takes damage from a block
EntityDamageByEntity | Entity entity, Entity damager, String cause, double damage, Function<double> setDamage | Called when an entity takes damage from an entity
EntityDeath | Entity entity, int exp, Function<int> setExp, table<ItemStack> drops, Function<table<ItemStack>> setDrops | Called when an entity dies
PlayerDeath | Entity entity, int exp, Function<int> setExp, table<ItemStack> drops, Function<table<ItemStack>> setDrops, String deathMessage, Function<String> setDeathMessage, boolean keepInventory, Function<boolean> setKeepInventory | Called when a player dies

## Roadmap
1. Finish documentation
2. Support all the most common events
3. Make EntityType and PotionEffect version-independent
4. TBA

## Copyright Notice
Copyright 2022 Wasabi Codes
Mozilla Public License Version 2.0
