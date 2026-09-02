package io.github.gtbauke.modernmachines.modular.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.api.modular.trait.ToolTrait;
import io.github.gtbauke.modernmachines.api.modular.trait.ToolTraitRegistry;
import io.github.gtbauke.modernmachines.api.modular.MaterialStatsManager;
import io.github.gtbauke.modernmachines.api.modular.MaterialToolStats;
import io.github.gtbauke.modernmachines.api.modular.ModularToolData;
import io.github.gtbauke.modernmachines.api.modular.PartSlot;
import io.github.gtbauke.modernmachines.core.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public abstract class ModularToolItem extends Item {
    public static final Identifier BASE_ATTACK_DAMAGE_ID = Identifier.withDefaultNamespace("base_attack_damage");
    public static final Identifier BASE_ATTACK_SPEED_ID = Identifier.withDefaultNamespace("base_attack_speed");

    private final TagKey<Block> mineableTag;
    private final float baseAttackDamage;
    private final float baseAttackSpeed;

    public ModularToolItem(TagKey<Block> mineableTag, float baseAttackDamage, float baseAttackSpeed, Properties properties) {
        super(properties.stacksTo(1));
        this.mineableTag = mineableTag;
        this.baseAttackDamage = baseAttackDamage;
        this.baseAttackSpeed = baseAttackSpeed;
    }

    public TagKey<Block> getMineableTag() {
        return mineableTag;
    }

    public float getBaseAttackDamage() {
        return baseAttackDamage;
    }

    public float getBaseAttackSpeed() {
        return baseAttackSpeed;
    }

    public static ModularToolData getData(ItemStack stack) {
        var data = stack.get(ModDataComponents.MODULAR_TOOL_DATA.get());
        return data != null ? data : ModularToolData.EMPTY;
    }

    public static void setData(ItemStack stack, ModularToolData data) {
        stack.set(ModDataComponents.MODULAR_TOOL_DATA.get(), data);
        recalculateComponents(stack);
    }

    public static Map<ToolTrait, Integer> getActiveTraits(ItemStack stack) {
        var data = getData(stack);
        if (data.parts().isEmpty()) {
            return Collections.emptyMap();
        }

        var traitLevels = new LinkedHashMap<ToolTrait, Integer>();

        for (var matId : data.parts().values()) {
            if (matId == null) {
                continue;
            }

            var statsOpt = MaterialStatsManager.getStats(matId);
            if (statsOpt.isEmpty()) {
                continue;
            }

            for (var traitEntry : statsOpt.get().traits()) {
                var trait = ToolTraitRegistry.get(traitEntry.id());
                if (trait == null) {
                    continue;
                }

                traitLevels.merge(trait, traitEntry.level(), Integer::sum);
            }
        }

        return traitLevels;
    }

    public static int getMaxDurability(ItemStack stack) {
        var data = getData(stack);
        var headMat = data.getPartMaterial(PartSlot.HEAD);
        if (headMat == null) {
            return 100;
        }

        int baseDurability = MaterialStatsManager.getStats(headMat)
                .flatMap(MaterialToolStats::head)
                .map(MaterialToolStats.HeadStats::durability)
                .orElse(100);

        float handleMultiplier = 1.0f;
        var handleMat = data.getPartMaterial(PartSlot.HANDLE);
        if (handleMat != null) {
            handleMultiplier = MaterialStatsManager.getStats(handleMat)
                    .flatMap(MaterialToolStats::handle)
                    .map(MaterialToolStats.HandleStats::durabilityMultiplier)
                    .orElse(1.0f);
        }

        int bindingBonus = 0;
        var bindingMat = data.getPartMaterial(PartSlot.BINDING);
        if (bindingMat != null) {
            bindingBonus = MaterialStatsManager.getStats(bindingMat)
                    .flatMap(MaterialToolStats::binding)
                    .map(MaterialToolStats.BindingStats::bonusDurability)
                    .orElse(0);
        }

        int attachmentBonus = 0;
        var tipMat = data.getPartMaterial(PartSlot.TIP);
        if (tipMat != null) {
            attachmentBonus += MaterialStatsManager.getStats(tipMat)
                    .flatMap(MaterialToolStats::attachment)
                    .map(MaterialToolStats.AttachmentStats::bonusDurability)
                    .orElse(0);
        }

        var pommelMat = data.getPartMaterial(PartSlot.POMMEL);
        if (pommelMat != null) {
            attachmentBonus += MaterialStatsManager.getStats(pommelMat)
                    .flatMap(MaterialToolStats::attachment)
                    .map(MaterialToolStats.AttachmentStats::bonusDurability)
                    .orElse(0);
        }

        int modifierBonus = data.getModifierLevel(Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "diamond")) * 500;

        return Math.max(1, Math.round(baseDurability * handleMultiplier) + bindingBonus + attachmentBonus + modifierBonus);
    }

    public static float getMiningSpeed(ItemStack stack) {
        var data = getData(stack);
        var headMat = data.getPartMaterial(PartSlot.HEAD);
        if (headMat == null) {
            return 1.0f;
        }

        float baseSpeed = MaterialStatsManager.getStats(headMat)
                .flatMap(MaterialToolStats::head)
                .map(MaterialToolStats.HeadStats::miningSpeed)
                .orElse(1.0f);

        float handleMultiplier = 1.0f;
        var handleMat = data.getPartMaterial(PartSlot.HANDLE);
        if (handleMat != null) {
            handleMultiplier = MaterialStatsManager.getStats(handleMat)
                    .flatMap(MaterialToolStats::handle)
                    .map(MaterialToolStats.HandleStats::miningSpeedMultiplier)
                    .orElse(1.0f);
        }

        float hasteBonus = data.getModifierLevel(Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "haste")) * 1.5f;

        float finalSpeed = Math.max(1.0f, (baseSpeed * handleMultiplier) + hasteBonus);
        for (var entry : getActiveTraits(stack).entrySet()) {
            finalSpeed = entry.getKey().modifyMiningSpeed(stack, null, null, finalSpeed, entry.getValue());
        }

        return Math.max(1.0f, finalSpeed);
    }

    public static float getAttackDamage(ItemStack stack, float baseDamage) {
        var data = getData(stack);
        var headMat = data.getPartMaterial(PartSlot.HEAD);
        if (headMat == null) {
            return baseDamage;
        }

        float headDamage = MaterialStatsManager.getStats(headMat)
                .flatMap(MaterialToolStats::head)
                .map(MaterialToolStats.HeadStats::attackDamage)
                .orElse(1.0f);

        float attachmentBonus = 0.0f;
        var tipMat = data.getPartMaterial(PartSlot.TIP);
        if (tipMat != null) {
            attachmentBonus += MaterialStatsManager.getStats(tipMat)
                    .flatMap(MaterialToolStats::attachment)
                    .map(MaterialToolStats.AttachmentStats::attackDamageBonus)
                    .orElse(0.0f);
        }

        var pommelMat = data.getPartMaterial(PartSlot.POMMEL);
        if (pommelMat != null) {
            attachmentBonus += MaterialStatsManager.getStats(pommelMat)
                    .flatMap(MaterialToolStats::attachment)
                    .map(MaterialToolStats.AttachmentStats::attackDamageBonus)
                    .orElse(0.0f);
        }

        float sharpnessBonus = data.getModifierLevel(Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "sharpness")) * 1.25f;

        float finalDamage = baseDamage + headDamage + attachmentBonus + sharpnessBonus;
        for (var entry : getActiveTraits(stack).entrySet()) {
            finalDamage = entry.getKey().modifyAttackDamage(stack, null, null, finalDamage, entry.getValue());
        }

        return Math.max(1.0f, finalDamage);
    }

    public static float getAttackSpeed(ItemStack stack, float baseSpeed) {
        var data = getData(stack);
        float speedBonus = 0.0f;

        var pommelMat = data.getPartMaterial(PartSlot.POMMEL);
        if (pommelMat != null) {
            speedBonus += MaterialStatsManager.getStats(pommelMat)
                    .flatMap(MaterialToolStats::attachment)
                    .map(MaterialToolStats.AttachmentStats::speedBonus)
                    .orElse(0.0f);
        }

        float hasteBonus = data.getModifierLevel(Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "haste")) * 0.2f;

        return Math.min(0.0f, baseSpeed + speedBonus + hasteBonus);
    }

    public static String getHarvestTier(ItemStack stack) {
        var data = getData(stack);
        var tipMat = data.getPartMaterial(PartSlot.TIP);
        if (tipMat != null) {
            var override = MaterialStatsManager.getStats(tipMat)
                    .flatMap(MaterialToolStats::attachment)
                    .flatMap(MaterialToolStats.AttachmentStats::tierOverride);
            if (override.isPresent()) {
                return override.get();
            }
        }

        var headMat = data.getPartMaterial(PartSlot.HEAD);
        if (headMat != null) {
            return MaterialStatsManager.getStats(headMat)
                    .flatMap(MaterialToolStats::head)
                    .map(MaterialToolStats.HeadStats::harvestTier)
                    .orElse("iron");
        }

        return "wood";
    }

    public static void recalculateComponents(ItemStack stack) {
        if (!(stack.getItem() instanceof ModularToolItem toolItem)) {
            return;
        }

        var data = getData(stack);
        if (data.parts().isEmpty()) {
            return;
        }

        int maxDurability = getMaxDurability(stack);
        stack.set(DataComponents.MAX_DAMAGE, maxDurability);
        if (!stack.has(DataComponents.DAMAGE)) {
            stack.set(DataComponents.DAMAGE, data.damage());
        }

        float totalDamage = getAttackDamage(stack, toolItem.baseAttackDamage);
        float totalSpeed = getAttackSpeed(stack, toolItem.baseAttackSpeed);

        var attrBuilder = ItemAttributeModifiers.builder();
        attrBuilder.add(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(BASE_ATTACK_DAMAGE_ID, totalDamage, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
        );
        attrBuilder.add(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(BASE_ATTACK_SPEED_ID, totalSpeed, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
        );
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, attrBuilder.build());

        float miningSpeed = getMiningSpeed(stack);
        var rules = new ArrayList<Tool.Rule>();
        if (toolItem.mineableTag != null) {
            BuiltInRegistries.BLOCK.get(toolItem.mineableTag).ifPresent(tag -> {
                rules.add(Tool.Rule.minesAndDrops(tag, miningSpeed));
            });
        }

        if (toolItem instanceof ModularSwordItem) {
            rules.add(Tool.Rule.minesAndDrops(HolderSet.direct(Blocks.COBWEB.builtInRegistryHolder()), 15.0F));
            BuiltInRegistries.BLOCK.get(BlockTags.SWORD_EFFICIENT).ifPresent(tag -> {
                rules.add(Tool.Rule.overrideSpeed(tag, 1.5F));
            });
        }

        stack.set(DataComponents.TOOL, new Tool(rules, 1.0F, 1, false));
    }

    @Override
    public void inventoryTick(ItemStack stack, @NonNull ServerLevel level, @NonNull Entity entity, @Nullable EquipmentSlot slot) {
        if (!stack.has(DataComponents.MAX_DAMAGE) || !stack.has(DataComponents.ATTRIBUTE_MODIFIERS)) {
            recalculateComponents(stack);
        }

        super.inventoryTick(stack, level, entity, slot);

        for (var entry : getActiveTraits(stack).entrySet()) {
            entry.getKey().onInventoryTick(stack, level, entity, slot, entry.getValue());
        }
    }

    @Override
    public int getMaxDamage(@NonNull ItemStack stack) {
        return getMaxDurability(stack);
    }

    @Override
    public int getDamage(ItemStack stack) {
        return stack.getOrDefault(DataComponents.DAMAGE, getData(stack).damage());
    }

    @Override
    public void setDamage(@NonNull ItemStack stack, int damage) {
        int max = getMaxDamage(stack);
        int clamped = Math.min(Math.max(0, damage), max);
        stack.set(DataComponents.DAMAGE, clamped);
        var data = getData(stack);
        if (data.damage() != clamped) {
            stack.set(ModDataComponents.MODULAR_TOOL_DATA.get(), data.withDamage(clamped));
        }
    }

    @Override
    public boolean isBarVisible(@NonNull ItemStack stack) {
        return getDamage(stack) > 0;
    }

    @Override
    public int getBarWidth(@NonNull ItemStack stack) {
        int max = getMaxDamage(stack);
        int current = getDamage(stack);
        return Math.round(13.0F - (float) current * 13.0F / (float) max);
    }

    @Override
    public int getBarColor(@NonNull ItemStack stack) {
        int max = getMaxDamage(stack);
        int current = getDamage(stack);
        float f = Math.max(0.0F, ((float) max - (float) current) / (float) max);
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public float getDestroySpeed(@NonNull ItemStack stack, @NonNull BlockState state) {
        if (mineableTag != null && state.is(mineableTag)) {
            float speed = getMiningSpeed(stack);
            for (var entry : getActiveTraits(stack).entrySet()) {
                speed = entry.getKey().modifyMiningSpeed(stack, state, null, speed, entry.getValue());
            }

            return Math.max(1.0f, speed);
        }

        return super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean isCorrectToolForDrops(@NonNull ItemStack stack, @NonNull BlockState state) {
        if (mineableTag != null && state.is(mineableTag)) {
            var tier = getHarvestTier(stack);
            if (state.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return tier.equalsIgnoreCase("diamond") || tier.equalsIgnoreCase("netherite") || tier.equalsIgnoreCase("titanium");
            } else if (state.is(BlockTags.NEEDS_IRON_TOOL)) {
                return !tier.equalsIgnoreCase("wood") && !tier.equalsIgnoreCase("stone");
            } else if (state.is(BlockTags.NEEDS_STONE_TOOL)) {
                return !tier.equalsIgnoreCase("wood");
            }

            return true;
        }

        return super.isCorrectToolForDrops(stack, state);
    }

    @Override
    public void hurtEnemy(@NonNull ItemStack stack, @NonNull LivingEntity target, @NonNull LivingEntity attacker) {
        applyDamage(stack, 2, attacker);

        for (var entry : getActiveTraits(stack).entrySet()) {
            entry.getKey().onAttack(stack, target, attacker, entry.getValue());
        }
    }

    @Override
    public boolean mineBlock(@NonNull ItemStack stack, @NonNull Level level, @NonNull BlockState state, @NonNull BlockPos pos, @NonNull LivingEntity entity) {
        if (!level.isClientSide() && state.getDestroySpeed(level, pos) != 0.0F) {
            applyDamage(stack, 1, entity);

            for (var entry : getActiveTraits(stack).entrySet()) {
                entry.getKey().onMineBlock(stack, level, state, pos, entity, entry.getValue());
            }
        }

        return true;
    }

    public void applyDamage(ItemStack stack, int amount, LivingEntity entity) {
        int finalAmount = amount;
        for (var entry : getActiveTraits(stack).entrySet()) {
            finalAmount = entry.getKey().onDamageTool(stack, finalAmount, entity, entry.getValue());
            if (finalAmount <= 0) {
                return;
            }
        }

        var data = getData(stack);
        int unbreaking = data.getModifierLevel(Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "reinforced"));
        if (unbreaking > 0 && entity.getRandom().nextInt(1 + unbreaking) > 0) {
            return;
        }

        stack.hurtAndBreak(finalAmount, entity, EquipmentSlot.MAINHAND);
        int newDmg = stack.getOrDefault(DataComponents.DAMAGE, 0);
        if (data.damage() != newDmg) {
            stack.set(ModDataComponents.MODULAR_TOOL_DATA.get(), data.withDamage(newDmg));
        }
    }

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, @NonNull Consumer<Component> tooltip, @NonNull TooltipFlag flag) {
        var data = getData(stack);
        if (data.parts().isEmpty()) {
            tooltip.accept(Component.translatable("tooltip.modernmachines.unassembled_tool").withStyle(ChatFormatting.RED));
            return;
        }

        tooltip.accept(Component.translatable("tooltip.modernmachines.parts_header").withStyle(ChatFormatting.GOLD));
        data.parts().forEach((slot, matId) -> {
            tooltip.accept(Component.literal("  ")
                    .append(Component.translatable("part_slot.modernmachines." + slot.getSerializedName()).withStyle(ChatFormatting.GRAY))
                    .append(": ")
                    .append(Component.translatable("material." + matId.getNamespace() + "." + matId.getPath()).withStyle(ChatFormatting.WHITE)));
        });

        tooltip.accept(Component.translatable("tooltip.modernmachines.stats_header").withStyle(ChatFormatting.GOLD));
        int currentDurability = getMaxDamage(stack) - getDamage(stack);
        tooltip.accept(Component.literal("  ")
                .append(Component.translatable("tooltip.modernmachines.stat.durability_ratio", currentDurability, getMaxDamage(stack)).withStyle(ChatFormatting.DARK_GREEN)));
        tooltip.accept(Component.literal("  ")
                .append(Component.translatable("tooltip.modernmachines.stat.mining_speed", String.format("%.1f", getMiningSpeed(stack))).withStyle(ChatFormatting.BLUE)));
        tooltip.accept(Component.literal("  ")
                .append(Component.translatable("tooltip.modernmachines.stat.attack_damage", String.format("%.1f", getAttackDamage(stack, baseAttackDamage))).withStyle(ChatFormatting.RED)));
        tooltip.accept(Component.literal("  ")
                .append(Component.translatable("tooltip.modernmachines.stat.harvest_tier", getHarvestTier(stack)).withStyle(ChatFormatting.YELLOW)));

        int usedSlots = data.getUsedModifierSlots();
        int maxSlots = data.getMaxModifierSlots();
        tooltip.accept(Component.translatable("tooltip.modernmachines.modifiers_header", usedSlots, maxSlots).withStyle(ChatFormatting.AQUA));
        for (var mod : data.modifiers()) {
            tooltip.accept(Component.literal("  - ")
                    .append(Component.translatable("modifier." + mod.id().getNamespace() + "." + mod.id().getPath()))
                    .append(" " + mod.level()).withStyle(ChatFormatting.LIGHT_PURPLE));
        }

        var traits = getActiveTraits(stack);
        if (!traits.isEmpty()) {
            tooltip.accept(Component.translatable("tooltip.modernmachines.traits_header").withStyle(ChatFormatting.YELLOW));
            for (var entry : traits.entrySet()) {
                var trait = entry.getKey();
                var level = entry.getValue();
                tooltip.accept(Component.literal("  - ")
                        .append(trait.getDisplayName(level).copy().withStyle(ChatFormatting.AQUA))
                        .append(": ")
                        .append(trait.getDescription(level).copy().withStyle(ChatFormatting.GRAY)));
            }
        }

        super.appendHoverText(stack, context, display, tooltip, flag);
    }

    @Override
    public @NonNull Component getName(@NonNull ItemStack stack) {
        var data = getData(stack);
        var headMat = data.getPartMaterial(PartSlot.HEAD);
        if (headMat != null) {
            var matName = MaterialStatsManager.getStats(headMat)
                    .map(MaterialToolStats::getEffectiveDisplayName)
                    .orElse(headMat.getPath());
            return Component.translatable(this.getDescriptionId() + ".named", matName);
        }

        return super.getName(stack);
    }

    @Override
    public boolean isFoil(@NonNull ItemStack stack) {
        var data = getData(stack);
        return !data.modifiers().isEmpty() || super.isFoil(stack);
    }
}
