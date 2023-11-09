package zombie.commands.serverCommands;

import zombie.characters.IsoPlayer;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.skills.PerkFactory;
import zombie.characters.traits.TraitCollection;
import zombie.characters.traits.TraitFactory;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.ComboItem;
import zombie.inventory.types.HandWeapon;
import zombie.iso.*;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoThumpable;
import zombie.network.GameServer;
import zombie.network.ServerWorldDatabase;

import java.sql.SQLException;
import java.util.ArrayList;

@CommandName(
        name = "check"
)

@CommandHelp(
        helpText = "Гыгы, живи"
)
@RequiredRight(
        requiredRights = 60
)
public class checkPlayers extends CommandBase {
    public checkPlayers(String var1, String var2, String var3, UdpConnection var4) {
        super(var1, var2, var3, var4);
    }


    PerkFactory.Perk[] listOfPerksTrue = {
            PerkFactory.Perks.Cooking, PerkFactory.Perks.Fitness, PerkFactory.Perks.Strength, PerkFactory.Perks.Blunt, PerkFactory.Perks.Axe, PerkFactory.Perks.Sprinting, PerkFactory.Perks.Lightfoot, PerkFactory.Perks.Nimble, PerkFactory.Perks.Sneak, PerkFactory.Perks.Woodwork, PerkFactory.Perks.Aiming, PerkFactory.Perks.Reloading, PerkFactory.Perks.Farming, PerkFactory.Perks.Fishing, PerkFactory.Perks.Trapping, PerkFactory.Perks.PlantScavenging, PerkFactory.Perks.Doctor, PerkFactory.Perks.Electricity,PerkFactory.Perks.MetalWelding, PerkFactory.Perks.Mechanics, PerkFactory.Perks.Spear, PerkFactory.Perks.Maintenance, PerkFactory.Perks.SmallBlade, PerkFactory.Perks.LongBlade, PerkFactory.Perks.SmallBlunt, PerkFactory.Perks.Tailoring
    };

    PerkFactory.Perk[] listOfPerksTest = {PerkFactory.Perks.Agility, PerkFactory.Perks.Cooking, PerkFactory.Perks.Melee, PerkFactory.Perks.Crafting, PerkFactory.Perks.Fitness, PerkFactory.Perks.Strength, PerkFactory.Perks.Blunt, PerkFactory.Perks.Axe, PerkFactory.Perks.Sprinting, PerkFactory.Perks.Lightfoot, PerkFactory.Perks.Nimble, PerkFactory.Perks.Sneak, PerkFactory.Perks.Woodwork, PerkFactory.Perks.Aiming, PerkFactory.Perks.Reloading, PerkFactory.Perks.Farming, PerkFactory.Perks.Survivalist, PerkFactory.Perks.Fishing, PerkFactory.Perks.Trapping, PerkFactory.Perks.Passiv, PerkFactory.Perks.Firearm, PerkFactory.Perks.PlantScavenging, PerkFactory.Perks.Doctor, PerkFactory.Perks.Electricity, PerkFactory.Perks.Blacksmith, PerkFactory.Perks.MetalWelding, PerkFactory.Perks.Melting, PerkFactory.Perks.Mechanics,PerkFactory.Perks.Spear, PerkFactory.Perks.Maintenance, PerkFactory.Perks.SmallBlade, PerkFactory.Perks.LongBlade, PerkFactory.Perks.SmallBlunt, PerkFactory.Perks.Combat, PerkFactory.Perks.Tailoring,
    };

    int summOfFullPerks;
    String listOfBanned = "";
    static int countCheaters = 0;

