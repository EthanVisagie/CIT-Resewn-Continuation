# CIT Resewn 1.21.11 Smoke Pack

This pack is meant to quickly verify the current 1.21.11 port.

What it covers:
- `type=item` with `model=`
- `type=item` with `texture=`
- `type=armor`
- `type=elytra`
- `type=enchantment` for item, armor, and elytra glint

## How To Use

1. Put this folder, or the matching zip, into your Minecraft `resourcepacks` folder.
2. Enable the pack above your normal test packs.
3. Launch with these two jars in `mods`:
   - `citresewn-continuation-1.2.2-fork.1+1.21.11.jar`
   - `citresewn-continuation-defaults-1.2.2-fork.1+1.21.11.jar`

## Quick Checks

1. Hold a `diamond_sword`.
   Expected: the sword uses the green custom model texture.

2. Hold a `golden_apple`.
   Expected: the apple uses the flat green texture-only replacement.

3. Wear a `diamond_chestplate` and `diamond_leggings`.
   Expected: chest and upper armor layer uses bright green, leggings use cyan.

4. Wear an `elytra`.
   Expected: the elytra texture turns green.

5. Enchant and re-check:
   - `diamond_sword` with `sharpness`
   - `diamond_chestplate` or `diamond_leggings` with `protection`
   - `elytra` with `unbreaking`

   Expected:
   - sword gets a red custom glint
   - armor gets a blue custom glint
   - elytra gets a yellow custom glint

## Suggested Commands

Use these one at a time while holding the target item:

```mcfunction
/give @s diamond_sword
/give @s golden_apple
/give @s diamond_chestplate
/give @s diamond_leggings
/give @s elytra
```

```mcfunction
/enchant @s sharpness 5
/enchant @s protection 4
/enchant @s unbreaking 3
```

## Pack Layout

All CIT files are under:

`assets/minecraft/optifine/cit/smoke`
