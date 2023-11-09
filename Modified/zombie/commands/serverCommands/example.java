//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package zombie.commands.serverCommands;

import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import zombie.characters.Faction;
import zombie.characters.IsoPlayer;
import zombie.commands.CommandBase;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.network.DBResult;
import zombie.network.GameServer;
import zombie.network.ServerWorldDatabase;
import zombie.network.packets.hit.Vehicle;
import zombie.vehicles.BaseVehicle;

@CommandName(
        name = "top"
)
@RequiredRight(
        requiredRights = 63
)
public class example extends CommandBase {
    public example(String var1, String var2, String var3, UdpConnection var4) {
        super(var1, var2, var3, var4);
    }

    protected String Command() throws SQLException {

        // Возвращает объект игрока IsoPlayer того, кто ввел команду
        IsoPlayer player = GameServer.getPlayerByRealUserName(this.getExecutorUsername());

        String timeSurvived = player.getTimeSurvived();
        String name = player.getDisplayName();
        //BaseVehicle vehicle = player.getVehicle();
        String info = "Player live: " + timeSurvived + " his name " + name;

        //Инфо в логах, пишется в "DebugServerLog"
        DebugLog.log("<" + info + ">");
        return "Этот текст возвращается игроку когда команда отработала".concat(" Игрок выжил" + timeSurvived);
    }
}
