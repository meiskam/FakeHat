/* FakeHat - Bukkit plugin
 * Copyright (C) 2012 meiskam
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.shininet.bukkit.fakehat;

import java.util.logging.Level;
import java.util.logging.Logger;

//import net.minecraft.server.NBTTagCompound;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;

public class EntityEquipmentListener {

	private final Server server;
	private final Logger logger;

	public EntityEquipmentListener(Server server, Logger logger) {
		this.server = server;
		this.logger = logger;
	}
	
	public void addListener(final ProtocolManager protocolManager, final JavaPlugin myPlugin) {

		protocolManager.addPacketListener(new PacketAdapter(myPlugin, ConnectionSide.SERVER_SIDE, 0x05, 0x14) {
			@Override
			public void onPacketSending(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				
				try {
					switch (event.getPacketID()) {
					case 0x05:
						if (packet.getSpecificModifier(int.class).size() >= 2 && packet.getItemModifier().size() >= 1) {
							int id = packet.getSpecificModifier(int.class).read(0);
							int slot = packet.getSpecificModifier(int.class).read(1);
							if (slot == 4) {
								ItemStack item = packet.getItemModifier().read(0);
								if (item == null) {
									event.setCancelled(true);
									return;
								}
								for (Player player : myPlugin.getServer().getOnlinePlayers()) {
									//logger.info(player.getEntityId()+" == "+id+" ?");
									if (player.getEntityId() == id) {
										//ItemStack item = packet.getItemModifier().read(0);
										item.setTypeId(((FakeHat)myPlugin).hat);
										item.setDurability(((FakeHat)myPlugin).damage);
										item.setAmount(1);
										//item = new ItemStack(((FakeHat)plugin).hat, 1, ((FakeHat)plugin).damage);
										//logger.info("id: "+id+" .. slot: "+slot+" .. item: "+item.toString());
										break;
									}
								}
							}
						}
						break;
						
					case 0x14:
						if (packet.getSpecificModifier(int.class).size() >= 1) {
							server.getScheduler().scheduleSyncDelayedTask(myPlugin, 
									new FakePacketEntityEquipment(myPlugin, protocolManager, event.getPlayer(), packet.getSpecificModifier(int.class).read(0)) );
							
							//new FakePacketEntityEquipment(protocolManager, event.getPlayer(), packet.getSpecificModifier(int.class).read(0))
							//((FakeHat)plugin).fakeEntityEquipment(event.getPlayer(), packet.getSpecificModifier(int.class).read(0));
						}
						break;
						
//					case 0x00:
//						break;
					}
				
				} catch (FieldAccessException e) {
					logger.log(Level.SEVERE, "Couldn't access field.", e);
				}
			}
		});
	}
}
