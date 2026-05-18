package shcm.shsupercm.fabric.citresewn.defaults.mixin.types.item;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.cit.CIT;
import shcm.shsupercm.fabric.citresewn.cit.CITContext;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeEnchantment;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeItem;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import static shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeItem.CONTAINER;

@Mixin(ItemModelResolver.class)
public abstract class ItemModelManagerMixin {
    @Invoker("getItemModel")
    public abstract ItemModel invokeGetItemModel(Identifier id);

    @Invoker("getItemProperties")
    public abstract ClientItem.Properties invokeGetItemProperties(Identifier id);

    @Inject(method = "appendItemLayers", at = @At("HEAD"), cancellable = true)
    private void citresewn$update(ItemStackRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, Level world, ItemOwner heldItemContext, int seed, CallbackInfo ci) {
        CITContext context = new CITContext(stack, world, heldItemContext == null ? null : heldItemContext.asLivingEntity());
        if (TypeEnchantment.CONTAINER.active())
            ((TypeEnchantment.CITEnchantmentRenderState) renderState).citresewn$setTypeEnchantments(TypeEnchantment.CONTAINER.getCITs(context));
        else
            ((TypeEnchantment.CITEnchantmentRenderState) renderState).citresewn$setTypeEnchantments(java.util.List.of());

        Identifier identifier = citresewn$getItemModelId(stack, world, heldItemContext);
        if (identifier == null)
            return;

        ClientItem.Properties properties = this.invokeGetItemProperties(identifier);
        ItemModel itemModel = this.invokeGetItemModel(identifier);
        if (properties == null || itemModel == null) {
            ci.cancel();
            return;
        }

        renderState.setOversizedInGui(properties.oversizedInGui());
        itemModel.update(
                renderState,
                stack,
                (ItemModelResolver) (Object) this,
                displayContext,
                world instanceof ClientLevel clientWorld ? clientWorld : null,
                heldItemContext,
                seed
        );
        ci.cancel();
    }

    @Inject(method = "shouldPlaySwapAnimation", at = @At("HEAD"), cancellable = true)
    private void citresewn$hasHandAnimationOnSwap(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Identifier identifier = citresewn$getItemModelId(stack, null, null);
        if (identifier == null)
            return;

        ClientItem.Properties properties = this.invokeGetItemProperties(identifier);
        if (properties != null)
            cir.setReturnValue(properties.handAnimationOnSwap());
    }

    @Inject(method = "swapAnimationScale", at = @At("HEAD"), cancellable = true)
    private void citresewn$getSwapAnimationScale(ItemStack stack, CallbackInfoReturnable<Float> cir) {
        Identifier identifier = citresewn$getItemModelId(stack, null, null);
        if (identifier == null)
            return;

        ClientItem.Properties properties = this.invokeGetItemProperties(identifier);
        if (properties != null)
            cir.setReturnValue(properties.swapAnimationScale());
    }

    private Identifier citresewn$getItemModelId(ItemStack stack, Level world, ItemOwner heldItemContext) {
        CIT<TypeItem> cit = null;
        if (CONTAINER.active()) {
            CITContext context = new CITContext(stack, world, heldItemContext == null ? null : heldItemContext.asLivingEntity());
            cit = CONTAINER.getCIT(context);
        }

        if (cit != null) {
            Identifier generatedId = cit.type.getGeneratedItemModelId(stack);
            if (generatedId != null)
                return generatedId;
        }

        return stack.get(net.minecraft.core.component.DataComponents.ITEM_MODEL);
    }
}
