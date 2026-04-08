package shcm.shsupercm.fabric.citresewn.defaults.mixin.types.armor;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import shcm.shsupercm.fabric.citresewn.cit.CIT;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeArmor;

import java.util.function.Function;

import static shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeArmor.CONTAINER;

@Mixin(EquipmentRenderer.class)
public class EquipmentRendererMixin {
    @WrapOperation(
            method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/util/Identifier;II)V",
            at = @At(value = "INVOKE", target = "Ljava/util/function/Function;apply(Ljava/lang/Object;)Ljava/lang/Object;")
    )
    private Object citresewn$replaceArmorTexture(
            Function<Object, Object> function,
            Object key,
            Operation<Object> original,
            EquipmentModel.LayerType layerType,
            RegistryKey<?> assetKey,
            Model<?> model,
            Object state,
            ItemStack stack,
            net.minecraft.client.util.math.MatrixStack matrices,
            OrderedRenderCommandQueue commandQueue,
            int light,
            Identifier texture,
            int outlineColor,
            int batchingIndex
    ) {
        Object originalValue = original.call(function, key);
        if (!CONTAINER.active() || !(originalValue instanceof Identifier originalTexture))
            return originalValue;

        if (layerType != EquipmentModel.LayerType.HUMANOID && layerType != EquipmentModel.LayerType.HUMANOID_LEGGINGS)
            return originalTexture;

        CIT<TypeArmor> cit = CONTAINER.getCIT(new CITContext(stack, null, null));
        if (cit == null)
            return originalTexture;

        EquipmentModel.Layer layer = ((LayerTextureKeyAccessor) key).citresewn$layer();
        Identifier replacement = cit.type.getTexture(layerType, layer, originalTexture);
        return replacement != null ? replacement : originalTexture;
    }
}
