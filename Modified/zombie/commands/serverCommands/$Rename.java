package zombie.commands.serverCommands;

import zombie.characters.IsoPlayer;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.network.GameServer;

@CommandName(
        name = "renameThing"
)

@CommandHelp(
        helpText = "Кост, не забудь исправить"
)

@RequiredRight(
        requiredRights = 32
)

public class $Rename extends CommandBase {
    public $Rename(String var1, String var2, String var3, UdpConnection var4) {
        super(var1, var2, var3, var4);
    }

    protected String Command() {
        DebugLog.log("<<< Start");
        IsoPlayer player = GameServer.getPlayerByUserNameForCommand(this.getExecutorUsername());
        InventoryItem primaryHandItem = player.getPrimaryHandItem();
        String result;

        //TODO: проверить что если вставить число
        DebugLog.log("<<< Before Check");
        if (primaryHandItem.getType().length() > 0) {
            DebugLog.log("<<< He hold something!");
            String oldItemName = primaryHandItem.getName();
            String newItemName = this.getCommandArg(0);
            primaryHandItem.setName(newItemName);
            result = "Название вещи успешно изменено";
            //DebugLog.log(String.format("Игрок %s поменял название вещи %s: со старого %s на новое %s", player.getDisplayName(),primaryHandItem.getName(), oldItemName, newItemName));
        } else {
            result = "Возьми предмет в основную руку, чтобы переименовать";
            DebugLog.log("<<< Nothing :с");
        }

        return result;
    }
}
