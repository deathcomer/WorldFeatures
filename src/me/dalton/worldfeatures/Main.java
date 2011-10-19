package me.dalton.worldfeatures;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;

import static java.nio.file.StandardCopyOption.*;

@SuppressWarnings("deprecation")
public class Main extends JavaPlugin{
	
	private File myfile2;
	public static final File myfile= new File("plugins/WorldFeatures/Settings.yml");
	public final Logger logger = Logger.getLogger("Minecraft");
    public PluginManager pm = null;
    Configuration settings;
    Configuration customLoad;

	public Configuration load() {
        try {
            Configuration PluginPropConfig = new Configuration(myfile);
            PluginPropConfig.load();
            return PluginPropConfig;
        } catch (Exception e) {}
        return null;
	}
	
	public Configuration customLoad(String s) {
		myfile2= new File("plugins/WorldFeatures/Created/"+s+".yml");
		Configuration PluginPropConfig = new Configuration(myfile2);
	    PluginPropConfig.load();
		
	    return PluginPropConfig;
	}
	
    private void saveArea(World world,Vector origin,Vector size,File file){
        EditSession es = new EditSession(new BukkitWorld(world), 200000);
    	CuboidClipboard cc = new CuboidClipboard(origin,size);
        cc.copy(es);
        try {cc.saveSchematic(file);} catch (IOException e) {e.printStackTrace();} catch (DataException e) {e.printStackTrace();}
    }
    
	public void onDisable() {
		this.logger.info("WorldFeatures Disabled");
	    pm = null;       
	}
		
