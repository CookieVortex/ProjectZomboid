package zombie.network;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import zombie.ZomboidFileSystem;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigFile;
import zombie.config.ConfigOption;
import zombie.config.DoubleConfigOption;
import zombie.config.IntegerConfigOption;
import zombie.config.StringConfigOption;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.logger.LoggerManager;
import zombie.debug.DebugLog;

public class ServerOptions {
    public static final ServerOptions instance = new ServerOptions();
    private ArrayList publicOptions = new ArrayList();
    public static HashMap clientOptionsList = null;
    public static final int MAX_PORT = 65535;
    private ArrayList options = new ArrayList();


    private HashMap optionByName = new HashMap();
    public ServerOptions.BooleanServerOption PVP = new ServerOptions.BooleanServerOption(this, "PVP", true);
    public ServerOptions.BooleanServerOption PauseEmpty = new ServerOptions.BooleanServerOption(this, "PauseEmpty", true);
    public ServerOptions.BooleanServerOption GlobalChat = new ServerOptions.BooleanServerOption(this, "GlobalChat", true);
    public ServerOptions.StringServerOption ChatStreams = new ServerOptions.StringServerOption(this, "ChatStreams", "s,r,a,w,y,sh,f,all", -1);
    public ServerOptions.BooleanServerOption Open = new ServerOptions.BooleanServerOption(this, "Open", true);
    public ServerOptions.TextServerOption ServerWelcomeMessage = new ServerOptions.TextServerOption(this, "ServerWelcomeMessage", "Welcome to Project Zomboid Multiplayer! <LINE> <LINE> To interact with the Chat panel: press Tab, T, or Enter. <LINE> <LINE> The Tab key will change the target stream of the message. <LINE> <LINE> Global Streams: /all <LINE> Local Streams: /say, /yell <LINE> Special Steams: /whisper, /safehouse, /faction. <LINE> <LINE> Press the Up arrow to cycle through your message history. Click the Gear icon to customize chat. <LINE> <LINE> Happy surviving!", -1);
    public ServerOptions.BooleanServerOption AutoCreateUserInWhiteList = new ServerOptions.BooleanServerOption(this, "AutoCreateUserInWhiteList", false);
    public ServerOptions.BooleanServerOption DisplayUserName = new ServerOptions.BooleanServerOption(this, "DisplayUserName", true);
    public ServerOptions.BooleanServerOption ShowFirstAndLastName = new ServerOptions.BooleanServerOption(this, "ShowFirstAndLastName", false);
    public ServerOptions.StringServerOption SpawnPoint = new ServerOptions.StringServerOption(this, "SpawnPoint", "0,0,0", -1);
    public ServerOptions.BooleanServerOption SafetySystem = new ServerOptions.BooleanServerOption(this, "SafetySystem", true);
    public ServerOptions.BooleanServerOption ShowSafety = new ServerOptions.BooleanServerOption(this, "ShowSafety", true);
    public ServerOptions.IntegerServerOption SafetyToggleTimer = new ServerOptions.IntegerServerOption(this, "SafetyToggleTimer", 0, 1000, 2);
    public ServerOptions.IntegerServerOption SafetyCooldownTimer = new ServerOptions.IntegerServerOption(this, "SafetyCooldownTimer", 0, 1000, 3);
    public ServerOptions.StringServerOption SpawnItems = new ServerOptions.StringServerOption(this, "SpawnItems", "", -1);
    public ServerOptions.IntegerServerOption DefaultPort = new ServerOptions.IntegerServerOption(this, "DefaultPort", 0, 65535, 16261);
    public ServerOptions.IntegerServerOption UDPPort = new ServerOptions.IntegerServerOption(this, "UDPPort", 0, 65535, 16262);
    public ServerOptions.IntegerServerOption ResetID = new ServerOptions.IntegerServerOption(this, "ResetID", 0, Integer.MAX_VALUE, Rand.Next(1000000000));
    public ServerOptions.StringServerOption Mods = new ServerOptions.StringServerOption(this, "Mods", "", -1);
    public ServerOptions.StringServerOption Map = new ServerOptions.StringServerOption(this, "Map", "Muldraugh, KY", -1);
    public ServerOptions.BooleanServerOption DoLuaChecksum = new ServerOptions.BooleanServerOption(this, "DoLuaChecksum", true);
    public ServerOptions.BooleanServerOption DenyLoginOnOverloadedServer = new ServerOptions.BooleanServerOption(this, "DenyLoginOnOverloadedServer", true);
    public ServerOptions.BooleanServerOption Public = new ServerOptions.BooleanServerOption(this, "Public", false);
    public ServerOptions.StringServerOption PublicName = new ServerOptions.StringServerOption(this, "PublicName", "My PZ Server", 64);
    public ServerOptions.TextServerOption PublicDescription = new ServerOptions.TextServerOption(this, "PublicDescription", "", 256);
    public ServerOptions.IntegerServerOption MaxPlayers = new ServerOptions.IntegerServerOption(this, "MaxPlayers", 1, 100, 32);
    public ServerOptions.IntegerServerOption PingLimit = new ServerOptions.IntegerServerOption(this, "PingLimit", 100, Integer.MAX_VALUE, 400);
    public ServerOptions.IntegerServerOption HoursForLootRespawn = new ServerOptions.IntegerServerOption(this, "HoursForLootRespawn", 0, Integer.MAX_VALUE, 0);
    public ServerOptions.IntegerServerOption MaxItemsForLootRespawn = new ServerOptions.IntegerServerOption(this, "MaxItemsForLootRespawn", 1, Integer.MAX_VALUE, 4);
    public ServerOptions.BooleanServerOption ConstructionPreventsLootRespawn = new ServerOptions.BooleanServerOption(this, "ConstructionPreventsLootRespawn", true);
    public ServerOptions.BooleanServerOption DropOffWhiteListAfterDeath = new ServerOptions.BooleanServerOption(this, "DropOffWhiteListAfterDeath", false);
    public ServerOptions.BooleanServerOption NoFire = new ServerOptions.BooleanServerOption(this, "NoFire", false);
    public ServerOptions.BooleanServerOption AnnounceDeath = new ServerOptions.BooleanServerOption(this, "AnnounceDeath", false);
    public ServerOptions.DoubleServerOption MinutesPerPage = new ServerOptions.DoubleServerOption(this, "MinutesPerPage", 0.0D, 60.0D, 1.0D);
    public ServerOptions.IntegerServerOption SaveWorldEveryMinutes = new ServerOptions.IntegerServerOption(this, "SaveWorldEveryMinutes", 0, Integer.MAX_VALUE, 0);
    public ServerOptions.BooleanServerOption PlayerSafehouse = new ServerOptions.BooleanServerOption(this, "PlayerSafehouse", false);
    public ServerOptions.BooleanServerOption AdminSafehouse = new ServerOptions.BooleanServerOption(this, "AdminSafehouse", false);
    public ServerOptions.BooleanServerOption SafehouseAllowTrepass = new ServerOptions.BooleanServerOption(this, "SafehouseAllowTrepass", true);
    public ServerOptions.BooleanServerOption SafehouseAllowFire = new ServerOptions.BooleanServerOption(this, "SafehouseAllowFire", true);
    public ServerOptions.BooleanServerOption SafehouseAllowLoot = new ServerOptions.BooleanServerOption(this, "SafehouseAllowLoot", true);
    public ServerOptions.BooleanServerOption SafehouseAllowRespawn = new ServerOptions.BooleanServerOption(this, "SafehouseAllowRespawn", false);
    public ServerOptions.IntegerServerOption SafehouseDaySurvivedToClaim = new ServerOptions.IntegerServerOption(this, "SafehouseDaySurvivedToClaim", 0, Integer.MAX_VALUE, 0);
    public ServerOptions.IntegerServerOption SafeHouseRemovalTime = new ServerOptions.IntegerServerOption(this, "SafeHouseRemovalTime", 0, Integer.MAX_VALUE, 144);
    public ServerOptions.BooleanServerOption SafehouseAllowNonResidential = new ServerOptions.BooleanServerOption(this, "SafehouseAllowNonResidential", false);
    public ServerOptions.BooleanServerOption AllowDestructionBySledgehammer = new ServerOptions.BooleanServerOption(this, "AllowDestructionBySledgehammer", true);
    public ServerOptions.BooleanServerOption SledgehammerOnlyInSafehouse = new ServerOptions.BooleanServerOption(this, "SledgehammerOnlyInSafehouse", false);
    public ServerOptions.BooleanServerOption KickFastPlayers = new ServerOptions.BooleanServerOption(this, "KickFastPlayers", false);
    public ServerOptions.StringServerOption ServerPlayerID = new ServerOptions.StringServerOption(this, "ServerPlayerID", Integer.toString(Rand.Next(Integer.MAX_VALUE)), -1);
    public ServerOptions.IntegerServerOption RCONPort = new ServerOptions.IntegerServerOption(this, "RCONPort", 0, 65535, 27015);
    public ServerOptions.StringServerOption RCONPassword = new ServerOptions.StringServerOption(this, "RCONPassword", "", -1);
    public ServerOptions.BooleanServerOption DiscordEnable = new ServerOptions.BooleanServerOption(this, "DiscordEnable", false);
    public ServerOptions.StringServerOption DiscordToken = new ServerOptions.StringServerOption(this, "DiscordToken", "", -1);
    public ServerOptions.StringServerOption DiscordChannel = new ServerOptions.StringServerOption(this, "DiscordChannel", "", -1);
    public ServerOptions.StringServerOption DiscordChannelID = new ServerOptions.StringServerOption(this, "DiscordChannelID", "", -1);
    public ServerOptions.StringServerOption Password = new ServerOptions.StringServerOption(this, "Password", "", -1);
    public ServerOptions.IntegerServerOption MaxAccountsPerUser = new ServerOptions.IntegerServerOption(this, "MaxAccountsPerUser", 0, Integer.MAX_VALUE, 0);
    public ServerOptions.BooleanServerOption AllowCoop = new ServerOptions.BooleanServerOption(this, "AllowCoop", true);
    public ServerOptions.BooleanServerOption SleepAllowed = new ServerOptions.BooleanServerOption(this, "SleepAllowed", false);
    public ServerOptions.BooleanServerOption SleepNeeded = new ServerOptions.BooleanServerOption(this, "SleepNeeded", false);
    public ServerOptions.BooleanServerOption KnockedDownAllowed = new ServerOptions.BooleanServerOption(this, "KnockedDownAllowed", true);
    public ServerOptions.BooleanServerOption SneakModeHideFromOtherPlayers = new ServerOptions.BooleanServerOption(this, "SneakModeHideFromOtherPlayers", true);
    public ServerOptions.StringServerOption WorkshopItems = new ServerOptions.StringServerOption(this, "WorkshopItems", "", -1);
    public ServerOptions.StringServerOption SteamScoreboard = new ServerOptions.StringServerOption(this, "SteamScoreboard", "true", -1);
    public ServerOptions.BooleanServerOption SteamVAC = new ServerOptions.BooleanServerOption(this, "SteamVAC", true);
    public ServerOptions.BooleanServerOption UPnP = new ServerOptions.BooleanServerOption(this, "UPnP", true);
    public ServerOptions.BooleanServerOption VoiceEnable = new ServerOptions.BooleanServerOption(this, "VoiceEnable", true);
    public ServerOptions.DoubleServerOption VoiceMinDistance = new ServerOptions.DoubleServerOption(this, "VoiceMinDistance", 0.0D, 100000.0D, 10.0D);
    public ServerOptions.DoubleServerOption VoiceMaxDistance = new ServerOptions.DoubleServerOption(this, "VoiceMaxDistance", 0.0D, 100000.0D, 100.0D);
    public ServerOptions.BooleanServerOption Voice3D = new ServerOptions.BooleanServerOption(this, "Voice3D", true);
    public ServerOptions.DoubleServerOption SpeedLimit = new ServerOptions.DoubleServerOption(this, "SpeedLimit", 10.0D, 150.0D, 70.0D);
    public ServerOptions.BooleanServerOption LoginQueueEnabled = new ServerOptions.BooleanServerOption(this, "LoginQueueEnabled", false);
    public ServerOptions.IntegerServerOption LoginQueueConnectTimeout = new ServerOptions.IntegerServerOption(this, "LoginQueueConnectTimeout", 20, 1200, 60);
    public ServerOptions.StringServerOption server_browser_announced_ip = new ServerOptions.StringServerOption(this, "server_browser_announced_ip", "", -1);
    public ServerOptions.BooleanServerOption PlayerRespawnWithSelf = new ServerOptions.BooleanServerOption(this, "PlayerRespawnWithSelf", false);
    public ServerOptions.BooleanServerOption PlayerRespawnWithOther = new ServerOptions.BooleanServerOption(this, "PlayerRespawnWithOther", false);
    public ServerOptions.DoubleServerOption FastForwardMultiplier = new ServerOptions.DoubleServerOption(this, "FastForwardMultiplier", 1.0D, 100.0D, 40.0D);
    public ServerOptions.BooleanServerOption DisableSafehouseWhenPlayerConnected = new ServerOptions.BooleanServerOption(this, "DisableSafehouseWhenPlayerConnected", false);
    public ServerOptions.BooleanServerOption Faction = new ServerOptions.BooleanServerOption(this, "Faction", true);
    public ServerOptions.IntegerServerOption FactionDaySurvivedToCreate = new ServerOptions.IntegerServerOption(this, "FactionDaySurvivedToCreate", 0, Integer.MAX_VALUE, 0);
    public ServerOptions.IntegerServerOption FactionPlayersRequiredForTag = new ServerOptions.IntegerServerOption(this, "FactionPlayersRequiredForTag", 1, Integer.MAX_VALUE, 1);
    public ServerOptions.BooleanServerOption DisableRadioStaff = new ServerOptions.BooleanServerOption(this, "DisableRadioStaff", false);
    public ServerOptions.BooleanServerOption DisableRadioAdmin = new ServerOptions.BooleanServerOption(this, "DisableRadioAdmin", true);
    public ServerOptions.BooleanServerOption DisableRadioGM = new ServerOptions.BooleanServerOption(this, "DisableRadioGM", true);
    public ServerOptions.BooleanServerOption DisableRadioOverseer = new ServerOptions.BooleanServerOption(this, "DisableRadioOverseer", false);
    public ServerOptions.BooleanServerOption DisableRadioModerator = new ServerOptions.BooleanServerOption(this, "DisableRadioModerator", false);
    public ServerOptions.BooleanServerOption DisableRadioInvisible = new ServerOptions.BooleanServerOption(this, "DisableRadioInvisible", true);
    public ServerOptions.StringServerOption ClientCommandFilter = new ServerOptions.StringServerOption(this, "ClientCommandFilter", "-vehicle.*;+vehicle.damageWindow;+vehicle.fixPart;+vehicle.installPart;+vehicle.uninstallPart", -1);
    public ServerOptions.StringServerOption ClientActionLogs = new ServerOptions.StringServerOption(this, "ClientActionLogs", "ISEnterVehicle;ISExitVehicle;ISTakeEngineParts;", -1);
    public ServerOptions.BooleanServerOption PerkLogs = new ServerOptions.BooleanServerOption(this, "PerkLogs", true);
    public ServerOptions.IntegerServerOption ItemNumbersLimitPerContainer = new ServerOptions.IntegerServerOption(this, "ItemNumbersLimitPerContainer", 0, 9000, 0);
    public ServerOptions.IntegerServerOption BloodSplatLifespanDays = new ServerOptions.IntegerServerOption(this, "BloodSplatLifespanDays", 0, 365, 0);
    public ServerOptions.BooleanServerOption AllowNonAsciiUsername = new ServerOptions.BooleanServerOption(this, "AllowNonAsciiUsername", false);
    public ServerOptions.BooleanServerOption BanKickGlobalSound = new ServerOptions.BooleanServerOption(this, "BanKickGlobalSound", true);
    public ServerOptions.BooleanServerOption RemovePlayerCorpsesOnCorpseRemoval = new ServerOptions.BooleanServerOption(this, "RemovePlayerCorpsesOnCorpseRemoval", false);
    public ServerOptions.BooleanServerOption TrashDeleteAll = new ServerOptions.BooleanServerOption(this, "TrashDeleteAll", false);
    public ServerOptions.BooleanServerOption PVPMeleeWhileHitReaction = new ServerOptions.BooleanServerOption(this, "PVPMeleeWhileHitReaction", false);
    public ServerOptions.BooleanServerOption MouseOverToSeeDisplayName = new ServerOptions.BooleanServerOption(this, "MouseOverToSeeDisplayName", true);
    public ServerOptions.BooleanServerOption HidePlayersBehindYou = new ServerOptions.BooleanServerOption(this, "HidePlayersBehindYou", true);
    public ServerOptions.DoubleServerOption PVPMeleeDamageModifier = new ServerOptions.DoubleServerOption(this, "PVPMeleeDamageModifier", 0.0D, 500.0D, 30.0D);
    public ServerOptions.DoubleServerOption PVPFirearmDamageModifier = new ServerOptions.DoubleServerOption(this, "PVPFirearmDamageModifier", 0.0D, 500.0D, 50.0D);
    public ServerOptions.DoubleServerOption CarEngineAttractionModifier = new ServerOptions.DoubleServerOption(this, "CarEngineAttractionModifier", 0.0D, 10.0D, 0.5D);
    public ServerOptions.BooleanServerOption PlayerBumpPlayer = new ServerOptions.BooleanServerOption(this, "PlayerBumpPlayer", false);
    public ServerOptions.IntegerServerOption MapRemotePlayerVisibility = new ServerOptions.IntegerServerOption(this, "MapRemotePlayerVisibility", 1, 3, 1);
    public ServerOptions.IntegerServerOption BackupsCount = new ServerOptions.IntegerServerOption(this, "BackupsCount", 1, 300, 5);
    public ServerOptions.BooleanServerOption BackupsOnStart = new ServerOptions.BooleanServerOption(this, "BackupsOnStart", true);
    public ServerOptions.BooleanServerOption BackupsOnVersionChange = new ServerOptions.BooleanServerOption(this, "BackupsOnVersionChange", true);
    public ServerOptions.IntegerServerOption BackupsPeriod = new ServerOptions.IntegerServerOption(this, "BackupsPeriod", 0, 1500, 0);
    public ServerOptions.BooleanServerOption AntiCheatProtectionType1 = new ServerOptions.BooleanServerOption(this, "AntiCheatProtectionType1", true);
    public ServerOptions.BooleanServerOption AntiCheatProtectionType2 = new ServerOptions.BooleanServerOption(this, "AntiCheatProtectionType2", true);
    public ServerOptions.BooleanServerOption AntiCheatProtectionType3 = new ServerOptions.BooleanServerOption(this, "AntiCheatProtectionType3", true);
    public ServerOptions.BooleanServerOption AntiCheatProtectionType4 = new ServerOptions.BooleanServerOption(this, "AntiCheatProtectionType4", true);
    public ServerOptions.BooleanServerOption AntiCheatProtectionType5 = new ServerOptions.BooleanServerOption(this, "AntiCheatProtectionType5", true);
    public ServerOptions.BooleanServerOption AntiCheatProtectionType6 = new ServerOptions.BooleanServerOption(this, "AntiCheatProtectionType6", true);
    public ServerOptions.BooleanServerOption AntiCheatProtectionType7 = new ServerOptions.BooleanServerOption(this, "AntiCheatProtectionType7", true);
    public ServerOptions.BooleanServerOption AntiCheatProtectionType8 = new ServerOptions.BooleanServerOption(this, "AntiCheatProtectionType8", true);
    public ServerOptions.BooleanServerOption FakeAntiCheatProtectionType8 = new ServerOptions.BooleanServerOption(this, "AntiCheatProtectionTypе8", false);
    public ServerOptions.BooleanServerOption AntiCheatProtectionType9 = new ServerOptions.BooleanServerOption(this, "AntiCheatProtectionType9", true);
    public ServerOptions.BooleanServerOption AntiCheatProtectionType10 = new ServerOptions.BooleanServerOption(this, "AntiCheatProtectionType10", true);
    public ServerOptions.BooleanServerOption AntiCheatProtectionType11 = new ServerOptions.BooleanServerOption(this, "AntiCheatProtectionType11", true);
    public ServerOptions.BooleanServerOption AntiCheatProtectionType12 = new ServerOptions.BooleanServerOption(this, "AntiCheatProtectionType12", false);
    public ServerOptions.BooleanServerOption FakeAntiCheatProtectionType12 = new ServerOptions.BooleanServerOption(this, "AntiCheatProtectionTypе12", true);
    public ServerOptions.BooleanServerOption AntiCheatProtectionType13 = new ServerOptions.BooleanServerOption(this, "AntiCheatProtectionType13", true);
    public ServerOptions.BooleanServerOption AntiCheatProtectionType14 = new ServerOptions.BooleanServerOption(this, "AntiCheatProtectionType14", true);
    public ServerOptions.BooleanServerOption AntiCheatProtectionType15 = new ServerOptions.BooleanServerOption(this, "AntiCheatProtectionType15", true);
    public ServerOptions.BooleanServerOption AntiCheatProtectionType16 = new ServerOptions.BooleanServerOption(this, "AntiCheatProtectionType16", true);
    public ServerOptions.BooleanServerOption AntiCheatProtectionType17 = new ServerOptions.BooleanServerOption(this, "AntiCheatProtectionType17", true);
    public ServerOptions.BooleanServerOption AntiCheatProtectionType18 = new ServerOptions.BooleanServerOption(this, "AntiCheatProtectionType18", true);
    public ServerOptions.BooleanServerOption AntiCheatProtectionType19 = new ServerOptions.BooleanServerOption(this, "AntiCheatProtectionType19", true);
    public ServerOptions.BooleanServerOption AntiCheatProtectionType20 = new ServerOptions.BooleanServerOption(this, "AntiCheatProtectionType20", true);
    public ServerOptions.BooleanServerOption AntiCheatProtectionType21 = new ServerOptions.BooleanServerOption(this, "AntiCheatProtectionType21", true);
    public ServerOptions.BooleanServerOption AntiCheatProtectionType22 = new ServerOptions.BooleanServerOption(this, "AntiCheatProtectionType22", true);
    public ServerOptions.BooleanServerOption AntiCheatProtectionType23 = new ServerOptions.BooleanServerOption(this, "AntiCheatProtectionType23", true);
    public ServerOptions.BooleanServerOption AntiCheatProtectionType24 = new ServerOptions.BooleanServerOption(this, "AntiCheatProtectionType24", true);
    public ServerOptions.DoubleServerOption AntiCheatProtectionType2ThresholdMultiplier = new ServerOptions.DoubleServerOption(this, "AntiCheatProtectionType2ThresholdMultiplier", 1.0D, 10.0D, 3.0D);
    public ServerOptions.DoubleServerOption AntiCheatProtectionType3ThresholdMultiplier = new ServerOptions.DoubleServerOption(this, "AntiCheatProtectionType3ThresholdMultiplier", 1.0D, 10.0D, 1.0D);
    public ServerOptions.DoubleServerOption AntiCheatProtectionType4ThresholdMultiplier = new ServerOptions.DoubleServerOption(this, "AntiCheatProtectionType4ThresholdMultiplier", 1.0D, 10.0D, 1.0D);
    public ServerOptions.DoubleServerOption AntiCheatProtectionType9ThresholdMultiplier = new ServerOptions.DoubleServerOption(this, "AntiCheatProtectionType9ThresholdMultiplier", 1.0D, 10.0D, 1.0D);
    public ServerOptions.DoubleServerOption AntiCheatProtectionType15ThresholdMultiplier = new ServerOptions.DoubleServerOption(this, "AntiCheatProtectionType15ThresholdMultiplier", 1.0D, 10.0D, 1.0D);
    public ServerOptions.DoubleServerOption AntiCheatProtectionType20ThresholdMultiplier = new ServerOptions.DoubleServerOption(this, "AntiCheatProtectionType20ThresholdMultiplier", 1.0D, 10.0D, 1.0D);
    public ServerOptions.DoubleServerOption AntiCheatProtectionType22ThresholdMultiplier = new ServerOptions.DoubleServerOption(this, "AntiCheatProtectionType22ThresholdMultiplier", 1.0D, 10.0D, 1.0D);
    public ServerOptions.DoubleServerOption AntiCheatProtectionType24ThresholdMultiplier = new ServerOptions.DoubleServerOption(this, "AntiCheatProtectionType24ThresholdMultiplier", 1.0D, 10.0D, 6.0D);
    public static ArrayList cardList = null;

