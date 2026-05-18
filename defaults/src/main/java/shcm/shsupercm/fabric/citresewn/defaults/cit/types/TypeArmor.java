package shcm.shsupercm.fabric.citresewn.defaults.cit.types;

import io.shcm.shsupercm.fabric.fletchingtable.api.Entrypoint;
import shcm.shsupercm.fabric.citresewn.api.CITTypeContainer;
import shcm.shsupercm.fabric.citresewn.cit.*;
import shcm.shsupercm.fabric.citresewn.defaults.cit.conditions.ConditionItems;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyGroup;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyKey;
import shcm.shsupercm.fabric.citresewn.pack.format.PropertyValue;

import java.util.*;
import java.util.function.BiFunction;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public class TypeArmor extends CITType {
    @Entrypoint(CITTypeContainer.ENTRYPOINT)
    public static final Container CONTAINER = new Container();

    public final Map<String, Identifier> textures = new HashMap<>();

    @Override
    public Set<PropertyKey> typeProperties() {
        return Set.of(PropertyKey.of("texture"));
    }

    @Override
    public void load(List<CITCondition> conditions, PropertyGroup properties, ResourceManager resourceManager) throws CITParsingException {
        boolean itemsConditionPresent = false;
        for (CITCondition condition : conditions)
            if (condition instanceof ConditionItems conditionItems)
                for (Item item : conditionItems.items)
                    if (isArmorItem(item))
                        itemsConditionPresent = true;
                    else
                        throw new CITParsingException("This type only accepts armor items for the items condition", properties, -1);

        if (!itemsConditionPresent)
            try {
                Identifier propertiesName = Identifier.tryParse(properties.stripName());
                if (!BuiltInRegistries.ITEM.containsKey(propertiesName))
                    throw new Exception();
                Item item = BuiltInRegistries.ITEM.getValue(propertiesName);
                if (!isArmorItem(item))
                    throw new Exception();
                conditions.add(new ConditionItems(item));
            } catch (Exception ignored) {
                throw new CITParsingException("Not targeting any item type", properties, -1);
            }

        for (PropertyValue propertyValue : properties.get("citresewn", "texture")) {
            Identifier identifier = resolveAsset(properties.identifier, propertyValue, "textures", ".png", resourceManager);
            if (identifier == null)
                throw new CITParsingException("Could not resolve texture", properties, propertyValue.position());

            textures.put(propertyValue.keyMetadata(), identifier);
        }
        if (textures.size() == 0)
            throw new CITParsingException("Texture not specified", properties, -1);
    }

    public static class Container extends CITTypeContainer<TypeArmor> {
        public Container() {
            super(TypeArmor.class, TypeArmor::new, "armor");
        }

        public final List<BiFunction<LivingEntity, EquipmentSlot, ItemStack>> getItemInSlotCompatRedirects = new ArrayList<>();

        public Set<CIT<TypeArmor>> loaded = new HashSet<>();
        public Map<Item, Set<CIT<TypeArmor>>> loadedTyped = new IdentityHashMap<>();

        @Override
        public void load(List<CIT<TypeArmor>> parsedCITs) {
            loaded.addAll(parsedCITs);
            for (CIT<TypeArmor> cit : parsedCITs)
                for (CITCondition condition : cit.conditions)
                    if (condition instanceof ConditionItems items)
                        for (Item item : items.items)
                            if (isArmorItem(item))
                                loadedTyped.computeIfAbsent(item, i -> new LinkedHashSet<>()).add(cit);
        }

        @Override
        public void dispose() {
            loaded.clear();
            loadedTyped.clear();
        }

        public CIT<TypeArmor> getCIT(CITContext context) {
            return ((CITCacheArmor) (Object) context.stack).citresewn$getCacheTypeArmor().get(context).get();
        }

        public CIT<TypeArmor> getRealTimeCIT(CITContext context) {
            if (!isArmorItem(context.stack.getItem()))
                return null;

            Set<CIT<TypeArmor>> loadedForItemType = loadedTyped.get(context.stack.getItem());
            if (loadedForItemType != null)
                for (CIT<TypeArmor> cit : loadedForItemType)
                    if (cit.test(context))
                        return cit;

            return null;
        }

        public ItemStack getVisualItemInSlot(LivingEntity entity, EquipmentSlot slot) {
            for (BiFunction<LivingEntity, EquipmentSlot, ItemStack> redirect : getItemInSlotCompatRedirects) {
                ItemStack stack = redirect.apply(entity, slot);
                if (stack != null)
                    return stack;
            }

            return entity.getItemBySlot(slot);
        }
    }

    public interface CITCacheArmor {
        CITCache.Single<TypeArmor> citresewn$getCacheTypeArmor();
    }

    public Identifier getTexture(EquipmentClientInfo.LayerType layerType, EquipmentClientInfo.Layer layer, Identifier originalTexture) {
        String basePath = layer.textureId().getPath();
        String currentPath = originalTexture.getPath();
        String currentKey = currentPath.startsWith("textures/entity/equipment/") && currentPath.endsWith(".png")
                ? currentPath.substring("textures/entity/equipment/".length(), currentPath.length() - ".png".length())
                : currentPath;
        int layerIndex = layerType == EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS ? 2 : 1;

        Identifier replacement = this.textures.get(currentKey);
        if (replacement != null)
            return replacement;

        replacement = this.textures.get(basePath);
        if (replacement != null)
            return replacement;

        String legacyKey;
        if (basePath.endsWith("_overlay"))
            legacyKey = basePath.substring(0, basePath.length() - "_overlay".length()) + "_layer_" + layerIndex + "_overlay";
        else
            legacyKey = basePath + "_layer_" + layerIndex;

        replacement = this.textures.get(legacyKey);
        if (replacement != null)
            return replacement;

        return this.textures.get(null);
    }

    private static boolean isArmorItem(Item item) {
        try {
            ItemStack stack = item.getDefaultInstance();
            Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
            if (equippable == null || equippable.assetId().isEmpty() || stack.get(DataComponents.GLIDER) != null)
                return false;

            return switch (equippable.slot()) {
                case HEAD, CHEST, LEGS, FEET -> true;
                default -> false;
            };
        } catch (NullPointerException ignored) {
            // In 26.1 this warning-only check can run before item default components
            // are fully bound during resource reload. Avoid rejecting otherwise valid CITs.
            return true;
        }
    }
}
