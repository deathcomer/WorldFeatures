package me.dalton.worldfeatures;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.WorldListener;
import org.bukkit.util.config.Configuration;
import org.bukkit.World;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;

@SuppressWarnings("deprecation")
public final class mChunkNotifier extends WorldListener {
	Random rand=new Random();
	private File myfile;
	private Chunk chunky;
	private int bHeight;
	private World wrld;
	private int chunkX , chunkZ , randX , randZ;
	private int width = 0 , bredth = 0 , height = 0;
	private int rotation;
	CuboidClipboard cc;
	static Configuration settings;
	//private boolean loadedFromRAM = false;
	
	static{settings = load2();}
	
	public Configuration load(String s) {
		myfile= new File("plugins/WorldFeatures/ToUse/"+s);

	    Configuration PluginPropConfig = new Configuration(myfile);
	    PluginPropConfig.load();
	    return PluginPropConfig;
	}
	
	public static final File myfile2= new File("plugins/WorldFeatures/Settings.yml");
	public static Configuration load2() {
		try {
            Configuration PluginPropConfig = new Configuration(myfile2);
            PluginPropConfig.load();
            return PluginPropConfig;

        } catch (Exception e) {
        	
        }
        return null;
	}
	
    private void loadArea(World world , Vector origin){
        EditSession es = new EditSession(new BukkitWorld(world), 200000);
        try {cc.paste(es , origin, true);} 
        catch (MaxChangedBlocksException e) {e.printStackTrace();}
    }
	
	public Block loadBlockChunk(int x , int y , int z){
		return wrld.getBlockAt(chunkX+x,y,chunkZ+z);
	}
	
	public boolean cornerBlocksOr(int i){
		if(loadBlockChunk(randX       , bHeight , randZ)       .getTypeId() == i
		 ||loadBlockChunk(randX+width , bHeight , randZ)       .getTypeId() == i
		 ||loadBlockChunk(randX       , bHeight , randZ+bredth).getTypeId() == i
		 ||loadBlockChunk(randX+width , bHeight , randZ+bredth).getTypeId() == i)
			return true;
		return false;
	}
	
	public boolean cornerBlocksAnd(int i){
		if(loadBlockChunk(randX       , bHeight , randZ)       .getTypeId() == i
		 &&loadBlockChunk(randX+width , bHeight , randZ)       .getTypeId() == i
		 &&loadBlockChunk(randX       , bHeight , randZ+bredth).getTypeId() == i
		 &&loadBlockChunk(randX+width , bHeight , randZ+bredth).getTypeId() == i)
			return true;
		return false;
	}
	