    public ServerOptions() {
        this.publicOptions.clear();
        this.publicOptions.addAll(this.optionByName.keySet());
        this.publicOptions.remove("Password");
        this.publicOptions.remove("RCONPort");
        this.publicOptions.remove("RCONPassword");
        this.publicOptions.remove("AntiCheatProtectionType8");
        this.publicOptions.remove("AntiCheatProtectionType12");
        this.publicOptions.remove(this.DiscordToken.getName());
        this.publicOptions.remove(this.DiscordChannel.getName());
        this.publicOptions.remove(this.DiscordChannelID.getName());
        Collections.sort(this.publicOptions);


    }

    private void initOptions() {
        initClientCommandsHelp();
        Iterator var1 = this.options.iterator();

        while(var1.hasNext()) {
            ServerOptions.ServerOption var2 = (ServerOptions.ServerOption)var1.next();
            var2.asConfigOption().resetToDefault();
        }

    }

    public ArrayList getPublicOptions() {
        return this.publicOptions;
    }

    public ArrayList getOptions() {
        return this.options;
    }

    public static void initClientCommandsHelp() {
        clientOptionsList = new HashMap();
        clientOptionsList.put("help", Translator.getText("UI_ServerOptionDesc_Help"));
        clientOptionsList.put("changepwd", Translator.getText("UI_ServerOptionDesc_ChangePwd"));
        clientOptionsList.put("roll", Translator.getText("UI_ServerOptionDesc_Roll"));
        clientOptionsList.put("card", Translator.getText("UI_ServerOptionDesc_Card"));
        clientOptionsList.put("safehouse", Translator.getText("UI_ServerOptionDesc_SafeHouse"));
    }

