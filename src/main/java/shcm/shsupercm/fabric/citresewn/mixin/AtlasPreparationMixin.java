package shcm.shsupercm.fabric.citresewn.mixin;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TextureAtlas.class)
public abstract class AtlasPreparationMixin {

    @ModifyVariable(method = "getSprite", argsOnly = true, at = @At("HEAD"))
    private Identifier citresewn$unwrapTexturePaths(Identifier id) {
        if (id.getPath().endsWith(".png")) {
            id = id.withPath(path -> path.substring(0, path.length() - 4));

            if (id.getPath().startsWith("textures/"))
                id = id.withPath(path -> path.substring(9));
        }
        return id;
    }
}
