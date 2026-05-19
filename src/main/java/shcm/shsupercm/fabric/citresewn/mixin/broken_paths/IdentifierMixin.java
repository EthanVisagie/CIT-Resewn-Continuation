package shcm.shsupercm.fabric.citresewn.mixin.broken_paths;

import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shcm.shsupercm.fabric.citresewn.CITResewn;
import shcm.shsupercm.fabric.citresewn.config.BrokenPaths;
import shcm.shsupercm.fabric.citresewn.platform.Platform;

import static shcm.shsupercm.fabric.citresewn.config.BrokenPaths.processingBrokenPaths;

/**
 * Applies broken paths logic when active.
 * @see BrokenPaths
 * @see ReloadableResourceManagerImplMixin
 */
@Mixin(Identifier.class)
public class IdentifierMixin {
    @Inject(method = "isValidPath", cancellable = true, at = @At("RETURN"))
    private static void citresewn$brokenpaths$processBrokenPaths(String path, CallbackInfoReturnable<Boolean> cir) {
        if (!processingBrokenPaths)
            return;

        if (!cir.getReturnValue()) {
            if (Platform.isDevelopmentEnvironment())
                CITResewn.logWarnLoading("Warning: Encountered broken path: \"" + path + "\"");

            cir.setReturnValue(true);
        }
    }
}
