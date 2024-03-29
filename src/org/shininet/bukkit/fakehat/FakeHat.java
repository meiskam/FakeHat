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

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

public class FakeHat extends JavaPlugin {

	private ProtocolManager protocolManager;
	private EntityEquipmentListener listener;
	
	public FileConfiguration config;
	public int hat;
	public short damage;
	
	public void initConfig(boolean copyDefaults) {
		config = getConfig();
		if (copyDefaults) {
			config.options().copyDefaults(true);
			saveDefaultConfig();
		}
		hat = config.getInt("hat", 86);
		damage = (short)config.getInt("damage", 0);
	}
/*	
	public void fakeEntityEquipment(Player player, int uid) {
		//this.getLogger().info("making a fake 0x05");
		PacketContainer packet = protocolManager.createPacket(0x05);
		 
		try {
			packet.getSpecificModifier(int.class).write(0, uid).write(1, 4);
			//packet.getSpecificModifier(ItemStack.class).write(0, new ItemStack(hat, 1, damage));
			packet.getItemModifier().write(0, new ItemStack(hat, 1, damage));
			
			protocolManager.sendServerPacket(player, packet);
		} catch (Exception e) {
			getLogger().log(Level.SEVERE, "Couldn't send fake EntityEquipment packet.", e);
		}
	}
*/
	
	@Override
	public void onEnable(){
		initConfig(true);
		
		protocolManager = ProtocolLibrary.getProtocolManager();
		
		listener = new EntityEquipmentListener(getServer(), getLogger());
		listener.addListener(protocolManager, this);
	}
	
	@Override
	public void onDisable() {
		protocolManager.removePacketListeners(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (cmd.getName().equalsIgnoreCase("FakeHat")) {
			if (args.length == 0) {
				sender.sendMessage("["+label+"] Subcommands: config");
			} else {
				if (args[0].equalsIgnoreCase("config")) {
					if (args.length == 1) {
						sender.sendMessage("["+label+":config] Subcommands: get, set, reload");
					} else {
						if (args[1].equalsIgnoreCase("get") || args[1].equalsIgnoreCase("view")) {
							if (sender.hasPermission("fakehat.config.get")) {
								if (args.length == 2) {
									sender.sendMessage("["+label+":config:get] Config variables: Hat, Damage");
								} else if (args.length == 3) {
									sender.sendMessage("["+label+":config:get] "+args[2].toLowerCase()+": "+config.get(args[2].toLowerCase()));
								} else {
									sender.sendMessage("["+label+":config:get] Too many params!");
								}
							} else {
								sender.sendMessage("["+label+":config:get] You don't have permission to use that command");
							}
						} else if (args[1].equalsIgnoreCase("set")) {
							if (sender.hasPermission("fakehat.config.set")) {
								if (args.length == 2 || args.length == 3) {
									sender.sendMessage("["+label+":config:set] Config variables: Hat, Damage");
								} else if (args.length == 4) {
									if (args[2].equalsIgnoreCase("hat")) {
										try {
											config.set("hat", Integer.parseInt(args[3]));
										} catch (NumberFormatException e) {
											sender.sendMessage("["+label+":config:set] ERROR: Can not convert "+args[3].toLowerCase()+" to a number");
										}
									} else if (args[2].equalsIgnoreCase("damage")) {
										try {
											config.set("damage", Integer.parseInt(args[3]));
										} catch (NumberFormatException e) {
											sender.sendMessage("["+label+":config:set] ERROR: Can not convert "+args[3].toLowerCase()+" to a number");
										}
									} else {
										config.set(args[2].toLowerCase(), args[3]);
									}
									saveConfig();
									sender.sendMessage("["+label+":config:set] "+args[2].toLowerCase()+": "+config.get(args[2].toLowerCase()));
								} else {
									sender.sendMessage("["+label+":config:set] Too many params!");
								}
							} else {
								sender.sendMessage("["+label+":config:set] You don't have permission to use that command");
							}
						} else if (args[1].equalsIgnoreCase("reload")) {
							if (sender.hasPermission("fakehat.config.set")) {
								reloadConfig();
								initConfig(false);
								sender.sendMessage("["+label+":config:reload] Config reloaded");
							} else {
								sender.sendMessage("["+label+":config:reload] You don't have permission to use that command");
							}
						} else {
							sender.sendMessage("["+label+":config:??] Invalid subcommand");
						}
					}

/*				} else if (args[0].equalsIgnoreCase("somethingelse")) {
					sender.sendMessage("["+label+":??] moo");
*/				} else {
					sender.sendMessage("["+label+":??] Invalid subcommand");
				}
			}
			return true;
		}
		return false; 
	}
}
