package zombie.network;

import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;


public class GameServerHelper extends GameServer {
    static void onReceiveSyncXP(ByteBuffer var0, UdpConnection var1, short var2) {
        IsoPlayer var3 = (IsoPlayer) IDToPlayerMap.get(var0.getShort());
        float needXPForLevelUp = 0.0f;

        if (var3 != null) {
            /*-- Записываем старые данные XP и уровней --*/
            ArrayList<IsoGameCharacter.PerkInfo> perkArrayOld = var3.getPerkList();
            ArrayList<Float> xpArrayOld = new ArrayList<>();
            for (int l = 0; l < perkArrayOld.size(); l++) {
                xpArrayOld.add(l, var3.getXp().getXP(perkArrayOld.get(l).perk));
            }

            if (var1 != null && var3 != null && !canModifyPlayerStats(var1, var3)) {
                PacketTypes.PacketType.SyncXP.onUnauthorized(var1);
            } else {
                if (var3 != null && !var3.isDead()) {
                    try {
                        var3.getXp().load(var0, 195);
                    } catch (IOException var9) {
                        var9.printStackTrace();
                    }


                    for (int var4 = 0; var4 < udpEngine.connections.size(); ++var4) {
                        UdpConnection var5 = (UdpConnection) udpEngine.connections.get(var4);
                        if (var5.getConnectedGUID() != var1.getConnectedGUID()) {
                            ByteBufferWriter var6 = var5.startPacket();
                            PacketTypes.PacketType.SyncXP.doPacket(var6);
                            var6.putShort(var3.getOnlineID());


                            try {
                                var3.getXp().save(var6.bb);
                            } catch (IOException var8) {
                                var8.printStackTrace();
                            }

                            PacketTypes.PacketType.SyncXP.send(var5);
                        }
                    }
                }

            }

            ArrayList<IsoGameCharacter.PerkInfo> perkArrayNew = var3.getPerkList();
            ArrayList<Float> xpArrayNew = new ArrayList<>();

            for (int k = 0; k < perkArrayNew.size(); k++) {
                xpArrayNew.add(k, var3.getXp().getXP(perkArrayNew.get(k).perk));
            }
            int oldPerkLevel = 0;
            int newPerkLevel = 0;
            float oldXP = 0.0f;
            float newXP = 0.0f;

            for (int n = 0; n < perkArrayNew.size(); n++) {
                switch (perkArrayOld.get(n).getLevel()) {
                    case 1:
                        needXPForLevelUp = 75.0F;
                        break;
                    case 2:
                        needXPForLevelUp = 150.0F;
                        break;
                    case 3:
                        needXPForLevelUp = 300.0F;
                        break;
                    case 4:
                        needXPForLevelUp = 750.0F;
                        break;
                    case 5:
                        needXPForLevelUp = 1500.0F;
                        break;
                    case 6:
                        needXPForLevelUp = 3000.0F;
                        break;
                    case 7:
                        needXPForLevelUp = 6000.0F;
                        break;
                    case 8:
                        needXPForLevelUp = 12000.0F;
                        break;
                    case 9:
                        needXPForLevelUp = 24000.0F;
                        break;
                    case 10:
                        needXPForLevelUp = 48000.0F;
                        break;
                }

                oldPerkLevel = perkArrayOld.get(n).getLevel();
                newPerkLevel = perkArrayNew.get(n).getLevel();

                oldXP = xpArrayOld.get(n);
                newXP = xpArrayNew.get(n);

                /*- Проверка на понижение УРОВНЕЙ навыков-*/
                /*TODO: сделать доп проверку на то сколько прожил игрок, иначе
                 * когда игрок умирает - игра может засчитать будто он понизил навыки  */
                if (oldPerkLevel > newPerkLevel && var1.accessLevel != 32) {
                    DebugLog.log("<AntiPerk>: Player " + var1.username + " lowered skills !");
                    DebugLog.log("<AntiPerk>: Player " + var1.username + " lowered skills !");
                } else {

                /*-- Проверка на то, что игрок не накрутил себе просто так уровень
                  -- проверяет, не равны ли XP прошлого уровня и старого--*/
                    if ((oldXP == newXP | newXP == 0.0f) & newPerkLevel != 0 & newPerkLevel == oldPerkLevel + 1 && var1.accessLevel != 32) {
                        DebugLog.log("<AntiPerk>: Player " + var1.username + " probably did leveling without experience!!");
                    }

                    /*-- Проверка на то, мог ли игрок действительно заработать ТАК много опыта --*/
                    /*- если игрок прокачал просто XP, без нового уровня */
                    /*и заработал за раз более 60% необходимого, то бьем тревогу-*/
                    if ((oldPerkLevel == newPerkLevel) & (newXP - oldXP > needXPForLevelUp * 0.6f) && var1.accessLevel != 32) {
                        DebugLog.log("<AntiPerk>: Player " + var1.username + " I gained too much experience!");
                    }
                    /*-- Проверка на то, мог ли игрок действительно заработать ТАК много опыта --*/
                    /*- если игрок прокачал XP и Level -*/
                    if ((oldPerkLevel + 1 == newPerkLevel) & (needXPForLevelUp - oldXP + newXP > newPerkLevel) && var1.accessLevel != 32) {
                        DebugLog.log("<AntiPerk>: Player " + var1.username
                                + " leveled up 1 and " + (needXPForLevelUp - oldXP + newXP)
                                + " experience. To level up to full level you need" + needXPForLevelUp);
                        DebugLog.log("<AntiPerk>: Player " + var1.username
                                + " leveled up 1 and " + (needXPForLevelUp - oldXP + newXP)
                                + " experience. To level up to full level you need" + needXPForLevelUp);
                    }
                }
            }
        }
    }

    public static boolean canModifyPlayerStats(UdpConnection var0, IsoPlayer var1) {
        return (var0 != null && var0.accessLevel != 56 && (var0.accessLevel & 56) != 0) || (var0 != null && var0.havePlayer(var1));
    }
}

