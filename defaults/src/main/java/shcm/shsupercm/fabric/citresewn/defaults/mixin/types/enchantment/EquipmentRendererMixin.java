package shcm.shsupercm.fabric.citresewn.defaults.mixin.types.enchantment;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.RenderCommandQueue;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import shcm.shsupercm.fabric.citresewn.cit.CIT;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeEnchantment;

import java.util.List;

import static shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeEnchantment.CONTAINER;

@Mixin(EquipmentRenderer.class)
@SuppressWarnings({"rawtypes", "unchecked"})
public class EquipmentRendererMixin {
    @WrapOperation(
            method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/util/Identifier;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/command/RenderCommandQueue;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IIILnet/minecraft/client/texture/Sprite;ILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V",
                    ordinal = 1
            )
    )
    private void citresewn$submitEnchantmentGlint(
            RenderCommandQueue queue,
            Model<?> model,
            Object state,
            MatrixStack matrices,
            RenderLayer renderLayer,
            int light,
            int overlay,
            int color,
            Sprite sprite,
            int batchingIndex,
            ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay,
            Operation<Void> original,
            EquipmentModel.LayerType layerType,
            RegistryKey<?> assetKey,
            Model<?> renderModel,
            Object renderState,
            ItemStack stack,
            MatrixStack renderMatrices,
            net.minecraft.client.render.command.OrderedRenderCommandQueue commandQueue,
            int renderLight,
            Identifier texture,
            int outlineColor,
            int renderBatchingIndex
    ) {
        if (!CONTAINER.active()) {
            original.call(queue, model, state, matrices, renderLayer, light, overlay, color, sprite, batchingIndex, crumblingOverlay);
            return;
        }

        if (!TypeEnchantment.hasEnchantments(stack)) {
            original.call(queue, model, state, matrices, renderLayer, light, overlay, color, sprite, batchingIndex, crumblingOverlay);
            return;
        }

        List<CIT<TypeEnchantment>> enchantments = CONTAINER.getCITs(new CITContext(stack, null, null));
        if (enchantments.isEmpty()) {
            original.call(queue, model, state, matrices, renderLayer, light, overlay, color, sprite, batchingIndex, crumblingOverlay);
            return;
        }

        boolean keepVanillaGlint = false;
        for (CIT<TypeEnchantment> enchantment : enchantments)
            if (enchantment.type.useGlint) {
                keepVanillaGlint = true;
                break;
            }

        if (keepVanillaGlint)
            original.call(queue, model, state, matrices, renderLayer, light, overlay, color, sprite, batchingIndex, crumblingOverlay);

        for (CIT<TypeEnchantment> enchantment : enchantments)
            queue.submitModel((Model) model, state, matrices, enchantment.type.getArmorGlintLayer(), light, overlay, color, sprite, batchingIndex, crumblingOverlay);
    }
}
