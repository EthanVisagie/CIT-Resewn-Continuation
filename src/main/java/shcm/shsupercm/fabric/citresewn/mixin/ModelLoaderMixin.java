package shcm.shsupercm.fabric.citresewn.mixin;

import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.cit.ActiveCITs;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Initializes the (re)loading of active cits in the resource manager.
 * @see ActiveCITs
 */
@Mixin(BakedModelManager.class)
public class ModelLoaderMixin {
    /**
     * @see ActiveCITs#load(ResourceManager, Profiler)
     */
    @Inject(method = "reload", at = @At("HEAD"))
    private void citresewn$loadCITs(ResourceReloader.Store store, Executor prepareExecutor, ResourceReloader.Synchronizer synchronizer, Executor applyExecutor, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        Profiler profiler = Profilers.get();
        profiler.push("citresewn:reloading_cits");
        ActiveCITs.load(store.getResourceManager(), profiler);
        profiler.pop();
    }
}