    public void init() {
        this.initOptions();
        String var10002 = ZomboidFileSystem.instance.getCacheDir();
        File var1 = new File(var10002 + File.separator + "Server");
        if (!var1.exists()) {
            var1.mkdirs();
        }

        var10002 = ZomboidFileSystem.instance.getCacheDir();
        File var2 = new File(var10002 + File.separator + "Server" + File.separator + GameServer.ServerName + ".ini");
        if (var2.exists()) {
            try {
                Core.getInstance().loadOptions();
            } catch (IOException var4) {
                var4.printStackTrace();
            }

            if (this.loadServerTextFile(GameServer.ServerName)) {
                this.saveServerTextFile(GameServer.ServerName);
            }
        } else {
            String var10003 = ZomboidFileSystem.instance.getCacheDir();
            this.initSpawnRegionsFile(new File(var10003 + File.separator + "Server" + File.separator + GameServer.ServerName + "_spawnregions.lua"));
            this.saveServerTextFile(GameServer.ServerName);
        }

        LoggerManager.init();
    }

    public void resetRegionFile() {
        String var10002 = ZomboidFileSystem.instance.getCacheDir();
        File var1 = new File(var10002 + File.separator + "Server" + File.separator + GameServer.ServerName + "_spawnregions.lua");
        var1.delete();
        this.initSpawnRegionsFile(var1);
    }

