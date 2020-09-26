package me.leafs.boiler;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = Boilerplate.MODID, version = Boilerplate.VERSION)
public class Boilerplate {
    public static final String MODID = "boilerplate";
    public static final String VERSION = "1.0";

    @Mod.Instance
    public static Boilerplate instance;

    @EventHandler
    public void init(FMLInitializationEvent event) {
    }
}