	public void onEnable() {
		pm = getServer().getPluginManager();
		
		//set defaults
		settings = load();
		settings.getInt("wandID" , 295);
		settings.getInt("chunkchance" , 2);
		settings.save();
		
		//create folders
		new File("plugins/WorldFeatures/Created").mkdir();
		new File("plugins/WorldFeatures/ToUse").mkdir();
		
		//set schematics to RAM
		/*
		File dir = new File("plugins/WorldFeatures/ToUse");
		
		
		String[]children = dir.list();
		ArrayList<String> schemes = new ArrayList<String>();
		for(int ab = 0 ; ab<children.length ; ab++)
			if(children[ab].substring(children[ab].indexOf('.')+1).equals("schematic"))
				schemes.add(children[ab]);
		
		
		ArrayList<String> configs = new ArrayList<String>();
		for(int ab = 0 ; ab<children.length ; ab++)
			if(children[ab].substring(children[ab].indexOf('.')+1).equals("yml"))
				configs.add(children[ab]);
		
		if(!schemes.isEmpty()){
		
			for(int x=0;x<configs.size();x++){
				File fi= new File("plugins/WorldFeatures/ToUse/"+configs.get(x));
			    Configuration Config = new Configuration(fi);
			    Config.load();
			    //updates
			    Config.getBoolean("info.loadToRAM" , false);
			    Config.save();
			    
			    if(Config.getBoolean("info.loadToRAM" , false) == true){
			    	try {
						schematics.put(configs.get(x) , CuboidClipboard.loadSchematic(new File("plugins/WorldFeatures/ToUse/"+configs.get(x).substring(0 , configs.get(x).indexOf('.'))+".schematic")));
					} catch (DataException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}
			    }
			}
		}
		*/
		
		//pm stuff
		mPlayerListener mplayerlistener = new mPlayerListener(this);
		mChunkNotifier notify = new mChunkNotifier();
		
		pm.registerEvent(Event.Type.CHUNK_POPULATED, notify, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, mplayerlistener, Event.Priority.Normal, this);
		
		PluginDescriptionFile pdfFile =this.getDescription();
		this.logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
	
	public final HashMap<Player, Location> point1 = new HashMap<Player,Location>();
	public final HashMap<Player, Location> point2 = new HashMap<Player,Location>();
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player playa = (Player)sender;
		
	    if(cmd.getName().equalsIgnoreCase("wf")||cmd.getName().equalsIgnoreCase("worldfeatures")){
	    	if (playa.hasPermission("WorldFeatures.commands") || playa.isOp()){
		    	if(args.length == 1 && args[0] != null && args[0] != "") {
		
					if(point1.get(playa) != null){
		    			if(point2.get(playa) != null){
		    	    		Configuration cusLoad = customLoad(args[0]);
		    	    		int loc1X=point1.get(playa).getBlockX() , loc1Y=point1.get(playa).getBlockY() , loc1Z=point1.get(playa).getBlockZ(),
		    	    		    loc2X=point2.get(playa).getBlockX() , loc2Y=point2.get(playa).getBlockY() , loc2Z=point2.get(playa).getBlockZ();
		    	    		
		    	    	    int minx = Math.min(loc1X, loc2X),
		    	    	    	miny = Math.min(loc1Y, loc2Y),
		    	    	    	minz = Math.min(loc1Z, loc2Z),
		    	    	        maxx = Math.max(loc1X, loc2X),
		    	    	        maxy = Math.max(loc1Y, loc2Y),
		    	    	        maxz = Math.max(loc1Z, loc2Z);
		    	    		
		    	    		cusLoad.setProperty("info.place", "ground");
		    	    		cusLoad.setProperty("info.maxspawns", 0);
		    	    		//cusLoad.setProperty("info.loadToRAM", false);
		    	    		cusLoad.setProperty("info.chance", 50);
		    	    		cusLoad.setProperty("info.basement", 0);
		    	    		cusLoad.setProperty("info.min", 1);
		    	    		cusLoad.setProperty("info.max", 126);
		    	    		cusLoad.setProperty("info.randomrotate", true);
		    	    		cusLoad.setProperty("info.biome", "none");
		    	    		
		    	    		cusLoad.save();
		    	    		
		    	    		Vector min = new Vector(minx , miny , minz);
		    	    		Vector max = new Vector(maxx , maxy , maxz);
		    	    		
		    	    		File schFile= new File("plugins/WorldFeatures/Created/"+args[0]+".schematic");
							saveArea(playa.getWorld(), max.subtract(min).add(new Vector(1,1,1)) , min , schFile);
		    	    	    
		    	    	    cusLoad.save();
		        			playa.sendMessage(ChatColor.YELLOW+"You saved this in the Created folder as the file: "+args[0]);
		    				return true;
		    			}
		    			else playa.sendMessage(ChatColor.YELLOW+"You need a corner! To select it, wield "+Material.getMaterial(settings.getInt("wandID",295)).toString()+"".toLowerCase().replace("_"," ")+" and right click.");
		    			return true;
					}
					else playa.sendMessage(ChatColor.YELLOW+"You need a corner! To select it, wield "+Material.getMaterial(settings.getInt("wandID",295)).toString()+"".toLowerCase().replace("_"," ")+" and left click.");
					return true;
		    		
		    	}
		    	
		    	else if(args.length == 2 && args[0] != null && args[0] != "" && args[1] != null && args[1] != "") {
		    		if(args[0].startsWith("c") || args[0].startsWith("C")){
		    			try {
		    				new File("plugins/WorldFeatures/ToUse/"+playa.getWorld().getName()).mkdir();
							Files.copy(new File ("plugins/WorldFeatures/Created/"+args[1]+".yml").toPath(), new File ("plugins/WorldFeatures/ToUse/"+playa.getWorld().getName()+"/"+args[1]+".yml").toPath(), REPLACE_EXISTING);
							Files.copy(new File ("plugins/WorldFeatures/Created/"+args[1]+".schematic").toPath(), new File ("plugins/WorldFeatures/ToUse/"+playa.getWorld().getName()+"/"+args[1]+".schematic").toPath(), REPLACE_EXISTING);
							playa.sendMessage(ChatColor.YELLOW+"Successfully copied "+args[1]+".");
						} catch (IOException e) {
							playa.sendMessage(ChatColor.YELLOW+"Something went wrong!");
						}
		    			return true;
		    		}
		    	}
		
				playa.sendMessage(ChatColor.YELLOW+"/wf <name>:"+ChatColor.WHITE+" Creates a schematic named <name> of the selected cuboid.");
				playa.sendMessage(ChatColor.YELLOW+"/wf copy <name>:"+ChatColor.WHITE+" Copies and pastes this schematic from your Created folder into your ToUse folder.");
		
		    	return true;
	    	}else{
	    		playa.sendMessage(ChatColor.YELLOW+"You do not have permission to do that.");
	    		return true;
	    	}
	    }
	    return false;
	}
}