    private void initSpawnRegionsFile(File var1) {
        if (!var1.exists()) {
            DebugLog.log("creating server spawnregions file \"" + var1.getPath() + "\"");

            try {
                var1.createNewFile();
                FileWriter var2 = new FileWriter(var1);
                var2.write("function SpawnRegions()" + System.lineSeparator());
                var2.write("\treturn {" + System.lineSeparator());
                var2.write("\t\t{ name = \"Muldraugh, KY\", file = \"media/maps/Muldraugh, KY/spawnpoints.lua\" }," + System.lineSeparator());
                var2.write("\t\t{ name = \"West Point, KY\", file = \"media/maps/West Point, KY/spawnpoints.lua\" }," + System.lineSeparator());
                var2.write("\t\t{ name = \"Rosewood, KY\", file = \"media/maps/Rosewood, KY/spawnpoints.lua\" }," + System.lineSeparator());
                var2.write("\t\t{ name = \"Riverside, KY\", file = \"media/maps/Riverside, KY/spawnpoints.lua\" }," + System.lineSeparator());
                var2.write("\t\t-- Uncomment the line below to add a custom spawnpoint for this server." + System.lineSeparator());
                String var10001 = GameServer.ServerName;
                var2.write("--\t\t{ name = \"Twiggy's Bar\", serverfile = \"" + var10001 + "_spawnpoints.lua\" }," + System.lineSeparator());
                var2.write("\t}" + System.lineSeparator());
                var2.write("end" + System.lineSeparator());
                var2.close();
                String var10002 = var1.getParent();
                var2 = new FileWriter(var10002 + File.separator + GameServer.ServerName + "_spawnpoints.lua");
                var2.write("function SpawnPoints()" + System.lineSeparator());
                var2.write("\treturn {" + System.lineSeparator());
                var2.write("\t\tunemployed = {" + System.lineSeparator());
                var2.write("\t\t\t{ worldX = 40, worldY = 22, posX = 67, posY = 201 }" + System.lineSeparator());
                var2.write("\t\t}" + System.lineSeparator());
                var2.write("\t}" + System.lineSeparator());
                var2.write("end" + System.lineSeparator());
                var2.close();
            } catch (Exception var3) {
                var3.printStackTrace();
            }

        }
    }

