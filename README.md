# ProjectZomboid
The code controls the placement of objects in the game world, with special attention paid to objects representing water, their placement is handled in a special way.

Checks various conditions related to changes in player levels and experience. Includes checks for level reductions, unacceptable experience changes, and possible level cheats.
If anomalies are detected (for example, lowering levels without appropriate experience), corresponding messages are displayed in the logs indicating the player's name (var1.username).

# Updates
Added the ability to add to an external file (.txt) a list of those items, when installed, the administrator should receive a notification about this, I’m talking about prohibited objects that ordinary players cannot install on the server.
Now you can add any number of item IDs to a text document and they will be checked on the server
