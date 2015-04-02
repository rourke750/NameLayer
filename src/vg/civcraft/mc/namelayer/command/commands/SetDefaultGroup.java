package vg.civcraft.mc.namelayer.command.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.namelayer.GroupManager.PlayerType;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.command.PlayerCommand;
import vg.civcraft.mc.namelayer.command.TabCompleters.GroupTabCompleter;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.permission.GroupPermission;
import vg.civcraft.mc.namelayer.permission.PermissionType;

public class SetDefaultGroup extends PlayerCommand{

	public SetDefaultGroup(String name) {
		super(name);
		setIdentifier("nlsdg");
		setDescription("Set or change a default group");
		setUsage("/nlsdg <group>");
		setArguments(1,1);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)){
			sender.sendMessage("I don't think you need to do that.");
			return true;
		}
		Player p = (Player) sender;
		UUID uuid = NameAPI.getUUID(p.getName());
		Group g = gm.getGroup(args[0]);
		if (g == null){
			p.sendMessage(ChatColor.RED + "That group does not exist.");
			return true;
		}
		
		PlayerType pType = g.getPlayerType(uuid);
		if (pType == null){
			p.sendMessage(ChatColor.RED + "You do not have access to that group.");
			return true;
		}
		
		GroupPermission gPerm = gm.getPermissionforGroup(g);
		if (!gPerm.isAccessible(pType, PermissionType.BLOCKS)){
			p.sendMessage(ChatColor.RED + "You do not have permission to default that group.");
			return true;
		}

		String x = gm.getDefaultGroup(uuid);
		if(x == null){
			g.setDefaultGroup(uuid);
			p.sendMessage(ChatColor.GREEN + "You have set your default group to " + g.getName());
		}
		else{
			g.changeDefaultGroup(uuid);
			p.sendMessage(ChatColor.GREEN + "You changed your default group from " + x + " to " + gm.getDefaultGroup(uuid));
		}
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (!(sender instanceof Player))
			return null;

		if (args.length == 1)
			return GroupTabCompleter.complete(args[0], PermissionType.BLOCKS, (Player) sender);
		else{
			return GroupTabCompleter.complete(null, PermissionType.BLOCKS, (Player)sender);
		}
	}
}
