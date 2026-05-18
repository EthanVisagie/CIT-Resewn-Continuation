package shcm.shsupercm.fabric.citresewn.defaults.mixin.types.armor;

import net.minecraft.client.resources.model.EquipmentClientInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer$LayerTextureKey")
public interface LayerTextureKeyAccessor {
    @Accessor("layer")
    EquipmentClientInfo.Layer citresewn$layer();
}