	public void onChunkPopulate(ChunkPopulateEvent event) {
		
		if(rand.nextInt(100)+1 > settings.getInt("chunkchance", 5))
			return;
		
		String[]children = new File("plugins/WorldFeatures/ToUse").list();
		ArrayList<String> schemes = new ArrayList<String>();
		ArrayList<String> configs = new ArrayList<String>();

		for(int ab = 0 ; ab<children.length ; ab++){
			if(children[ab].substring(children[ab].indexOf('.')+1).equals("schematic"))
				schemes.add(children[ab]);
			else if(children[ab].substring(children[ab].indexOf('.')+1).equals("yml"))
				configs.add(children[ab]);
		}
		
		if (!(schemes != null && schemes.size()>0))
			return;

	    List<Integer> chosen = new ArrayList<Integer>();
	    
    	for(int x=0;x<configs.size();x++){
    		File fi= new File("plugins/WorldFeatures/ToUse/"+configs.get(x));
    	    Configuration Config = new Configuration(fi);
    	    Config.load();
    	    if(rand.nextInt(100)+1 <= Config.getDouble("info.chance", 50)){
    	    	chosen.add(x);
    	    }
    	}
    	
    	if(chosen.isEmpty())
    		return;
    	
		int haylea = chosen.get(rand.nextInt(chosen.size()));
		String schemeToConfig = schemes.get(haylea).substring(0 , schemes.get(haylea).indexOf('.'))+".yml";
    	Configuration derp = load(schemeToConfig);
    	
	    randX = rand.nextInt(16);
	    randZ = rand.nextInt(16);
	    boolean canSpawn = true;
		chunky = event.getChunk();
		chunkX = chunky.getX()*16;
		chunkZ = chunky.getZ()*16;
		wrld = chunky.getWorld();
		
    	int maxspawns = derp.getInt("info.maxspawns", 0);
    	
    	if(!(derp.getInt("dontEdit.spawns."+wrld.getName(), 0) < maxspawns || maxspawns == 0))
    		return;

    	//if(plug.schematics.containsKey(schemeToConfig)){
    	//	cc = plug.schematics.get(schemeToConfig);
		//	loadedFromRAM = true;
    	//}
    	//else{
    		try {
				cc = CuboidClipboard.loadSchematic(new File("plugins/WorldFeatures/ToUse/"+schemes.get(haylea)));
			} catch (DataException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}
    	//}
    	
    	rotation = rand.nextInt(4)*90;
    	cc.rotate2D(rotation);
    	
    	switch(rotation){
	    	case 0:
	    		width  = cc.getWidth();
				bredth = cc.getLength(); break;
	    	case 90:
	    		width  = 0 - cc.getWidth();
				bredth = cc.getLength(); break;
	    	case 180:
	    		width  = 0 - cc.getWidth();
				bredth = 0 - cc.getLength(); break;
	    	case 270:
				width  = cc.getWidth();
				bredth = 0 - cc.getLength(); break;
    	}

		height = cc.getHeight();
	    
	    String place = derp.getString("info.place","ground");
		
	    if(place.equals("anywhere")){
		    int minrange = derp.getInt("info.min", 1);
		    int maxrange = derp.getInt("info.max", 126)-derp.getInt("info.y", 0);
		    bHeight=rand.nextInt(maxrange-minrange)+1+minrange;
	    	if(bHeight>126-height){canSpawn=false;}
	    }
	    
	    else if(place.equals("ground")){
	    	bHeight=126;
	    	int base = derp.getInt("info.basement", 0);
	    	
	    	//air
	    	while(cornerBlocksOr(0)==true){ bHeight--;}
	    	
	    	//snow
	    	while(cornerBlocksOr(78)==true){ bHeight--;}
	    	
	    	//leaves
	    	while(cornerBlocksOr(18)==true){ bHeight--;}

	    	//if it is too high for its height
	    	if(bHeight>126-height+base){canSpawn=false;}
	    	
	    	//check if it is over water
	    	if(cornerBlocksOr(9)==true)
	    		canSpawn=false;
	    }
	    
	    else if(place.equals("air")){
	    	bHeight=126;

	    	//air
	    	while(cornerBlocksOr(0) == true) {bHeight--;}
	    	
	    	bHeight = rand.nextInt(126-bHeight)+1+bHeight;
	    	
	    	//if it is too high for its height
	    	while(bHeight > 126-height){bHeight--;}
	    	
	    	canSpawn = false;
	    	
	    	//check if it's still in air
	    	if(cornerBlocksAnd(0)==true)
	    		canSpawn = true;
	    }
	    
	    else if(place.equals("underground")){
	    	bHeight=1;

	    	//air
	    	while(loadBlockChunk(randX       , bHeight+1+height , randZ)         .getTypeId()!=0){bHeight++;}
	    	while(loadBlockChunk(randX+width , bHeight+1+height , randZ)         .getTypeId()!=0){bHeight++;}
	    	while(loadBlockChunk(randX       , bHeight+1+height , randZ + bredth).getTypeId()!=0){bHeight++;}
	    	while(loadBlockChunk(randX+width , bHeight+1+height , randZ + bredth).getTypeId()!=0){bHeight++;}
	    	
	    	bHeight = rand.nextInt(bHeight)+1;
	    	
	    	//if it is too high for its height
	    	while(bHeight>126-height){bHeight--;}
	    	
	    	canSpawn = false;
	    	
	    	if(loadBlockChunk(randX       , bHeight+1+height , randZ)         .getTypeId()!=0 
	    	&& loadBlockChunk(randX+width , bHeight+1+height , randZ)         .getTypeId()!=0 
	    	&& loadBlockChunk(randX       , bHeight+1+height , randZ + bredth).getTypeId()!=0 
	    	&& loadBlockChunk(randX+width , bHeight+1+height , randZ + bredth).getTypeId()!=0)
	    		canSpawn = true;
	    }
	    					    
	    if(canSpawn == false)
	    	return;
	    
    	loadArea(wrld , new Vector(chunkX+randX , bHeight + 1 - derp.getInt("info.basement", 0) , chunkZ + randZ));
    
    	derp.setProperty("dontEdit.spawns."+wrld.getName(), derp.getInt("dontEdit.spawns."+wrld.getName(), 0) + 1);
    	derp.save();
    	
	    //if(loadedFromRAM == true){
	    //	cc.rotate2D(360-rotation);
	    //}
	}
}