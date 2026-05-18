package shcm.shsupercm.fabric.citresewn.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.cit.ActiveCITs;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;

/**
 * Initializes the (re)loading of active cits in the resource manager.
 * @see ActiveCITs
 */
@Mixin(ModelManager.class)
public class ModelLoaderMixin {
    /**
     * @see ActiveCITs#load(ResourceManager, Profiler)
     */
    @Inject(method = "reload", at = @At("HEAD"))
    private void citresewn$loadCITs(PreparableReloadListener.SharedState store, Executor prepareExecutor, PreparableReloadListener.PreparationBarrier synchronizer, Executor applyExecutor, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        ProfilerFiller profiler = Profiler.get();
        profiler.push("citresewn:reloading_cits");
        ActiveCITs.load(store.resourceManager(), profiler);
        profiler.pop();
    }
}
