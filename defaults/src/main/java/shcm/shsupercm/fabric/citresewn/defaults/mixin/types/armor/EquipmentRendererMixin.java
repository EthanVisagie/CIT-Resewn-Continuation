package shcm.shsupercm.fabric.citresewn.defaults.mixin.types.armor;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import shcm.shsupercm.fabric.citresewn.cit.CIT;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeArmor;

import java.util.function.Function;

import static shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeArmor.CONTAINER;

@Mixin(EquipmentLayerRenderer.class)
public class EquipmentRendererMixin {
    @WrapOperation(
            method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;II)V",
            at = @At(value = "INVOKE", target = "Ljava/util/function/Function;apply(Ljava/lang/Object;)Ljava/lang/Object;")
    )
    private Object citresewn$replaceArmorTexture(
            Function<Object, Object> function,
            Object key,
            Operation<Object> original,
            EquipmentClientInfo.LayerType layerType,
            ResourceKey<?> assetKey,
            Model<?> model,
            Object state,
            ItemStack stack,
            com.mojang.blaze3d.vertex.PoseStack matrices,
            SubmitNodeCollector commandQueue,
            int light,
            Identifier texture,
            int outlineColor,
            int batchingIndex
    ) {
        Object originalValue = original.call(function, key);
        if (!CONTAINER.active() || !(originalValue instanceof Identifier originalTexture))
            return originalValue;

        if (layerType != EquipmentClientInfo.LayerType.HUMANOID && layerType != EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS)
            return originalTexture;

        CIT<TypeArmor> cit = CONTAINER.getCIT(new CITContext(stack, null, null));
        if (cit == null)
            return originalTexture;

        EquipmentClientInfo.Layer layer = ((LayerTextureKeyAccessor) key).citresewn$layer();
        Identifier replacement = cit.type.getTexture(layerType, layer, originalTexture);
        return replacement != null ? replacement : originalTexture;
    }
}
