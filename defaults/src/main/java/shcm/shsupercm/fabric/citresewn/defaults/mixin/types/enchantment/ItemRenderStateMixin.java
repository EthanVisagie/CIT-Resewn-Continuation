package shcm.shsupercm.fabric.citresewn.defaults.mixin.types.enchantment;

import net.minecraft.client.render.item.ItemRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shcm.shsupercm.fabric.citresewn.cit.CIT;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeEnchantment;

import java.util.List;

@Mixin(ItemRenderState.class)
public class ItemRenderStateMixin implements TypeEnchantment.CITEnchantmentRenderState {
    private List<CIT<TypeEnchantment>> citresewn$typeEnchantments = List.of();

    @Inject(method = "clear", at = @At("HEAD"))
    private void citresewn$clearEnchantments(CallbackInfo ci) {
        this.citresewn$typeEnchantments = List.of();
    }

    @Override
    public List<CIT<TypeEnchantment>> citresewn$getTypeEnchantments() {
        return this.citresewn$typeEnchantments;
    }

    @Override
    public void citresewn$setTypeEnchantments(List<CIT<TypeEnchantment>> enchantments) {
        this.citresewn$typeEnchantments = enchantments;
    }
}
