package shcm.shsupercm.fabric.citresewn.defaults.mixin.types.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.cit.CIT;
import shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeItem;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;

import static shcm.shsupercm.fabric.citresewn.defaults.cit.types.TypeItem.CONTAINER;

@Mixin(ModelManager.class)
public class BakedModelManagerMixin {
    @Inject(method = "loadBlockModels", at = @At("RETURN"), cancellable = true)
    private static void citresewn$addItemTypeModels(net.minecraft.server.packs.resources.ResourceManager resourceManager, Executor executor, CallbackInfoReturnable<CompletableFuture<Map<Identifier, UnbakedModel>>> cir) {
        if (!CONTAINER.active())
            return;

        cir.setReturnValue(cir.getReturnValue().thenApply(models -> {
            Map<Identifier, UnbakedModel> syntheticModels = new HashMap<>(models);
            for (CIT<TypeItem> cit : CONTAINER.loaded)
                syntheticModels.putAll(cit.type.createUnbakedModels(resourceManager));
            return syntheticModels;
        }));
    }
}