    public String getOption(String var1) {

        ServerOptions.ServerOption var2 = this.getOptionByName(var1);
        return var2 == null ? null : var2.asConfigOption().getValueAsString();
    }

    public Boolean getBoolean(String var1) {
        ServerOptions.ServerOption var2 = this.getOptionByName(var1);
        return false;
    }

    public Float getFloat(String var1) {
        ServerOptions.ServerOption var2 = this.getOptionByName(var1);
        return var2 instanceof ServerOptions.DoubleServerOption ? (float)((ServerOptions.DoubleServerOption)var2).getValue() : null;
    }

    public Double getDouble(String var1) {
        ServerOptions.ServerOption var2 = this.getOptionByName(var1);
        return var2 instanceof ServerOptions.DoubleServerOption ? ((ServerOptions.DoubleServerOption)var2).getValue() : null;
    }

    public Integer getInteger(String var1) {
        ServerOptions.ServerOption var2 = this.getOptionByName(var1);
        return var2 instanceof ServerOptions.IntegerServerOption ? ((ServerOptions.IntegerServerOption)var2).getValue() : null;
    }

    public void putOption(String var1, String var2) {
        ServerOptions.ServerOption var3 = this.getOptionByName(var1);
        if (var3 != null) {
            var3.asConfigOption().parse(var2);
        }

    }

