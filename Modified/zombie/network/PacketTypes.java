package zombie.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import se.krka.kahlua.vm.KahluaTable;
import zombie.SystemDisabler;
import zombie.Lua.LuaManager;
import zombie.commands.PlayerType;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;

public class PacketTypes {
    public static final short ContainerDeadBody = 0;
    public static final short ContainerWorldObject = 1;
    public static final short ContainerObject = 2;
    public static final short ContainerVehicle = 3;
    public static final Map packetTypes = new TreeMap();
    public static final KahluaTable packetCountTable;

    public static void doPingPacket(ByteBufferWriter var0) {
        var0.putInt(28);
    }

    public static KahluaTable getPacketCounts(int var0) {
        packetCountTable.wipe();
        if (GameClient.bClient) {
            Iterator var1 = packetTypes.values().iterator();

            while(var1.hasNext()) {
                PacketTypes.PacketType var2 = (PacketTypes.PacketType)var1.next();
                if (var0 == 1) {
                    packetCountTable.rawset(String.format("%03d-%s", var2.id, var2.name()), String.valueOf(var2.serverPacketCount));
                } else {
                    packetCountTable.rawset(String.format("%03d-%s", var2.id, var2.name()), String.valueOf(var2.clientPacketCount));
                }
            }
        }

        return packetCountTable;
    }

    static {
        packetCountTable = LuaManager.platform.newTable();
        PacketTypes.PacketType[] var0 = PacketTypes.PacketType.values();
        int var1 = var0.length;

        for(int var2 = 0; var2 < var1; ++var2) {
            PacketTypes.PacketType var3 = var0[var2];
            PacketTypes.PacketType var4 = (PacketTypes.PacketType)packetTypes.put(var3.getId(), var3);
            if (var4 != null) {
                DebugLog.Multiplayer.error(String.format("PacketType: duplicate \"%s\" \"%s\" id=%d", var4.name(), var3.name(), var3.getId()));
            }
        }

    }

