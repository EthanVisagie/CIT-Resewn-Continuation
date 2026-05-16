# Legacy CustomModelData Compatibility Notes

## Goal

Support legacy OptiFine/CIT conditions such as:

```properties
nbt.CustomModelData=12345
```

on Minecraft `1.21.11`, where item NBT has been replaced by data components.

## Current failure

The current 1.21.11 code rejects this at parse time:

```text
Errored while parsing CIT: NBT condition is not supported since 1.21 @L4 in minecraft:optifine/cit/custom_model_data.properties from cmd-test-pack
```

`ConditionComponents` only translates legacy `nbt.display.Name` and `nbt.display.Lore`. Other `nbt.*` keys throw `NBT condition is not supported since 1.21`.

## Implemented fix

`ConditionComponents` now handles `nbt.CustomModelData` as a narrow legacy special case:

- Parse-time: `nbt.CustomModelData` is translated to `minecraft:custom_model_data` instead of throwing the generic unsupported-NBT error.
- Runtime on `1.21.11`: the legacy numeric value is compared against `CustomModelDataComponent.getFloat(0)`.
- Runtime on `1.21` / `1.21.1`: the versioned branch compares against `CustomModelDataComponent.value()`.

This intentionally does not restore broad arbitrary `nbt.*` matching on 1.21+.

## Version shape

Local Yarn-mapped class inspection:

- Minecraft `1.21` / `1.21.1`:
  - `net.minecraft.component.type.CustomModelDataComponent(int value)`
  - `DataComponentTypes.CUSTOM_MODEL_DATA` exists.
- Minecraft `1.21.11`:
  - `net.minecraft.component.type.CustomModelDataComponent(List<Float> floats, List<Boolean> flags, List<String> strings, List<Integer> colors)`
  - `DataComponentTypes.CUSTOM_MODEL_DATA` exists.

External references:

- Minecraft Wiki notes that `custom_model_data` changed in `1.21.4` / `24w45a` to use `floats`, `flags`, `strings`, and `colors`.
- mappings.dev for `1.21.9` shows the list-based `CustomModelData` shape, matching `1.21.11`.
- Paper `1.21.10` API exposes `CustomModelData.floats()`, `flags()`, `strings()`, and `colors()`, matching `1.21.11`.

## Compatibility plan

For `1.21.11`, legacy:

```properties
nbt.CustomModelData=12345
```

should match the first float entry of `minecraft:custom_model_data`, equivalent to modern component data:

```text
minecraft:custom_model_data={floats:[12345.0]}
```

This should be implemented as a narrow special case instead of reopening broad legacy NBT support.

## Cross-version note

The fix must be version-aware:

- `1.21` / `1.21.1`: compare the legacy value against `CustomModelDataComponent.value()`.
- `1.21.4+`, including `1.21.9`, `1.21.10`, and `1.21.11`: compare the legacy value against `CustomModelDataComponent.getFloat(0)` or `floats().get(0)`.

The current active release target is `1.21.11`, so the immediate patch can target the list-based component shape. If this fix is backported to the repo's `1.21` folder, it needs the integer-shape branch.

## Risk

Low to moderate if scoped only to `nbt.CustomModelData`.

Avoid mapping all arbitrary `nbt.*` keys to components. Legacy NBT paths do not generally map cleanly to modern components, but `CustomModelData` is a high-value, well-known compatibility key.