    public void putSaveOption(String var1, String var2) {
        this.putOption(var1, var2);
        this.saveServerTextFile(GameServer.ServerName);
    }

    public String changeOption(String var1, String var2) {
        ServerOptions.ServerOption var3 = this.getOptionByName(var1);
        if (var3 == null) {
            return "Option " + var1 + " doesn't exist.";
        } else {
            var3.asConfigOption().parse(var2);
            return !this.saveServerTextFile(GameServer.ServerName) ? "An error as occured." : "Option : " + var1 + " is now : " + var3.asConfigOption().getValueAsString();
        }
    }

    public static ServerOptions getInstance() {
        return instance;
    }

    public static ArrayList getClientCommandList(boolean var0) {
        String var1 = " <LINE> ";
        if (!var0) {
            var1 = "\n";
        }

        if (clientOptionsList == null) {
            initClientCommandsHelp();
        }

        ArrayList var2 = new ArrayList();
        Iterator var3 = clientOptionsList.keySet().iterator();
        String var4 = null;
        var2.add("List of commands : " + var1);

        while(var3.hasNext()) {
            var4 = (String)var3.next();
            var2.add("* " + var4 + " : " + (String)clientOptionsList.get(var4) + (var3.hasNext() ? var1 : ""));
        }

        return var2;
    }

