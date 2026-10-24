package com.gtnh.dualoutputhatchme;

import com.gtnh.dualoutputhatchme.registration.Registration;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        Registration.registerMetaTileEntities();
    }

    public void init(FMLInitializationEvent event) {}

    public void postInit(FMLPostInitializationEvent event) {}

    public void loadComplete(FMLLoadCompleteEvent event) {
        // GTNH 的容量升级配方在 postInit 注册完成后才能完整读取。
        Registration.registerRecipes();
    }
}
