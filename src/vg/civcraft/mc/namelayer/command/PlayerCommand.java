package vg.civcraft.mc.namelayer.command;

import org.bukkit.Bukkit;

import vg.civcraft.mc.namelayer.GroupManager;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.NameLayerPlugin;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.misc.Mercury;

public abstract class PlayerCommand implements Command{

	protected GroupManager gm = NameAPI.getGroupManager();
	private String name = "";
	private String description = "";
	private String usage = "";
	private String identifier = "";
	private int min = 0;
	private int max = 0;
	
	public PlayerCommand(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getUsage() {
		return usage;
	}

	public String getIdentifier() {
		return identifier;
	}

	public int getMinArguments() {
		return min;
	}

	public int getMaxArguments() {
		return max;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setDescription(String description){
		Bukkit.getPluginCommand(identifier).setDescription(description);
		this.description = description;
	}
	
	public void setUsage(String usage){
		Bukkit.getPluginCommand(identifier).setUsage(usage);
		this.usage = usage;
	}
	
	public void setIdentifier(String identifier){
		this.identifier = identifier;
	}
	
	public void setArguments(int min, int max){
		this.min = min;
		this.max = max;
	}
	
	public void checkRecacheGroup(Group g){
		if (NameLayerPlugin.isMercuryEnabled()){
			String message = "recache " + g.getName();
			Mercury.invalidateGroup(message);
		}
	}
}