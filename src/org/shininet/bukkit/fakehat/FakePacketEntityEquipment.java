package org.shininet.bukkit.fakehat;

import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;

public class FakePacketEntityEquipment implements Runnable {
	private Plugin plugin;
	private ProtocolManager protocolManager;
	private Player player;
	private int uid;
	
	public FakePacketEntityEquipment(Plugin plugin, ProtocolManager protocolManager, Player player, int uid) {
		this.plugin = plugin;
		this.protocolManager = protocolManager;
		this.player = player;
		this.uid = uid;
	}

	@Override
	public void run() {
		PacketContainer packet = protocolManager.createPacket(0x05);
		 
		try {
			packet.getSpecificModifier(int.class).write(0, uid).write(1, 4);
			//packet.getSpecificModifier(ItemStack.class).write(0, new ItemStack(hat, 1, damage));
			packet.getItemModifier().write(0, new ItemStack(((FakeHat)plugin).hat, 1, ((FakeHat)plugin).damage));
			
			protocolManager.sendServerPacket(player, packet);
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Couldn't send fake EntityEquipment packet.", e);
		}
	}
	

}
