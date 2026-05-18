package shcm.shsupercm.fabric.citresewn.defaults.mixin.types.enchantment;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import shcm.shsupercm.fabric.citresewn.cit.CIT;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeEnchantment;

import java.util.List;

import static shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeEnchantment.CONTAINER;

@Mixin(EquipmentRenderer.class)
public class EquipmentRendererMixin {
    @WrapOperation(
            method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/ItemRenderer;getArmorGlintConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/RenderLayer;Z)Lnet/minecraft/client/render/VertexConsumer;"
            )
    )
    private VertexConsumer citresewn$getArmorGlintConsumer(
            VertexConsumerProvider vertexConsumers,
            RenderLayer layer,
            boolean glint,
            Operation<VertexConsumer> original,
            Object layerType,
            Object assetKey,
            Object model,
            ItemStack stack
    ) {
        if (!CONTAINER.active() || !TypeEnchantment.hasEnchantments(stack))
            return original.call(vertexConsumers, layer, glint);

        List<CIT<TypeEnchantment>> enchantments = CONTAINER.getCITs(new CITContext(stack, null, null));
        if (enchantments.isEmpty())
            return original.call(vertexConsumers, layer, glint);

        for (CIT<TypeEnchantment> enchantment : enchantments)
            if (enchantment.type.useGlint)
                return original.call(vertexConsumers, layer, glint);

        return original.call(vertexConsumers, layer, false);
    }
}
