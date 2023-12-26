# ProjectZomboid
The code controls the placement of objects in the game world, with special attention paid to objects representing water, their placement is handled in a special way.

Checks various conditions related to changes in player levels and experience. Includes checks for level reductions, unacceptable experience changes, and possible level cheats.
If anomalies are detected (for example, lowering levels without appropriate experience), corresponding messages are displayed in the logs indicating the player's name (var1.username).

# Updates
Added the ability to add to an external file (.txt) a list of those items, when installed, the administrator should receive a notification about this, I’m talking about prohibited objects that ordinary players cannot install on the server.
Now you can add any number of item IDs to a text document and they will be checked on the server

Made the work of future administrators a little easier.
Now all cheating actions on the server will be stored in a separate log file and all actions of players that may indicate cheating will be recorded there.

This file will be created in the standard path

Example:
11-12-23_20-42-30_CustomLog.txt

Inside:

[11-12-23 20:44:18.166] <BUILD> 192.168.1.000 player wants to build WATER.

# Updates
Now you can get the player's inventory, as well as set a search for those items that are not available for use.
If you want to prevent the use of bombs on the server, then the usual method will not work, so now there is a solution.

LOG  : General, 1702763929757> 616 233 583> PipeBomb found Marginal 192.168.1.***.
LOG  : General, 1702763929761> 616 233 587> PipeBomb found Marginal 192.168.1.***.
# Updates
Now it’s easier to work with self-written methods, I moved them to a separate file and connected them to the main one
# Updates
Results: Automatic creation of a folder and file inside for the administrator, in which you create a list for those objects that cannot be used by ordinary players
If a player uses this object, a ban occurs on the server by IP and player name 🙄 

https://github.com/CookieVortex/ProjectZomboid/assets/24642100/d094bdb2-0657-4394-878c-2e79997b4d09

![Screenshot_2](https://github.com/CookieVortex/ProjectZomboid/assets/24642100/21a6096a-3884-4c5d-a5a9-31cc551c9761)
![Screenshot_1](https://github.com/CookieVortex/ProjectZomboid/assets/24642100/e63a202b-08f5-401e-b739-efdd8f8fcdf7)

# Updates

Dynamically adding a player's inventory to the database

https://github.com/CookieVortex/ProjectZomboid/assets/24642100/4c5cef94-77ab-48a4-89d1-9ae248628d63


