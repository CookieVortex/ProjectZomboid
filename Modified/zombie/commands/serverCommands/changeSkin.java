package zombie.commands.serverCommands;


import zombie.characters.IsoPlayer;

import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.logger.ExceptionLogger;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.HandWeapon;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.scripting.ScriptManager;

@CommandName(
        name = "changeSkin"
)

@CommandHelp(
        helpText = "Гыгы, живи"
)
@RequiredRight(
        requiredRights = 60
)
public class changeSkin extends CommandBase {
    public changeSkin(String var1, String var2, String var3, UdpConnection var4) {
        super(var1, var2, var3, var4);
    }


    protected String Command() {
        ByteBufferWriter buf = this.connection.startPacket();
        PacketTypes.PacketType.HumanVisual.doPacket(buf);
        IsoPlayer player = GameServer.getPlayerByUserNameForCommand(this.getExecutorUsername());
        InventoryItem var8 = (HandWeapon)InventoryItemFactory.CreateItem("Base.Axe");
        player.getInventory().addItem(var8);

        return "готово: ";
    }


}
