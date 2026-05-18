package shcm.shsupercm.fabric.citresewn.defaults.mixin.types.elytra;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.cit.CIT;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeElytra;

import static shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeElytra.CONTAINER;

import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

@Mixin(WingsLayer.class)
public class ElytraFeatureRendererMixin {
    @Inject(method = "getPlayerElytraTexture", at = @At("HEAD"), cancellable = true)
    private static void citresewn$getTexture(HumanoidRenderState state, CallbackInfoReturnable<Identifier> cir) {
        if (!CONTAINER.active())
            return;

        ItemStack equippedStack = state.chestEquipment;
        if (equippedStack == null || equippedStack.get(DataComponents.GLIDER) == null)
            return;

        CIT<TypeElytra> cit = CONTAINER.getCIT(new CITContext(equippedStack, null, null));
        if (cit != null)
            cir.setReturnValue(cit.type.texture);
    }
}
