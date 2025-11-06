package ca.maximilian.bluemap_viewer.neoforge;

import net.neoforged.fml.common.Mod;

import ca.maximilian.bluemap_viewer.ExampleMod;

@Mod(ExampleMod.MOD_ID)
public final class ExampleModNeoForge {
    public ExampleModNeoForge() {
        // Run our common setup.
        ExampleMod.init();
    }
}
