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

@CommandName(
        name = "top"
)
@RequiredRight(
        requiredRights = 63
)
public class playersTop extends CommandBase {
    public playersTop(String var1, String var2, String var3, UdpConnection var4) {
        super(var1, var2, var3, var4);
    }

    protected String Command() throws SQLException {
        ArrayList<DBResult> playersListDBResult = ServerWorldDatabase.instance.getTableResult("whitelist");
        HashMap<String, Double> timePlayersList = new HashMap();
        HashMap<String, Integer> killersZombiesList = new HashMap();
        HashMap<String, Integer> killersSurvivorList = new HashMap();
        String str = "";
        DebugLog.log("<Mod-Topper> Start working hardly");
        ArrayList<Faction> serverFactions = Faction.getFactions();
        HashMap<String, Integer> factionsList = new HashMap();
        Iterator var9 = serverFactions.iterator();

        while(var9.hasNext()) {
            Faction set = (Faction)var9.next();
            factionsList.put("Фракция: " + set.getName() + ". Лидер: " + set.getOwner() + ". Численность: ", set.getPlayers().size() + 1);
        }

        for(int i = 0; i < playersListDBResult.size(); ++i) {
            HashMap playerMap = ((DBResult)playersListDBResult.get(i)).getValues();
            IsoPlayer player = GameServer.getPlayerByUserNameForCommand(String.valueOf(playerMap.get("username")));

            try {
                String var10000 = player.getUsername();
                DebugLog.log("<Mod-Topper> Test For player: " + var10000 + " survivor kills: " + player.getDragCharacter().getSurvivorKills());
            } catch (Exception var20) {
            }

            try {
                timePlayersList.put(player.getUsername(), player.getHoursSurvived());
                killersZombiesList.put(player.getUsername(), player.getZombieKills());
                killersSurvivorList.put(player.getUsername(), player.getDragCharacter().getSurvivorKills());
            } catch (Exception var19) {
            }
        }

        HashMap<String, Double> stimePlayersList = timePlayersList.entrySet().stream().sorted(Comparator.comparingDouble(e -> -e.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> { throw new AssertionError(); },
                        LinkedHashMap::new
                ));

        HashMap<String, Integer> skillersZombiesList = killersZombiesList.entrySet().stream().sorted(Comparator.comparingDouble(e -> -e.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> { throw new AssertionError(); },
                        LinkedHashMap::new
                ));

        HashMap<String, Integer> sFactionList = factionsList.entrySet().stream().sorted(Comparator.comparingDouble(e -> -e.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> { throw new AssertionError(); },
                        LinkedHashMap::new
                ));

        HashMap<String, Integer> skillersSurvivorList = killersSurvivorList.entrySet().stream().sorted(Comparator.comparingDouble(e -> -e.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> { throw new AssertionError(); },
                        LinkedHashMap::new
                ));

        String pKillersStr = "Топ 10 долгожителей:<LINE>";
        int counter = 0;

        for(Iterator var15 = stimePlayersList.entrySet().iterator(); var15.hasNext(); ++counter) {
            Map.Entry<String, Double> set = (Map.Entry)var15.next();
            if (counter > 9) {
                break;
            }

            pKillersStr = pKillersStr + (counter + 1) + ". " + (String)set.getKey() + " прожил: " + Math.floor((Double)set.getValue() / 24.0) + " сут(-ок/-ки)<LINE>";
        }

        String zKillersStr = "Топ 10 убийц зомби:<LINE>";
        counter = 0;

        for(Iterator var26 = skillersZombiesList.entrySet().iterator(); var26.hasNext(); ++counter) {
            Map.Entry<String, Integer> set = (Map.Entry)var26.next();
            if (counter > 9) {
                break;
            }

            zKillersStr = zKillersStr + (counter + 1) + ". " + (String)set.getKey() + " унес жизней: " + set.getValue() + "<LINE>";
        }

        String factionStr = "Топ 5 фракций:<LINE>";
        counter = 0;

        for(Iterator var28 = sFactionList.entrySet().iterator(); var28.hasNext(); ++counter) {
            Map.Entry<String, Integer> set = (Map.Entry)var28.next();
            if (counter >= 5) {
                break;
            }

            factionStr = factionStr + (counter + 1) + ". " + (String)set.getKey() + set.getValue() + " человек<LINE>";
        }

        str = pKillersStr + "<LINE>" + zKillersStr + "<LINE>" + factionStr;
        return str;
    }
}