    public static enum PacketType {
        Validate(1, 0, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveValidatePacket, GameClient::receiveValidatePacket, GameClient::receiveValidatePacket),
        Login(2, 1, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveLogin, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        HumanVisual(3, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveHumanVisual, GameClient::receiveHumanVisual, (PacketTypes.CallbackClientProcess)null),
        KeepAlive(4, 1, 0, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveKeepAlive, GameClient::receiveKeepAlive, GameClient::skipPacket),
        Vehicles(5, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveVehicles, GameClient::receiveVehicles, (PacketTypes.CallbackClientProcess)null),
        PlayerConnect(6, 1, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePlayerConnect, GameClient::receivePlayerConnect, (PacketTypes.CallbackClientProcess)null),
        VehiclesUnreliable(7, 2, 0, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveVehicles, GameClient::receiveVehicles, (PacketTypes.CallbackClientProcess)null),
        VehicleAuthorization(8, 2, 3, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveVehicleAuthorization, (PacketTypes.CallbackClientProcess)null),
        MetaGrid(9, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveMetaGrid, (PacketTypes.CallbackClientProcess)null),
        Helicopter(11, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveHelicopter, (PacketTypes.CallbackClientProcess)null),
        SyncIsoObject(12, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncIsoObject, GameClient::receiveSyncIsoObject, (PacketTypes.CallbackClientProcess)null),
        PlayerTimeout(13, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receivePlayerTimeout, GameClient::receivePlayerTimeout),
        ServerMap(15, 1, 3, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveServerMap, GameClient::receiveServerMapLoading),
        PassengerMap(16, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePassengerMap, GameClient::receivePassengerMap, (PacketTypes.CallbackClientProcess)null),
        AddItemToMap(17, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveAddItemToMap, GameClient::receiveAddItemToMap, (PacketTypes.CallbackClientProcess)null),
        SentChunk(18, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        SyncClock(19, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveSyncClock, (PacketTypes.CallbackClientProcess)null),
        AddInventoryItemToContainer(20, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveAddInventoryItemToContainer, GameClient::receiveAddInventoryItemToContainer, (PacketTypes.CallbackClientProcess)null),
        RemoveInventoryItemFromContainer(22, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRemoveInventoryItemFromContainer, GameClient::receiveRemoveInventoryItemFromContainer, (PacketTypes.CallbackClientProcess)null),
        RemoveItemFromSquare(23, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRemoveItemFromSquare, GameClient::receiveRemoveItemFromSquare, (PacketTypes.CallbackClientProcess)null),
        RequestLargeAreaZip(24, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRequestLargeAreaZip, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        Equip(25, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveEquip, GameClient::receiveEquip, (PacketTypes.CallbackClientProcess)null),
        HitCharacter(26, 0, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveHitCharacter, GameClient::receiveHitCharacter, (PacketTypes.CallbackClientProcess)null),
        AddCoopPlayer(27, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveAddCoopPlayer, GameClient::receiveAddCoopPlayer, (PacketTypes.CallbackClientProcess)null),
        WeaponHit(28, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveWeaponHit, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        /** @deprecated */
        @Deprecated
        KillZombie(30, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        SandboxOptions(31, 1, 2, 32, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSandboxOptions, GameClient::receiveSandboxOptions, (PacketTypes.CallbackClientProcess)null),
        SmashWindow(32, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSmashWindow, GameClient::receiveSmashWindow, (PacketTypes.CallbackClientProcess)null),
        PlayerDeath(33, 0, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePlayerDeath, GameClient::receivePlayerDeath, (PacketTypes.CallbackClientProcess)null),
        RequestZipList(34, 0, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRequestZipList, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        ItemStats(35, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveItemStats, GameClient::receiveItemStats, (PacketTypes.CallbackClientProcess)null),
        NotRequiredInZip(36, 0, 0, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveNotRequiredInZip, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        RequestData(37, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRequestData, GameClient::receiveRequestData, GameClient::receiveRequestData),
        GlobalObjects(38, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveGlobalObjects, GameClient::receiveGlobalObjects, (PacketTypes.CallbackClientProcess)null),
        ZombieDeath(39, 1, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveZombieDeath, GameClient::receiveZombieDeath, (PacketTypes.CallbackClientProcess)null),
        AccessDenied(40, 0, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, (PacketTypes.CallbackClientProcess)null, GameClient::receiveAccessDenied),
        PlayerDamage(41, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePlayerDamage, GameClient::receivePlayerDamage, (PacketTypes.CallbackClientProcess)null),
        Bandage(42, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveBandage, GameClient::receiveBandage, (PacketTypes.CallbackClientProcess)null),
        EatFood(43, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveEatFood, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        RequestItemsForContainer(44, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRequestItemsForContainer, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        Drink(45, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveDrink, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        SyncAlarmClock(46, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncAlarmClock, GameClient::receiveSyncAlarmClock, (PacketTypes.CallbackClientProcess)null),
        PacketCounts(47, 1, 2, 62, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePacketCounts, GameClient::receivePacketCounts, (PacketTypes.CallbackClientProcess)null),
        SendModData(48, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSendModData, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        RemoveContestedItemsFromInventory(49, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveRemoveContestedItemsFromInventory, (PacketTypes.CallbackClientProcess)null),
        ScoreboardUpdate(50, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveScoreboardUpdate, GameClient::receiveScoreboardUpdate, (PacketTypes.CallbackClientProcess)null),
        ReceiveModData(51, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveReceiveModData, (PacketTypes.CallbackClientProcess)null),
        ServerQuit(52, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveServerQuit, (PacketTypes.CallbackClientProcess)null),
        PlaySound(53, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePlaySound, GameClient::receivePlaySound, (PacketTypes.CallbackClientProcess)null),
        WorldSound(54, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveWorldSound, GameClient::receiveWorldSound, (PacketTypes.CallbackClientProcess)null),
        AddAmbient(55, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveAddAmbient, (PacketTypes.CallbackClientProcess)null),
        SyncClothing(56, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncClothing, GameClient::receiveSyncClothing, (PacketTypes.CallbackClientProcess)null),
        ClientCommand(57, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveClientCommand, GameClient::receiveClientCommand, (PacketTypes.CallbackClientProcess)null),
        ObjectModData(58, 2, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveObjectModData, GameClient::receiveObjectModData, (PacketTypes.CallbackClientProcess)null),
        ObjectChange(59, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveObjectChange, (PacketTypes.CallbackClientProcess)null),
        BloodSplatter(60, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveBloodSplatter, (PacketTypes.CallbackClientProcess)null),
        ZombieSound(61, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveZombieSound, (PacketTypes.CallbackClientProcess)null),
        ZombieDescriptors(62, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveZombieDescriptors, (PacketTypes.CallbackClientProcess)null),
        SlowFactor(63, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveSlowFactor, (PacketTypes.CallbackClientProcess)null),
        Weather(64, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveWeather, (PacketTypes.CallbackClientProcess)null),
        WorldMapPlayerPosition(65, 3, 1, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveWorldMapPlayerPosition, GameClient::receiveWorldMapPlayerPosition, (PacketTypes.CallbackClientProcess)null),
        /** @deprecated */
        @Deprecated
        RequestPlayerData(67, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRequestPlayerData, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        RemoveCorpseFromMap(68, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRemoveCorpseFromMap, GameClient::receiveRemoveCorpseFromMap, (PacketTypes.CallbackClientProcess)null),
        AddCorpseToMap(69, 1, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveAddCorpseToMap, GameClient::receiveAddCorpseToMap, (PacketTypes.CallbackClientProcess)null),
        BecomeCorpse(70, 1, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveBecomeCorpse, (PacketTypes.CallbackClientProcess)null),
        StartFire(75, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveStartFire, GameClient::receiveStartFire, (PacketTypes.CallbackClientProcess)null),
        UpdateItemSprite(76, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveUpdateItemSprite, GameClient::receiveUpdateItemSprite, (PacketTypes.CallbackClientProcess)null),
        StartRain(77, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveStartRain, (PacketTypes.CallbackClientProcess)null),
        StopRain(78, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveStopRain, (PacketTypes.CallbackClientProcess)null),
        WorldMessage(79, 1, 2, 56, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveWorldMessage, GameClient::receiveWorldMessage, (PacketTypes.CallbackClientProcess)null),
        getModData(80, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveGetModData, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        ReceiveCommand(81, 2, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveReceiveCommand, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        ReloadOptions(82, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveReloadOptions, (PacketTypes.CallbackClientProcess)null),
        Kicked(83, 0, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveKicked, GameClient::receiveKicked),
        ExtraInfo(84, 1, 2, 62, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveExtraInfo, GameClient::receiveExtraInfo, (PacketTypes.CallbackClientProcess)null),
        AddItemInInventory(85, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveAddItemInInventory, (PacketTypes.CallbackClientProcess)null),
        ChangeSafety(86, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveChangeSafety, GameClient::receiveChangeSafety, (PacketTypes.CallbackClientProcess)null),
        Ping(87, 0, 0, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePing, GameClient::receivePing, GameClient::receivePing),
        /** @deprecated */
        @Deprecated
        WriteLog(88, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveWriteLog, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        AddXP(89, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveAddXp, GameClient::receiveAddXp, (PacketTypes.CallbackClientProcess)null),
        UpdateOverlaySprite(90, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveUpdateOverlaySprite, GameClient::receiveUpdateOverlaySprite, (PacketTypes.CallbackClientProcess)null),
        Checksum(91, 1, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveChecksum, GameClient::receiveChecksum, GameClient::receiveChecksumLoading),
        ConstructedZone(92, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveConstructedZone, GameClient::receiveConstructedZone, (PacketTypes.CallbackClientProcess)null),
        RegisterZone(94, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRegisterZone, GameClient::receiveRegisterZone, (PacketTypes.CallbackClientProcess)null),
        /** @deprecated */
        @Deprecated
        WoundInfection(97, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveWoundInfection, GameClient::receiveWoundInfection, (PacketTypes.CallbackClientProcess)null),
        Stitch(98, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveStitch, GameClient::receiveStitch, (PacketTypes.CallbackClientProcess)null),
        Disinfect(99, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveDisinfect, GameClient::receiveDisinfect, (PacketTypes.CallbackClientProcess)null),
        /** @deprecated */
        @Deprecated
        AdditionalPain(100, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveAdditionalPain, (PacketTypes.CallbackClientProcess)null),
        RemoveGlass(101, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRemoveGlass, GameClient::receiveRemoveGlass, (PacketTypes.CallbackClientProcess)null),
        Splint(102, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSplint, GameClient::receiveSplint, (PacketTypes.CallbackClientProcess)null),
        RemoveBullet(103, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRemoveBullet, GameClient::receiveRemoveBullet, (PacketTypes.CallbackClientProcess)null),
        CleanBurn(104, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveCleanBurn, GameClient::receiveCleanBurn, (PacketTypes.CallbackClientProcess)null),
        SyncThumpable(105, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncThumpable, GameClient::receiveSyncThumpable, (PacketTypes.CallbackClientProcess)null),
        SyncDoorKey(106, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncDoorKey, GameClient::receiveSyncDoorKey, (PacketTypes.CallbackClientProcess)null),
        /** @deprecated */
        @Deprecated
        AddXpCommand(107, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveAddXpCommand, (PacketTypes.CallbackClientProcess)null),
        Teleport(108, 1, 2, 62, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveTeleport, GameClient::receiveTeleport, (PacketTypes.CallbackClientProcess)null),
        RemoveBlood(109, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRemoveBlood, GameClient::receiveRemoveBlood, (PacketTypes.CallbackClientProcess)null),
        AddExplosiveTrap(110, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveAddExplosiveTrap, GameClient::receiveAddExplosiveTrap, (PacketTypes.CallbackClientProcess)null),
        BodyDamageUpdate(112, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveBodyDamageUpdate, GameClient::receiveBodyDamageUpdate, (PacketTypes.CallbackClientProcess)null),
        SyncSafehouse(114, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncSafehouse, GameClient::receiveSyncSafehouse, (PacketTypes.CallbackClientProcess)null),
        SledgehammerDestroy(115, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSledgehammerDestroy, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        StopFire(116, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveStopFire, GameClient::receiveStopFire, (PacketTypes.CallbackClientProcess)null),
        Cataplasm(117, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveCataplasm, GameClient::receiveCataplasm, (PacketTypes.CallbackClientProcess)null),
        AddAlarm(118, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveAddAlarm, (PacketTypes.CallbackClientProcess)null),
        PlaySoundEveryPlayer(119, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receivePlaySoundEveryPlayer, (PacketTypes.CallbackClientProcess)null),
        SyncFurnace(120, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncFurnace, GameClient::receiveSyncFurnace, (PacketTypes.CallbackClientProcess)null),
        SendCustomColor(121, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSendCustomColor, GameClient::receiveSendCustomColor, (PacketTypes.CallbackClientProcess)null),
        SyncCompost(122, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncCompost, GameClient::receiveSyncCompost, (PacketTypes.CallbackClientProcess)null),
        ChangePlayerStats(123, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveChangePlayerStats, GameClient::receiveChangePlayerStats, (PacketTypes.CallbackClientProcess)null),
        /** @deprecated */
        @Deprecated
        AddXpFromPlayerStatsUI(124, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveAddXp, GameClient::receiveAddXp, (PacketTypes.CallbackClientProcess)null),
        SyncXP(126, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncXP, GameClient::receiveSyncXP, (PacketTypes.CallbackClientProcess)null),
        /** @deprecated */
        @Deprecated
        PacketTypeShort(127, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        Userlog(128, 1, 2, 62, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveUserlog, GameClient::receiveUserlog, (PacketTypes.CallbackClientProcess)null),
        AddUserlog(129, 1, 2, 62, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveAddUserlog, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        RemoveUserlog(130, 1, 2, 56, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRemoveUserlog, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        AddWarningPoint(131, 1, 2, 56, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveAddWarningPoint, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        MessageForAdmin(132, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveMessageForAdmin, (PacketTypes.CallbackClientProcess)null),
        WakeUpPlayer(133, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveWakeUpPlayer, GameClient::receiveWakeUpPlayer, (PacketTypes.CallbackClientProcess)null),
        /** @deprecated */
        @Deprecated
        SendTransactionID(134, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        GetDBSchema(135, 1, 2, 60, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveGetDBSchema, GameClient::receiveGetDBSchema, (PacketTypes.CallbackClientProcess)null),
        GetTableResult(136, 1, 2, 60, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveGetTableResult, GameClient::receiveGetTableResult, (PacketTypes.CallbackClientProcess)null),
        ExecuteQuery(137, 1, 2, 32, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveExecuteQuery, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        ChangeTextColor(138, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveChangeTextColor, GameClient::receiveChangeTextColor, (PacketTypes.CallbackClientProcess)null),
        SyncNonPvpZone(139, 1, 2, 32, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncNonPvpZone, GameClient::receiveSyncNonPvpZone, (PacketTypes.CallbackClientProcess)null),
        SyncFaction(140, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncFaction, GameClient::receiveSyncFaction, (PacketTypes.CallbackClientProcess)null),
        SendFactionInvite(141, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSendFactionInvite, GameClient::receiveSendFactionInvite, (PacketTypes.CallbackClientProcess)null),
        AcceptedFactionInvite(142, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveAcceptedFactionInvite, GameClient::receiveAcceptedFactionInvite, (PacketTypes.CallbackClientProcess)null),
        AddTicket(143, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveAddTicket, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        ViewTickets(144, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveViewTickets, GameClient::receiveViewTickets, (PacketTypes.CallbackClientProcess)null),
        RemoveTicket(145, 1, 2, 62, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRemoveTicket, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        RequestTrading(146, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRequestTrading, GameClient::receiveRequestTrading, (PacketTypes.CallbackClientProcess)null),
        TradingUIAddItem(147, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveTradingUIAddItem, GameClient::receiveTradingUIAddItem, (PacketTypes.CallbackClientProcess)null),
        TradingUIRemoveItem(148, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveTradingUIRemoveItem, GameClient::receiveTradingUIRemoveItem, (PacketTypes.CallbackClientProcess)null),
        TradingUIUpdateState(149, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveTradingUIUpdateState, GameClient::receiveTradingUIUpdateState, (PacketTypes.CallbackClientProcess)null),
        /** @deprecated */
        @Deprecated
        SendItemListNet(150, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSendItemListNet, GameClient::receiveSendItemListNet, (PacketTypes.CallbackClientProcess)null),
        ChunkObjectState(151, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveChunkObjectState, GameClient::receiveChunkObjectState, (PacketTypes.CallbackClientProcess)null),
        ReadAnnotedMap(152, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveReadAnnotedMap, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        RequestInventory(153, 1, 2, 56, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRequestInventory, GameClient::receiveRequestInventory, (PacketTypes.CallbackClientProcess)null),
        SendInventory(154, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSendInventory, GameClient::receiveSendInventory, (PacketTypes.CallbackClientProcess)null),
        InvMngReqItem(155, 1, 2, 56, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveInvMngReqItem, GameClient::receiveInvMngReqItem, (PacketTypes.CallbackClientProcess)null),
        InvMngGetItem(156, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveInvMngGetItem, GameClient::receiveInvMngGetItem, (PacketTypes.CallbackClientProcess)null),
        InvMngRemoveItem(157, 1, 2, 56, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveInvMngRemoveItem, GameClient::receiveInvMngRemoveItem, (PacketTypes.CallbackClientProcess)null),
        StartPause(158, 1, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveStartPause, (PacketTypes.CallbackClientProcess)null),
        StopPause(159, 1, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveStopPause, (PacketTypes.CallbackClientProcess)null),
        TimeSync(160, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveTimeSync, GameClient::receiveTimeSync, (PacketTypes.CallbackClientProcess)null),
        /** @deprecated */
        @Deprecated
        SyncIsoObjectReq(161, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncIsoObjectReq, GameClient::receiveSyncIsoObjectReq, (PacketTypes.CallbackClientProcess)null),
        /** @deprecated */
        @Deprecated
        PlayerSave(162, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePlayerSave, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        /** @deprecated */
        @Deprecated
        SyncWorldObjectsReq(163, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveSyncWorldObjectsReq, (PacketTypes.CallbackClientProcess)null),
        /** @deprecated */
        @Deprecated
        SyncObjects(164, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncObjects, GameClient::receiveSyncObjects, (PacketTypes.CallbackClientProcess)null),
        SendPlayerProfile(166, 1, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSendPlayerProfile, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        LoadPlayerProfile(167, 1, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveLoadPlayerProfile, GameClient::receiveLoadPlayerProfile, (PacketTypes.CallbackClientProcess)null),
        SpawnRegion(171, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveSpawnRegion, GameClient::receiveSpawnRegion),
        PlayerDamageFromCarCrash(172, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receivePlayerDamageFromCarCrash, (PacketTypes.CallbackClientProcess)null),
        PlayerAttachedItem(173, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePlayerAttachedItem, GameClient::receivePlayerAttachedItem, (PacketTypes.CallbackClientProcess)null),
        ZombieHelmetFalling(174, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveZombieHelmetFalling, GameClient::receiveZombieHelmetFalling, (PacketTypes.CallbackClientProcess)null),
        AddBrokenGlass(175, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveAddBrokenGlass, (PacketTypes.CallbackClientProcess)null),
        SyncPerks(177, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncPerks, GameClient::receiveSyncPerks, (PacketTypes.CallbackClientProcess)null),
        SyncWeight(178, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncWeight, GameClient::receiveSyncWeight, (PacketTypes.CallbackClientProcess)null),
        SyncInjuries(179, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncInjuries, GameClient::receiveSyncInjuries, (PacketTypes.CallbackClientProcess)null),
        SyncEquippedRadioFreq(181, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncEquippedRadioFreq, GameClient::receiveSyncEquippedRadioFreq, (PacketTypes.CallbackClientProcess)null),
        InitPlayerChat(182, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveInitPlayerChat, (PacketTypes.CallbackClientProcess)null),
        PlayerJoinChat(183, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receivePlayerJoinChat, (PacketTypes.CallbackClientProcess)null),
        PlayerLeaveChat(184, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receivePlayerLeaveChat, (PacketTypes.CallbackClientProcess)null),
        ChatMessageFromPlayer(185, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveChatMessageFromPlayer, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        ChatMessageToPlayer(186, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveChatMessageToPlayer, (PacketTypes.CallbackClientProcess)null),
        PlayerStartPMChat(187, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePlayerStartPMChat, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        AddChatTab(189, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveAddChatTab, (PacketTypes.CallbackClientProcess)null),
        RemoveChatTab(190, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveRemoveChatTab, (PacketTypes.CallbackClientProcess)null),
        PlayerConnectedToChat(191, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receivePlayerConnectedToChat, (PacketTypes.CallbackClientProcess)null),
        PlayerNotFound(192, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receivePlayerNotFound, (PacketTypes.CallbackClientProcess)null),
        SendSafehouseInvite(193, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSendSafehouseInvite, GameClient::receiveSendSafehouseInvite, (PacketTypes.CallbackClientProcess)null),
        AcceptedSafehouseInvite(194, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveAcceptedSafehouseInvite, GameClient::receiveAcceptedSafehouseInvite, (PacketTypes.CallbackClientProcess)null),
        ClimateManagerPacket(200, 1, 2, 62, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveClimateManagerPacket, GameClient::receiveClimateManagerPacket, (PacketTypes.CallbackClientProcess)null),
        IsoRegionServerPacket(201, 1, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveIsoRegionServerPacket, (PacketTypes.CallbackClientProcess)null),
        IsoRegionClientRequestFullUpdate(202, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveIsoRegionClientRequestFullUpdate, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        EventPacket(210, 0, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveEventPacket, GameClient::receiveEventPacket, (PacketTypes.CallbackClientProcess)null),
        Statistic(211, 1, 0, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveStatistic, GameClient::receiveStatistic, (PacketTypes.CallbackClientProcess)null),
        StatisticRequest(212, 1, 2, 32, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveStatisticRequest, GameClient::receiveStatisticRequest, (PacketTypes.CallbackClientProcess)null),
        PlayerUpdateReliable(213, 0, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePlayerUpdate, GameClient::receivePlayerUpdate, (PacketTypes.CallbackClientProcess)null),
        ActionPacket(214, 1, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveActionPacket, GameClient::receiveActionPacket, (PacketTypes.CallbackClientProcess)null),
        ZombieControl(215, 0, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveZombieControl, (PacketTypes.CallbackClientProcess)null),
        PlayWorldSound(216, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePlayWorldSound, GameClient::receivePlayWorldSound, (PacketTypes.CallbackClientProcess)null),
        StopSound(217, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveStopSound, GameClient::receiveStopSound, (PacketTypes.CallbackClientProcess)null),
        PlayerUpdate(218, 2, 0, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePlayerUpdate, GameClient::receivePlayerUpdate, (PacketTypes.CallbackClientProcess)null),
        ZombieSimulation(219, 2, 0, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveZombieSimulation, GameClient::receiveZombieSimulation, (PacketTypes.CallbackClientProcess)null),
        PingFromClient(220, 1, 0, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePingFromClient, GameClient::receivePingFromClient, (PacketTypes.CallbackClientProcess)null),
        ZombieSimulationReliable(221, 0, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveZombieSimulation, GameClient::receiveZombieSimulation, (PacketTypes.CallbackClientProcess)null),
        EatBody(222, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveEatBody, GameClient::receiveEatBody, (PacketTypes.CallbackClientProcess)null),
        Thump(223, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveThump, GameClient::receiveThump, (PacketTypes.CallbackClientProcess)null),
        SyncRadioData(224, 0, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncRadioData, GameClient::receiveSyncRadioData, (PacketTypes.CallbackClientProcess)null),
        LoginQueueRequest2(225, 0, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, LoginQueue::receiveServerLoginQueueRequest, (PacketTypes.CallbackClientProcess)null, LoginQueue::receiveClientLoginQueueRequest),
        LoginQueueDone2(226, 0, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, LoginQueue::receiveLoginQueueDone, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        ItemTransaction(227, 0, 3, 63, PacketTypes.PacketAuthorization.Policy.Kick, ItemTransactionManager::receiveOnServer, ItemTransactionManager::receiveOnClient, (PacketTypes.CallbackClientProcess)null),
        KickOutOfSafehouse(228, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveKickOutOfSafehouse, GameClient::receiveTeleport, (PacketTypes.CallbackClientProcess)null),
        SneezeCough(229, 3, 0, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSneezeCough, GameClient::receiveSneezeCough, (PacketTypes.CallbackClientProcess)null),
        BurnCorpse(230, 2, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveBurnCorpse, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        WaveSignal(300, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveWaveSignal, GameClient::receiveWaveSignal, (PacketTypes.CallbackClientProcess)null),
        PlayerListensChannel(301, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePlayerListensChannel, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        RadioServerData(302, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRadioServerData, GameClient::receiveRadioServerData, (PacketTypes.CallbackClientProcess)null),
        RadioDeviceDataState(303, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveRadioDeviceDataState, GameClient::receiveRadioDeviceDataState, (PacketTypes.CallbackClientProcess)null),
        SyncCustomLightSettings(304, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveSyncCustomLightSettings, GameClient::receiveSyncCustomLightSettings, (PacketTypes.CallbackClientProcess)null),
        ReplaceOnCooked(305, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveReplaceOnCooked, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        PlayerDataRequest(306, 1, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receivePlayerDataRequest, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        GlobalModData(32000, 0, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveGlobalModData, GameClient::receiveGlobalModData, (PacketTypes.CallbackClientProcess)null),
        GlobalModDataRequest(32001, 0, 2, 63, PacketTypes.PacketAuthorization.Policy.Kick, GameServer::receiveGlobalModDataRequest, (PacketTypes.CallbackClientProcess)null, (PacketTypes.CallbackClientProcess)null),
        RadioPostSilenceEvent(32002, 0, 2, 0, PacketTypes.PacketAuthorization.Policy.Kick, (PacketTypes.CallbackServerProcess)null, GameClient::receiveRadioPostSilence, (PacketTypes.CallbackClientProcess)null);

        private final short id;
        private final byte requiredAccessLevel;
        private final PacketTypes.PacketAuthorization.Policy unauthorizedPacketPolicy;
        public final int PacketPriority;
        public final int PacketReliability;
        public final byte OrderingChannel;
        private final PacketTypes.CallbackServerProcess serverProcess;
        private final PacketTypes.CallbackClientProcess mainLoopHandlePacketInternal;
        private final PacketTypes.CallbackClientProcess gameLoadingDealWithNetData;
        public int incomePackets;
        public int outcomePackets;
        public int incomeBytes;
        public int outcomeBytes;
        public long clientPacketCount;
        public long serverPacketCount;

        private PacketType(int var3, int var4, int var5, int var6, PacketTypes.PacketAuthorization.Policy var7, PacketTypes.CallbackServerProcess var8, PacketTypes.CallbackClientProcess var9, PacketTypes.CallbackClientProcess var10) {
            this(var3, var4, var5, (byte)0, (byte)var6, var7, var8, var9, var10);
        }

        private PacketType(int var3, int var4, int var5, byte var6, byte var7, PacketTypes.PacketAuthorization.Policy var8, PacketTypes.CallbackServerProcess var9, PacketTypes.CallbackClientProcess var10, PacketTypes.CallbackClientProcess var11) {
            this.id = (short)var3;
            this.requiredAccessLevel = var7;
            this.unauthorizedPacketPolicy = var8;
            this.PacketPriority = var4;
            this.PacketReliability = var5;
            this.OrderingChannel = var6;
            this.serverProcess = var9;
            this.mainLoopHandlePacketInternal = var10;
            this.gameLoadingDealWithNetData = var11;
            this.resetStatistics();
        }

        public void resetStatistics() {
            this.incomePackets = 0;
            this.outcomePackets = 0;
            this.incomeBytes = 0;
            this.outcomeBytes = 0;
            this.clientPacketCount = 0L;
            this.serverPacketCount = 0L;
        }

        public void send(UdpConnection var1) {
            var1.endPacket(this.PacketPriority, this.PacketReliability, this.OrderingChannel);
            DebugLog.Packet.noise("type=%s username=%s connection=%d ip=%s size=%d", this.name(), var1.username, var1.getConnectedGUID(), var1.ip, var1.getBufferPosition());
        }

        public void doPacket(ByteBufferWriter var1) {
            var1.putByte((byte)-122);
            var1.putShort(this.getId());
        }

        public short getId() {
            return this.id;
        }

        public void onServerPacket(ByteBuffer var1, UdpConnection var2) throws Exception {
            if (PacketTypes.PacketAuthorization.isAuthorized(var2, this)) {
                DebugLog.Packet.noise("type=%s username=%s connection=%d ip=%s", this.name(), var2.username, var2.getConnectedGUID(), var2.ip);
                this.serverProcess.call(var1, var2, this.getId());
            }

        }

        public void onMainLoopHandlePacketInternal(ByteBuffer var1) throws IOException {
            DebugLog.Packet.noise("type=%s", this.name());
            this.mainLoopHandlePacketInternal.call(var1, this.getId());
        }

        public boolean onGameLoadingDealWithNetData(ByteBuffer var1) {
            DebugLog.Packet.noise("type=%s", this.name());
            if (this.gameLoadingDealWithNetData == null) {
                DebugLog.Network.noise("Delay processing packet of type %s while loading game", this.name());
                return false;
            } else {
                try {
                    this.gameLoadingDealWithNetData.call(var1, this.getId());
                    return true;
                } catch (Exception var3) {
                    return false;
                }
            }
        }

        public void onUnauthorized(UdpConnection var1) {
            DebugLog.Multiplayer.warn(String.format("On unauthorized packet %s (%d) was received from user=\"%s\" (%d) ip %s %s", this.name(), this.requiredAccessLevel, var1.username, var1.accessLevel, var1.ip, SteamUtils.isSteamModeEnabled() ? var1.steamID : ""));

            try {
                this.unauthorizedPacketPolicy.apply(var1, this.name());
            } catch (Exception var3) {
                var3.printStackTrace();
            }

        }

        // $FF: synthetic method
        private static PacketTypes.PacketType[] $values() {
            return new PacketTypes.PacketType[]{Validate, Login, HumanVisual, KeepAlive, Vehicles, PlayerConnect, VehiclesUnreliable, VehicleAuthorization, MetaGrid, Helicopter, SyncIsoObject, PlayerTimeout, ServerMap, PassengerMap, AddItemToMap, SentChunk, SyncClock, AddInventoryItemToContainer, RemoveInventoryItemFromContainer, RemoveItemFromSquare, RequestLargeAreaZip, Equip, HitCharacter, AddCoopPlayer, WeaponHit, KillZombie, SandboxOptions, SmashWindow, PlayerDeath, RequestZipList, ItemStats, NotRequiredInZip, RequestData, GlobalObjects, ZombieDeath, AccessDenied, PlayerDamage, Bandage, EatFood, RequestItemsForContainer, Drink, SyncAlarmClock, PacketCounts, SendModData, RemoveContestedItemsFromInventory, ScoreboardUpdate, ReceiveModData, ServerQuit, PlaySound, WorldSound, AddAmbient, SyncClothing, ClientCommand, ObjectModData, ObjectChange, BloodSplatter, ZombieSound, ZombieDescriptors, SlowFactor, Weather, WorldMapPlayerPosition, RequestPlayerData, RemoveCorpseFromMap, AddCorpseToMap, BecomeCorpse, StartFire, UpdateItemSprite, StartRain, StopRain, WorldMessage, getModData, ReceiveCommand, ReloadOptions, Kicked, ExtraInfo, AddItemInInventory, ChangeSafety, Ping, WriteLog, AddXP, UpdateOverlaySprite, Checksum, ConstructedZone, RegisterZone, WoundInfection, Stitch, Disinfect, AdditionalPain, RemoveGlass, Splint, RemoveBullet, CleanBurn, SyncThumpable, SyncDoorKey, AddXpCommand, Teleport, RemoveBlood, AddExplosiveTrap, BodyDamageUpdate, SyncSafehouse, SledgehammerDestroy, StopFire, Cataplasm, AddAlarm, PlaySoundEveryPlayer, SyncFurnace, SendCustomColor, SyncCompost, ChangePlayerStats, AddXpFromPlayerStatsUI, SyncXP, PacketTypeShort, Userlog, AddUserlog, RemoveUserlog, AddWarningPoint, MessageForAdmin, WakeUpPlayer, SendTransactionID, GetDBSchema, GetTableResult, ExecuteQuery, ChangeTextColor, SyncNonPvpZone, SyncFaction, SendFactionInvite, AcceptedFactionInvite, AddTicket, ViewTickets, RemoveTicket, RequestTrading, TradingUIAddItem, TradingUIRemoveItem, TradingUIUpdateState, SendItemListNet, ChunkObjectState, ReadAnnotedMap, RequestInventory, SendInventory, InvMngReqItem, InvMngGetItem, InvMngRemoveItem, StartPause, StopPause, TimeSync, SyncIsoObjectReq, PlayerSave, SyncWorldObjectsReq, SyncObjects, SendPlayerProfile, LoadPlayerProfile, SpawnRegion, PlayerDamageFromCarCrash, PlayerAttachedItem, ZombieHelmetFalling, AddBrokenGlass, SyncPerks, SyncWeight, SyncInjuries, SyncEquippedRadioFreq, InitPlayerChat, PlayerJoinChat, PlayerLeaveChat, ChatMessageFromPlayer, ChatMessageToPlayer, PlayerStartPMChat, AddChatTab, RemoveChatTab, PlayerConnectedToChat, PlayerNotFound, SendSafehouseInvite, AcceptedSafehouseInvite, ClimateManagerPacket, IsoRegionServerPacket, IsoRegionClientRequestFullUpdate, EventPacket, Statistic, StatisticRequest, PlayerUpdateReliable, ActionPacket, ZombieControl, PlayWorldSound, StopSound, PlayerUpdate, ZombieSimulation, PingFromClient, ZombieSimulationReliable, EatBody, Thump, SyncRadioData, LoginQueueRequest2, LoginQueueDone2, ItemTransaction, KickOutOfSafehouse, SneezeCough, BurnCorpse, WaveSignal, PlayerListensChannel, RadioServerData, RadioDeviceDataState, SyncCustomLightSettings, ReplaceOnCooked, PlayerDataRequest, GlobalModData, GlobalModDataRequest, RadioPostSilenceEvent};
        }
    }

    public interface CallbackClientProcess {
        void call(ByteBuffer var1, short var2) throws IOException;
    }

    public interface CallbackServerProcess {
        void call(ByteBuffer var1, UdpConnection var2, short var3) throws Exception;
    }

    private static class PacketAuthorization {
        private static void unauthorizedPacketPolicyLogUser(UdpConnection var0, String var1) throws Exception {
            if (ServerOptions.instance.AntiCheatProtectionType8.getValue() && PacketValidator.checkUser(var0)) {
                PacketValidator.doBanUser(var0, var1, "Type8");
                DebugLog.log("<!Забанен по 8!>");
            }

        }

        private static void unauthorizedPacketPolicyKickUser(UdpConnection var0, String var1) throws Exception {
            if (ServerOptions.instance.AntiCheatProtectionType8.getValue() && PacketValidator.checkUser(var0)) {
                PacketValidator.doBanUser(var0, var1, "Type8");
                DebugLog.log("<!Забанен по 8!>");
            }

        }

        private static void unauthorizedPacketPolicyBanUser(UdpConnection var0, String var1) throws Exception {
            if (ServerOptions.instance.AntiCheatProtectionType8.getValue() && PacketValidator.checkUser(var0)) {
                PacketValidator.doBanUser(var0, var1, "Type8");
                DebugLog.log("<!Забанен по 8!>");
            }

        }

        private static boolean isAuthorized(UdpConnection var0, PacketTypes.PacketType var1) throws Exception {
            boolean var2 = (var0.accessLevel & var1.requiredAccessLevel) != 0;
            if ((!var2 || var1.serverProcess == null) && (!Core.bDebug || SystemDisabler.doKickInDebug)) {
                DebugLog.Multiplayer.warn(String.format("Unauthorized packet %s (%s) was received from user=\"%s\" (%s) ip %s %s", var1.name(), PlayerType.toString(var1.requiredAccessLevel), var0.username, PlayerType.toString(var0.accessLevel), var0.ip, SteamUtils.isSteamModeEnabled() ? var0.steamID : ""));
                var1.unauthorizedPacketPolicy.apply(var0, var1.name());
            }

            return var2;
        }

        public static enum Policy {
            Log(PacketTypes.PacketAuthorization::unauthorizedPacketPolicyLogUser),
            Kick(PacketTypes.PacketAuthorization::unauthorizedPacketPolicyKickUser),
            Ban(PacketTypes.PacketAuthorization::unauthorizedPacketPolicyBanUser);

            private final PacketTypes.PacketAuthorization.UnauthorizedPacketPolicy policy;

            private Policy(PacketTypes.PacketAuthorization.UnauthorizedPacketPolicy var3) {
                this.policy = var3;
            }

            private void apply(UdpConnection var1, String var2) throws Exception {
                this.policy.call(var1, var2);
            }

            // $FF: synthetic method
            private static PacketTypes.PacketAuthorization.Policy[] $values() {
                return new PacketTypes.PacketAuthorization.Policy[]{Log, Kick, Ban};
            }
        }

        public interface UnauthorizedPacketPolicy {
            void call(UdpConnection var1, String var2) throws Exception;
        }
    }
}
