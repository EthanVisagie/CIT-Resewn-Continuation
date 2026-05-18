package shcm.shsupercm.fabric.citresewn.defaults.cit.types;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import shcm.shsupercm.fabric.citresewn.api.CITTypeContainer;
import shcm.shsupercm.fabric.citresewn.cit.*;
import shcm.shsupercm.fabric.citresewn.defaults.cit.conditions.ConditionItems;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyKey;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class TypeElytra extends CITType {
    @Entrypoint(CITTypeContainer.ENTRYPOINT)
    public static final Container CONTAINER = new Container();

    public Identifier texture;

    @Override
    public Set<PropertyKey> typeProperties() {
        return Set.of(PropertyKey.of("texture"));
    }

    @Override
    public void load(List<CITCondition> conditions, PropertyGroup properties, ResourceManager resourceManager) throws CITParsingException {
        for (CITCondition condition : conditions)
            if (condition instanceof ConditionItems items)
                for (Item item : items.items)
                    if (!isElytraItem(item))
                        warn("Non elytra item type condition", null, properties);

        texture = resolveAsset(properties.identifier, properties.getLastWithoutMetadata("citresewn", "texture"), "textures", ".png", resourceManager);
        if (texture == null)
            throw new CITParsingException("Texture not specified", properties, -1);
    }

    public static class Container extends CITTypeContainer<TypeElytra> {
        public Container() {
            super(TypeElytra.class, TypeElytra::new, "elytra");
        }

        public final List<Function<LivingEntity, ItemStack>> getItemInSlotCompatRedirects = new ArrayList<>();

        public Set<CIT<TypeElytra>> loaded = new LinkedHashSet<>();

        @Override
        public void load(List<CIT<TypeElytra>> parsedCITs) {
            loaded.addAll(parsedCITs);
        }

        @Override
        public void dispose() {
            loaded.clear();
        }

        public CIT<TypeElytra> getCIT(CITContext context) {
            return ((CITCacheElytra) (Object) context.stack).citresewn$getCacheTypeElytra().get(context).get();
        }

        public CIT<TypeElytra> getRealTimeCIT(CITContext context) {
            for (CIT<TypeElytra> cit : loaded)
                if (cit.test(context))
                    return cit;

            return null;
        }

        public ItemStack getVisualElytraItem(LivingEntity entity) {
            for (Function<LivingEntity, ItemStack> redirect : getItemInSlotCompatRedirects) {
                ItemStack stack = redirect.apply(entity);
                if (stack != null)
                    return stack;
            }

            return entity.getItemBySlot(EquipmentSlot.CHEST);
        }
    }

    public static class ElytraCITCache extends CITCache.Single<TypeElytra> {
        private int lastDamage = Integer.MIN_VALUE;
        private int lastMaxDamage = Integer.MIN_VALUE;

        public ElytraCITCache() {
            super(CONTAINER::getRealTimeCIT);
        }

        @Override
        public java.lang.ref.WeakReference<CIT<TypeElytra>> get(CITContext context) {
            int damage = context.stack.isDamageableItem() ? context.stack.getDamageValue() : 0;
            int maxDamage = context.stack.isDamageableItem() ? context.stack.getMaxDamage() : 0;

            if (damage != lastDamage || maxDamage != lastMaxDamage) {
                this.cit = null;
                this.lastDamage = damage;
                this.lastMaxDamage = maxDamage;
            }

            return super.get(context);
        }
    }

    public interface CITCacheElytra {
        CITCache.Single<TypeElytra> citresewn$getCacheTypeElytra();
    }

    private static boolean isElytraItem(Item item) {
        try {
            return item.getDefaultInstance().get(DataComponents.GLIDER) != null;
        } catch (NullPointerException ignored) {
            // In 26.1 this can run during resource reload before item default components
            // are fully bound. This check is only used for warnings, so fall back to the
            // vanilla elytra identity instead of failing the whole CIT.
            return item == net.minecraft.world.item.Items.ELYTRA;
        }
    }
}
