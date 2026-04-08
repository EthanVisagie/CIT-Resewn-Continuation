package shcm.shsupercm.fabric.citresewn.defaults.mixin.types.armor;

import net.minecraft.client.render.entity.equipment.EquipmentModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.render.entity.equipment.EquipmentRenderer$LayerTextureKey")
public interface LayerTextureKeyAccessor {
    @Accessor("layer")
    EquipmentModel.Layer citresewn$layer();
}
