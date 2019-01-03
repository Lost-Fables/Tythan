package co.lotc.core.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

import co.lotc.core.Tythan;
import co.lotc.core.TythanCommon;
import lombok.Getter;

public class TythanBukkit extends JavaPlugin implements Tythan {
	private final TythanCommon common = new TythanCommon(this);
	@Getter private boolean debugging;
	
	@Override
	public void onLoad() {
		common.onLoad();
	}
	
	@Override
	public void onEnable(){
		
	}

	@Override
	public void onDisable(){

	}
}
