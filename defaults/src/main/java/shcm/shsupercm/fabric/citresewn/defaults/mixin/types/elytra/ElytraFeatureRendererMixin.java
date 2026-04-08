package shcm.shsupercm.fabric.citresewn.defaults.mixin.types.elytra;

import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.cit.CIT;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeElytra;

import static shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeElytra.CONTAINER;

@Mixin(ElytraFeatureRenderer.class)
public class ElytraFeatureRendererMixin {
    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
    private static void citresewn$getTexture(BipedEntityRenderState state, CallbackInfoReturnable<Identifier> cir) {
        if (!CONTAINER.active())
            return;

        ItemStack equippedStack = state.equippedChestStack;
        if (equippedStack == null || equippedStack.get(DataComponentTypes.GLIDER) == null)
            return;

        CIT<TypeElytra> cit = CONTAINER.getCIT(new CITContext(equippedStack, null, null));
        if (cit != null)
            cir.setReturnValue(cit.type.texture);
    }
}
