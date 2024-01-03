package zombie.network;

import zombie.GameTime;
import zombie.Lua.LuaEventManager;
import zombie.MapCollisionData;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.logger.LoggerManager;
import zombie.core.logger.ZLogger;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.objects.IsoFire;
import zombie.iso.objects.IsoGenerator;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.vehicles.PolygonalMap2;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class GameServerHelper extends GameServer {
    private static ZLogger customZLogger;

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

            if (!canModifyPlayerStats(var1, var3)) {
                PacketTypes.PacketType.SyncXP.onUnauthorized(var1);
            } else {
                if (!var3.isDead()) {
                    try {
                        var3.getXp().load(var0, 195);
                    } catch (IOException var9) {
                        var9.printStackTrace();
                    }


                    for (int var4 = 0; var4 < udpEngine.connections.size(); ++var4) {
                        UdpConnection var5 = udpEngine.connections.get(var4);
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
            int oldPerkLevel;
            int newPerkLevel;
            float oldXP;
            float newXP;

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
                        DebugLog.log("<AntiPerk>: Player " + var1.username + " leveled up 1 and " + (needXPForLevelUp - oldXP + newXP) + " experience. To level up to full level you need" + needXPForLevelUp);
                    }
                }

            }
        }
    }

    public static boolean canModifyPlayerStats(UdpConnection var0, IsoPlayer var1) {
        return (var0 != null && var0.accessLevel != 56 && (var0.accessLevel & 56) != 0) || (var0 != null && var0.havePlayer(var1));
    }

    public static void readIdListFromFileExecute() {
        idList = new ArrayList<>();
        Path directoryPath = Paths.get("java\\zombie\\network\\ID_Options");
        Path filePath = directoryPath.resolve("item_ids.txt");

        // Проверяем существование директории
        if (!Files.exists(directoryPath)) {
            try {
                // Создаем директорию, если ее нет
                Files.createDirectories(directoryPath);
            } catch (IOException e) {
                // Обработка ошибки создания директории
                e.printStackTrace();
                return;
            }
        }

        // Проверяем существование файла
        if (!Files.exists(filePath)) {
            try {
                // Создаем файл, если его нет
                Files.createFile(filePath);
            } catch (FileAlreadyExistsException e) {
                // Файл уже существует, ничего не делаем
            } catch (IOException e) {
                // Обработка ошибки создания файла
                e.printStackTrace();
                return;
            }
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                idList.add(line.trim());
            }
        } catch (IOException e) {
            DebugLog.log("Error reading idList from file");
        }
        System.out.println("IdList contents: " + idList);
    }

    public static void readIdListFromFile() {
        GameServerHelper.readIdListFromFileExecute();
    }


    public static void initCustomLogger() {
        customZLogger = LoggerManager.getLogger("CustomLog");
    }

    public static void log(String message) {
        if (customZLogger == null) {
            initCustomLogger();
        }

        if (customZLogger != null) {
            customZLogger.write(message);
        } else {
            System.err.println("Custom logger is not initialized.");
        }
    }

    public static void hasBomb(ItemContainer playerInventory, UdpConnection connection) {
        for (InventoryItem item : playerInventory.getItems()) {
            String itemType = item.getType();
            if (itemType.toLowerCase().contains("aerosolbomb") || itemType.toLowerCase().contains("flametrap") || itemType.toLowerCase().contains("pipebomb")) {
                DebugLog.log("PipeBomb found " + itemType + " " + connection.username + " " + connection.ip);

            }
        }
    }

    public static void receiveAddItemToMapExecute(ByteBuffer byteBuffer, UdpConnection connection, short var2) throws Exception {
        String var1 = "Cheater";
        IsoObject var3 = WorldItemTypes.createFromBuffer(byteBuffer);
        if (var3 instanceof IsoFire && ServerOptions.instance.NoFire.getValue()) {
            DebugLog.log("user \"" + connection.username + "\" tried to start a fire");
        } else {
            var3.loadFromRemoteBuffer(byteBuffer);
            if (var3.square != null) {
                DebugLog.log(DebugType.Objects, "object: added " + var3 + " index=" + var3.getObjectIndex() + " " + var3.getX() + "," + var3.getY() + "," + var3.getZ());
                ZLogger var10000;
                String var10001;
                if (var3 instanceof IsoWorldInventoryObject) {
                    var10000 = LoggerManager.getLogger("item");
                    var10001 = connection.idStr;
                    var10000.write(var10001 + " \"" + connection.username + "\" floor +1 " + (int) var3.getX() + "," + (int) var3.getY() + "," + (int) var3.getZ() + " [" + ((IsoWorldInventoryObject) var3).getItem().getFullType() + "]");
                } else {
                    String var4 = var3.getName() != null ? var3.getName() : var3.getObjectName();
                    if (var3.getSprite() != null && var3.getSprite().getName() != null) {
                        var4 = var4 + " (" + var3.getSprite().getName() + ")";
                    }
                    var10000 = LoggerManager.getLogger("map");
                    var10001 = connection.idStr;
                    var10000.write(var10001 + " \"" + connection.username + "\" added " + var4 + " at " + var3.getX() + "," + var3.getY() + "," + var3.getZ());
                }
                //Проверяем не хранится ли в нашем списке устанавливаемый игроком объект, если нет, разрешаем установку
                List<String> idList = getIdList();
                if (var3.getSprite() != null && idList.contains(var3.getSprite().getName())) {
                    ServerWorldDatabase.instance.banIp(connection.ip, connection.username, var1, true); //Баним по IP
                    ServerWorldDatabase.instance.banUser(connection.username, true); //Баним по Username
                    GameServer.kick(connection, "UI_Policy_Ban", var1);
                    GameServerHelper.log("<BUILD> " + connection.ip + " player want to build WATER");
                } else {
                    var3.addToWorld();
                    var3.square.RecalcProperties();
                    if (!(var3 instanceof IsoWorldInventoryObject)) {
                        var3.square.restackSheetRope();
                        IsoWorld.instance.CurrentCell.checkHaveRoof(var3.square.getX(), var3.square.getY());
                        MapCollisionData.instance.squareChanged(var3.square);
                        PolygonalMap2.instance.squareChanged(var3.square);
                        ServerMap.instance.physicsCheck(var3.square.x, var3.square.y);
                        IsoRegions.squareChanged(var3.square);
                        IsoGenerator.updateGenerator(var3.square);
                    }

                    for (int var7 = 0; var7 < udpEngine.connections.size(); ++var7) {
                        UdpConnection var5 = udpEngine.connections.get(var7);
                        if (var5.getConnectedGUID() != connection.getConnectedGUID() && var5.RelevantTo((float) var3.square.x, (float) var3.square.y)) {
                            ByteBufferWriter var6 = var5.startPacket();
                            PacketTypes.PacketType.AddItemToMap.doPacket(var6);
                            var3.writeToRemoteBuffer(var6);
                            PacketTypes.PacketType.AddItemToMap.send(var5);
                        }
                    }

                    //Включать на случай тестов, если что-то пойдет не так

                    /*
                    idList.forEach(id -> {
                        DebugLog.log("ID in arrayList: " + id);
                        DebugLog.log("Tile ID: " + var3.getSprite().getName());
                        DebugLog.log("Is equals: " + id.equals(var3.getSprite().getName()));
                        DebugLog.log("Is contains: " + id.contains(var3.getSprite().getName()));
                    });*/


                    if (!(var3 instanceof IsoWorldInventoryObject)) {
                        LuaEventManager.triggerEvent("OnObjectAdded", var3);
                    } else {
                        ((IsoWorldInventoryObject) var3).dropTime = GameTime.getInstance().getWorldAgeHours();
                    }
                }
            }
        }
    }
}