    public static String getRandomCard() {
        if (cardList == null) {
            cardList = new ArrayList();
            cardList.add("the Ace of Clubs");
            cardList.add("a Two of Clubs");
            cardList.add("a Three of Clubs");
            cardList.add("a Four of Clubs");
            cardList.add("a Five of Clubs");
            cardList.add("a Six of Clubs");
            cardList.add("a Seven of Clubs");
            cardList.add("a Height of Clubs");
            cardList.add("a Nine of Clubs");
            cardList.add("a Ten of Clubs");
            cardList.add("the Jack of Clubs");
            cardList.add("the Queen of Clubs");
            cardList.add("the King of Clubs");
            cardList.add("the Ace of Diamonds");
            cardList.add("a Two of Diamonds");
            cardList.add("a Three of Diamonds");
            cardList.add("a Four of Diamonds");
            cardList.add("a Five of Diamonds");
            cardList.add("a Six of Diamonds");
            cardList.add("a Seven of Diamonds");
            cardList.add("a Height of Diamonds");
            cardList.add("a Nine of Diamonds");
            cardList.add("a Ten of Diamonds");
            cardList.add("the Jack of Diamonds");
            cardList.add("the Queen of Diamonds");
            cardList.add("the King of Diamonds");
            cardList.add("the Ace of Hearts");
            cardList.add("a Two of Hearts");
            cardList.add("a Three of Hearts");
            cardList.add("a Four of Hearts");
            cardList.add("a Five of Hearts");
            cardList.add("a Six of Hearts");
            cardList.add("a Seven of Hearts");
            cardList.add("a Height of Hearts");
            cardList.add("a Nine of Hearts");
            cardList.add("a Ten of Hearts");
            cardList.add("the Jack of Hearts");
            cardList.add("the Queen of Hearts");
            cardList.add("the King of Hearts");
            cardList.add("the Ace of Spades");
            cardList.add("a Two of Spades");
            cardList.add("a Three of Spades");
            cardList.add("a Four of Spades");
            cardList.add("a Five of Spades");
            cardList.add("a Six of Spades");
            cardList.add("a Seven of Spades");
            cardList.add("a Height of Spades");
            cardList.add("a Nine of Spades");
            cardList.add("a Ten of Spades");
            cardList.add("the Jack of Spades");
            cardList.add("the Queen of Spades");
            cardList.add("the King of Spades");
        }

        return (String)cardList.get(Rand.Next(cardList.size()));
    }

