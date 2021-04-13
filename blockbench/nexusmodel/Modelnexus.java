// Made with Blockbench 3.6.6
// Exported for Minecraft version 1.12.2 or 1.15.2 (same format for both) for entity models animated with GeckoLib
// Paste this class into your mod and follow the documentation for GeckoLib to use animations. You can find the documentation here: https://github.com/bernie-g/geckolib
// Blockbench plugin created by Gecko
package com.example.mod;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.animation.model.AnimatedEntityModel;
import software.bernie.geckolib.animation.render.AnimatedModelRenderer;

public class Modelnexus extends AnimatedEntityModel<Entity> {

    

    public Modelnexus()
    {
        textureWidth = 128;
    textureHeight = 128;
    

    
  }


    @Override
    public ResourceLocation getAnimationFileLocation()
    {
        return new ResourceLocation("MODID", "animations/ANIMATIONFILE.json");
    }
}