    protected String Command() {
        Boolean exist = false;
        DebugLog.log("<Start of check>");
        for(int i = 0; i < GameServer.udpEngine.connections.size(); ++i) {
           UdpConnection connection = (UdpConnection)GameServer.udpEngine.connections.get(i);
           DebugLog.log("Check for: " + connection.username);
           IsoPlayer player = GameServer.getPlayerByUserNameForCommand(connection.username);
           summOfFullPerks = 0;

            if (!player.getAccessLevel().equals("admin")) {
                try {
                    checkMeleeWeapon(player, connection);
                    checkFireWeapon(player, connection);
                    checkPerfectPlayerStats(player, connection);
                    checkTraits(player, connection);
                    //getNearVehicle(player);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

           for (PerkFactory.Perk perk: listOfPerksTrue) {
               summOfFullPerks += player.getPerkLevel(perk);

               if ((summOfFullPerks >= 25 & getDaysSurvived(player) < 1) | (summOfFullPerks >= 60 & getDaysSurvived(player) < 3)) {
                   try {
                       countCheaters +=1;
                       ServerWorldDatabase.instance.banIp(connection.ip, connection.username, "Packet type 12 error", true);
                       DebugLog.log("Banned:  " + connection.username + " with IP " + connection.ip + " lvl's count " + summOfFullPerks);
                       for(int var5 = 0; var5 < GameServer.udpEngine.connections.size(); ++var5) {
                           UdpConnection var6 = (UdpConnection)GameServer.udpEngine.connections.get(var5);
                           if (var6.username.equals(player.getUsername())) {
                               GameServer.kick(var6, "Packet type 12 error", (String)null);
                           }}
                   } catch (SQLException e) {
                       throw new RuntimeException(e);
                   }
               }
           }
            DebugLog.log("<AC-Levels> Count "+ player.getUsername() + " 's skills: " + summOfFullPerks);
        }

        return "Забаненных: " + countCheaters;
    }

    public void checkPerfectPlayerStats(IsoPlayer player, UdpConnection connection) throws SQLException {
        int suspicionPoints = 0;
        if (player.getStats().getEndurance() == 1.0F) {
            suspicionPoints += 1;
        }

        if (player.getStats().getHunger() == 0.0F) {
            suspicionPoints += 1;
        }

        if (player.getStats().getThirst() == 0.0F) {
            suspicionPoints += 1;
        }

        /*
        if (this.isDisablePain & player.ge) {
            player.getStats().setPain(0.0F);
        }
        */

        if (player.getStats().getMorale() == 1.0F) {
            suspicionPoints += 1;
        }

        /*сделать проверку на сигераты + перк
        if (this.isDisableStressFromCigarettes) {
            player.getStats().setStressFromCigarettes(0.0F);
        }*/

        if (player.getBodyDamage().getBoredomLevel() == 0.0F) {
            suspicionPoints += 1;
        }

        if (player.getBodyDamage().getUnhappynessLevel() == 0.0f) {
            suspicionPoints += 1;
        }

        if (player.getBodyDamage().getWetness() == 0.0f) {
            suspicionPoints += 1;
        }

        if (player.getNutrition().getCalories() == 1200.0F) {
            suspicionPoints += 1;
        }

        if (player.getNutrition().getWeight() == 80.0F) {
            suspicionPoints += 1;
        }

        if (suspicionPoints == 9) {
            countCheaters +=1;
            DebugLog.log("<AC-PerfectStats>Banned for suspicionPoints:  " + connection.username);
            ServerWorldDatabase.instance.banIp(connection.ip, connection.username, "Хватит читерить", true);
            GameServer.kick(connection, "Хватит читерить", (String)null);
        }
    }

    public int getDaysSurvived(IsoPlayer player) {
        int hours = (int)player.getHoursSurvived();
        return (int) hours / 24;
    }

    public void checkFireWeapon (IsoPlayer player, UdpConnection connectionObj) throws SQLException {

        InventoryItem playerItem = player.getPrimaryHandItem();
        HandWeapon weapon;
        weapon = (HandWeapon)playerItem;


        if (playerItem != null & weapon != null && playerItem.getStringItemType().equals("RangedWeapon") && playerItem instanceof HandWeapon) {
            if (weapon.getMinDamage() >= 15.0f | weapon.getMaxDamage() >= 15.0f | weapon.getActualWeight() == 0f | weapon.getRecoilDelay() == 0 | weapon.getCriticalChance() == 100.0f | weapon.getAimingTime() ==0) {
                countCheaters +=1;
                DebugLog.log("<AC-Weapon>Kicked for fireweapon:  " + connectionObj.username);
                GameServer.kick(connectionObj, "У тебя оказалось в руках читерское оружие, избавься от него и сообщи админам, иначе...", (String)null);


            }
        }
    }

    public void checkMeleeWeapon(IsoPlayer player, UdpConnection connection) throws SQLException {

        InventoryItem playerItem = player.getPrimaryHandItem();
        HandWeapon weapon;
        weapon = (HandWeapon)playerItem;

        if (playerItem != null & weapon != null && (playerItem.getStringItemType().equals("RangedWeapon") || playerItem.getStringItemType().equals("MeleeWeapon")) && playerItem instanceof HandWeapon) {
            if (weapon.getActualWeight() == 0f |weapon.getMaxDamage() > 8.1f | weapon.getMinDamage() > 8.1f | weapon.getMaxRange() > 1.6f) {
                countCheaters +=1;
                DebugLog.log("<AC-Weapon>Kicked for hand weapon:  " + connection.username);
                GameServer.kick(connection, "Обнаружено читерское оружие у тебя в руках! Избавься от него и сообщи админам, иначе...", (String)null);
            }
        }
    }

    public void checkTraits(IsoPlayer player, UdpConnection connectionObj) throws SQLException {
        TraitFactory.Trait trait;
        TraitCollection traitCollection = player.getTraits();
        int traitsCost = 0;
        ProfessionFactory.Profession playerProfession = ProfessionFactory.getProfession(player.getDescriptor().getProfession());
        traitsCost = traitsCost + playerProfession.getCost();
        DebugLog.log("Профессия игрока "+ playerProfession.name + " отнимает по дефолту "+ playerProfession.getCost());


        int i = 0;

        for (i = 0; i < traitCollection.size(); i++) {

            trait = TraitFactory.getTrait(traitCollection.get(i));
            DebugLog.log("Перк: " + trait.name + " цена: " + trait.cost);
            traitsCost += trait.cost * (-1);
        }

        DebugLog.log("<AC-Perks>Сумма перков игрока: " +player.username + "-"+ traitsCost);
        if (traitsCost < 0) {
            countCheaters += 1;
            DebugLog.log("<AC-perks>Banned for trait: " + connectionObj.username);
            ServerWorldDatabase.instance.banIp(connectionObj.ip, connectionObj.username, "У тебя явно накручены перки, лучше прокачал бы IQ", true);
            GameServer.kick(connectionObj, "", (String)null);
        }
    }

    public void getNearVehicle(IsoPlayer player) {
        IsoChunk var0 = player.getChunk();
        for(int i = 0; i < 10; ++i) {
            for(int j = 0; j < 10; ++j) {
                IsoGridSquare var7 = var0.getGridSquare(j, i, 0);
                IsoMetaGrid.Zone var8 = var7 == null ? null : var7.getZone();
                if (var8 != null ) {
                    for(int k = 0; k < 8; ++k) {
                        var7 = var0.getGridSquare(j, j, k);
                        if (var7 != null) {
                            int var10 = var7.getObjects().size();
                            IsoObject[] var11 = (IsoObject[])var7.getObjects().getElements();

                            for(int var12 = 0; var12 < var10; ++var12) {
                                IsoObject var13 = var11[var12];
                                if (!(var13 instanceof IsoDeadBody) && !(var13 instanceof IsoThumpable) && !(var13 instanceof IsoCompost)) {
                                    for(int var14 = 0; var14 < var13.getContainerCount(); ++var14) {
                                        ItemContainer var15 = var13.getContainerByIndex(var14);
                                        ArrayList<InventoryItem> itemsInContainer = var15.getItems();
                                        for (zombie.inventory.InventoryItem item: itemsInContainer) {
                                            try {
                                                DebugLog.log(item.getActualWeight());
                                                DebugLog.log(item.getPreviousOwner());
                                                DebugLog.log(item.getRemoteRange());
                                                InventoryItem ime2 = (InventoryItem) item;
                                                HandWeapon newWeap = (HandWeapon) item;
                                                DebugLog.log(newWeap.getMaxRange());
                                            } catch (Exception e) {

                                            }

                                }
                            }}
                        }
                    }
                }
            }
        }
        }
    }
}
