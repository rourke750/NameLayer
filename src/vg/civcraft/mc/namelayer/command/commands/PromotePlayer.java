package vg.civcraft.mc.namelayer.command.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.namelayer.GroupManager.PlayerType;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.command.PlayerCommand;
import vg.civcraft.mc.namelayer.command.TabCompleters.GroupTabCompleter;
import vg.civcraft.mc.namelayer.command.TabCompleters.InviteTabCompleter;
import vg.civcraft.mc.namelayer.command.TabCompleters.MemberTypeCompleter;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.listeners.PlayerListener;
import vg.civcraft.mc.namelayer.permission.GroupPermission;
import vg.civcraft.mc.namelayer.permission.PermissionType;

import vg.civcraft.mc.namelayer.events.PromotePlayerEvent;

public class PromotePlayer extends PlayerCommand{

	public PromotePlayer(String name) {
		super(name);
		setIdentifier("nlpp");
		setDescription("Promote/Demote a Player in a Group");
		setUsage("/nlpp <group> <player> <playertype>");
		setArguments(3,3);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)){
			sender.sendMessage("How about No?");
			return true;
		}
		
		Player p = (Player) sender;
		
		UUID executor = NameAPI.getUUID(p.getName());
		
		UUID promotee = NameAPI.getUUID(args[1]);
		
		if(promotee ==null){
			p.sendMessage(ChatColor.RED + "That player does not exist");
			return true;
		}
		
		if(promotee.equals(executor)){
			p.sendMessage(ChatColor.RED + "You cannot promote yourself");
			return true;
		}
		
		Group group = gm.getGroup(args[0]);
		if(group == null){
			p.sendMessage(ChatColor.RED + "That group does not exist");
			return true;
		}
		if (group.isDisciplined()){
			p.sendMessage(ChatColor.RED + "This group is disiplined.");
			return true;
		}
		
		PlayerType promoteecurrentType = group.getPlayerType(promotee);
		PlayerType promoteeType = PlayerType.getPlayerType(args[2]);
		if(promoteeType == null){
			PlayerType.displayPlayerTypes(p);
			return true;
		}
		
		GroupPermission perm = gm.getPermissionforGroup(group);
		PlayerType t = group.getPlayerType(executor); // playertype for the player running the command.
		
		if (t == null){
			p.sendMessage(ChatColor.RED + "You are not on that group.");
			return true;
		}
		
		boolean allowed = false;
		switch (promoteeType){ // depending on the type the executor wants to add the player to
		case MEMBERS:
			allowed = perm.isAccessible(t, PermissionType.MEMBERS);
			break;
		case MODS:
			allowed = perm.isAccessible(t, PermissionType.MODS);
			break;
		case ADMINS:
			allowed = perm.isAccessible(t, PermissionType.ADMINS);
			break;
		case OWNER:
			allowed = perm.isAccessible(t, PermissionType.OWNER);
			break;
		default:
			allowed = false;
			break;
		}
		
		if (!allowed){
			p.sendMessage(ChatColor.RED + "You do not have permissions to modify this group.");
			return true;
		}
		
		if (!group.isMember(promotee)){ //can't edit a player who isn't in the group
			p.sendMessage(ChatColor.RED + NameAPI.getCurrentName(promotee) + " is not a member of this group.");
			return true;
		}
		
		OfflinePlayer prom = Bukkit.getOfflinePlayer(promotee);
		if(prom.isOnline()){
			Player oProm = (Player) prom;
			PromotePlayerEvent event = new PromotePlayerEvent(oProm, group, promoteecurrentType, promoteeType);
			Bukkit.getPluginManager().callEvent(event);
			if(event.isCancelled()){
				return false;
			}
			group.removeMember(promotee);
			group.addMember(promotee, promoteeType);
			p.sendMessage(ChatColor.GREEN + NameAPI.getCurrentName(promotee) + " has been added as (PlayerType) " +
					promoteeType.toString() + " in (Group) " + group.getName());
			oProm.sendMessage(ChatColor.GREEN + "You have been promoted to (PlayerType) " +
					promoteeType.toString() + " in (Group) " + group.getName());
		}
		else{
			//player is offline change their perms
			group.removeMember(promotee);
			group.addMember(promotee, promoteeType);
			p.sendMessage(ChatColor.GREEN + NameAPI.getCurrentName(promotee) + " has been added as (PlayerType) " +
					promoteeType.toString() + " in (Group) " + group.getName());
		}
		checkRecacheGroup(group);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "I'm sorry baby, please run this as a player :)");
			return null;
		}
		if (args.length < 2) {
			if (args.length == 0)
				return GroupTabCompleter.complete(null, null, (Player) sender);
			else
				return GroupTabCompleter.complete(args[0], null, (Player)sender);

		} else if (args.length == 2)
			return null;

		else if (args.length == 3)
			return MemberTypeCompleter.complete(args[2]);

		else return null;
	}

}
