package me.dalton.worldfeatures;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.util.config.Configuration;

@SuppressWarnings("deprecation")
public class mPlayerListener extends PlayerListener {
	
	private Main plugin;
	
	public mPlayerListener(Main plugin) {
		this.plugin = plugin;
	}
	
	public static final File myfile= new File("plugins/WorldFeaturesSettings.yml");
	public Configuration load() {
        try {
            Configuration PluginPropConfig = new Configuration(myfile);
            PluginPropConfig.load();
            return PluginPropConfig;
        } catch (Exception e) {}
        return null;
	}
	
	public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        Configuration derp=load();
        
        if(p.getItemInHand().getTypeId() != derp.getInt("wandID",295))
        	return;
        
        if(!(p.hasPermission("WorldFeatures.commands") || p.isOp()))
        		return;
        
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
    		this.plugin.point2.put(p, event.getClickedBlock().getLocation());
    		p.sendMessage(ChatColor.YELLOW+"Second corner set: "+this.plugin.point2.get(p).getBlockX()+", "
    				+(int)this.plugin.point2.get(p).getBlockY()+", "+this.plugin.point2.get(p).getBlockZ());
        }
        
        else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
    		this.plugin.point1.put(p, event.getClickedBlock().getLocation());
    		p.sendMessage(ChatColor.YELLOW+"First corner set: " + this.plugin.point1.get(p).getBlockX()+", "
    				+this.plugin.point1.get(p).getBlockY()+", " + this.plugin.point1.get(p).getBlockZ());
        }
	}
}