    public void addOption(ServerOptions.ServerOption var1) {
        if (this.optionByName.containsKey(var1.asConfigOption().getName())) {
            throw new IllegalArgumentException();
        } else {
            this.options.add(var1);
            this.optionByName.put(var1.asConfigOption().getName(), var1);
        }
    }

    public int getNumOptions() {
        return this.options.size();
    }

    public ServerOptions.ServerOption getOptionByIndex(int var1) {
        return (ServerOptions.ServerOption)this.options.get(var1);
    }

    public ServerOptions.ServerOption getOptionByName(String var1) {
        return (ServerOptions.ServerOption)this.optionByName.get(var1);
    }

    public boolean loadServerTextFile(String var1) {
        ConfigFile var2 = new ConfigFile();
        String var10000 = ZomboidFileSystem.instance.getCacheDir();
        String var3 = var10000 + File.separator + "Server" + File.separator + var1 + ".ini";
        if (var2.read(var3)) {
            Iterator var4 = var2.getOptions().iterator();

            while(var4.hasNext()) {
                ConfigOption var5 = (ConfigOption)var4.next();
                ServerOptions.ServerOption var6 = (ServerOptions.ServerOption)this.optionByName.get(var5.getName());
                if (var6 != null) {
                    var6.asConfigOption().parse(var5.getValueAsString());
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean saveServerTextFile(String var1) {
        ConfigFile var2 = new ConfigFile();
        String var10000 = ZomboidFileSystem.instance.getCacheDir();
        String var3 = var10000 + File.separator + "Server" + File.separator + var1 + ".ini";
        ArrayList var4 = new ArrayList();
        Iterator var5 = this.options.iterator();

        while(var5.hasNext()) {
            ServerOptions.ServerOption var6 = (ServerOptions.ServerOption)var5.next();
            var4.add(var6.asConfigOption());
        }

        return var2.write(var3, 0, var4);
    }

    public int getMaxPlayers() {
        return Math.min(100, getInstance().MaxPlayers.getValue());
    }

    public static class BooleanServerOption extends BooleanConfigOption implements ServerOptions.ServerOption {
        public BooleanServerOption(ServerOptions var1, String var2, boolean var3) {
            super(var2, var3);
            var1.addOption(this);
        }

        public ConfigOption asConfigOption() {
            return this;
        }

        public String getTooltip() {
            return Translator.getTextOrNull("UI_ServerOption_" + this.name + "_tooltip");
        }
    }

    public static class StringServerOption extends StringConfigOption implements ServerOptions.ServerOption {
        public StringServerOption(ServerOptions var1, String var2, String var3, int var4) {
            super(var2, var3, var4);
            var1.addOption(this);
        }

        public ConfigOption asConfigOption() {
            return this;
        }

        public String getTooltip() {
            return Translator.getTextOrNull("UI_ServerOption_" + this.name + "_tooltip");
        }
    }

    public static class TextServerOption extends StringConfigOption implements ServerOptions.ServerOption {
        public TextServerOption(ServerOptions var1, String var2, String var3, int var4) {
            super(var2, var3, var4);
            var1.addOption(this);
        }

        public String getType() {
            return "text";
        }

        public ConfigOption asConfigOption() {
            return this;
        }

        public String getTooltip() {
            return Translator.getTextOrNull("UI_ServerOption_" + this.name + "_tooltip");
        }
    }

    public static class IntegerServerOption extends IntegerConfigOption implements ServerOptions.ServerOption {
        public IntegerServerOption(ServerOptions var1, String var2, int var3, int var4, int var5) {
            super(var2, var3, var4, var5);
            var1.addOption(this);
        }

        public ConfigOption asConfigOption() {
            return this;
        }

        public String getTooltip() {
            String var1 = Translator.getTextOrNull("UI_ServerOption_" + this.name + "_tooltip");
            String var2 = Translator.getText("Sandbox_MinMaxDefault", this.min, this.max, this.defaultValue);
            if (var1 == null) {
                return var2;
            } else {
                return var2 == null ? var1 : var1 + "\\n" + var2;
            }
        }
    }

    public static class DoubleServerOption extends DoubleConfigOption implements ServerOptions.ServerOption {
        public DoubleServerOption(ServerOptions var1, String var2, double var3, double var5, double var7) {
            super(var2, var3, var5, var7);
            var1.addOption(this);
        }

        public ConfigOption asConfigOption() {
            return this;
        }

        public String getTooltip() {
            String var1 = Translator.getTextOrNull("UI_ServerOption_" + this.name + "_tooltip");
            String var2 = Translator.getText("Sandbox_MinMaxDefault", String.format("%.02f", this.min), String.format("%.02f", this.max), String.format("%.02f", this.defaultValue));
            if (var1 == null) {
                return var2;
            } else {
                return var2 == null ? var1 : var1 + "\\n" + var2;
            }
        }
    }

    public interface ServerOption {
        ConfigOption asConfigOption();

        String getTooltip();
    }
}
