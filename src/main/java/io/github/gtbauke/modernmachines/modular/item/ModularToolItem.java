package io.github.gtbauke.modernmachines.modular.item;

import java.util.Optional;
import java.util.function.Consumer;

import io.github.gtbauke.modernmachines.ModernMachines;
import io.github.gtbauke.modernmachines.api.modular.MaterialStatsManager;
import io.github.gtbauke.modernmachines.api.modular.MaterialToolStats;
import io.github.gtbauke.modernmachines.api.modular.ModifierEntry;
import io.github.gtbauke.modernmachines.api.modular.ModularToolData;
import io.github.gtbauke.modernmachines.api.modular.PartSlot;
import io.github.gtbauke.modernmachines.core.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ModularToolItem extends Item {
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

    public static ModularToolData getData(ItemStack stack) {
        ModularToolData data = stack.get(ModDataComponents.MODULAR_TOOL_DATA.get());
        return data != null ? data : ModularToolData.EMPTY;
    }

    public static void setData(ItemStack stack, ModularToolData data) {
        stack.set(ModDataComponents.MODULAR_TOOL_DATA.get(), data);
    }

    public static int getMaxDurability(ItemStack stack) {
        ModularToolData data = getData(stack);
        Identifier headMat = data.getPartMaterial(PartSlot.HEAD);
        if (headMat == null) return 100;

        int baseDurability = MaterialStatsManager.getStats(headMat)
                .flatMap(MaterialToolStats::head)
                .map(MaterialToolStats.HeadStats::durability)
                .orElse(100);

        float handleMultiplier = 1.0f;
        Identifier handleMat = data.getPartMaterial(PartSlot.HANDLE);
        if (handleMat != null) {
            handleMultiplier = MaterialStatsManager.getStats(handleMat)
                    .flatMap(MaterialToolStats::handle)
                    .map(MaterialToolStats.HandleStats::durabilityMultiplier)
                    .orElse(1.0f);
        }

        int bindingBonus = 0;
        Identifier bindingMat = data.getPartMaterial(PartSlot.BINDING);
        if (bindingMat != null) {
            bindingBonus = MaterialStatsManager.getStats(bindingMat)
                    .flatMap(MaterialToolStats::binding)
                    .map(MaterialToolStats.BindingStats::bonusDurability)
                    .orElse(0);
        }

        int attachmentBonus = 0;
        Identifier tipMat = data.getPartMaterial(PartSlot.TIP);
        if (tipMat != null) {
            attachmentBonus += MaterialStatsManager.getStats(tipMat)
                    .flatMap(MaterialToolStats::attachment)
                    .map(MaterialToolStats.AttachmentStats::bonusDurability)
                    .orElse(0);
        }

        int modifierBonus = data.getModifierLevel(Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "diamond")) * 500;

        return Math.max(1, Math.round(baseDurability * handleMultiplier) + bindingBonus + attachmentBonus + modifierBonus);
    }

    public static float getMiningSpeed(ItemStack stack) {
        ModularToolData data = getData(stack);
        Identifier headMat = data.getPartMaterial(PartSlot.HEAD);
        if (headMat == null) return 1.0f;

        float baseSpeed = MaterialStatsManager.getStats(headMat)
                .flatMap(MaterialToolStats::head)
                .map(MaterialToolStats.HeadStats::miningSpeed)
                .orElse(1.0f);

        float handleMultiplier = 1.0f;
        Identifier handleMat = data.getPartMaterial(PartSlot.HANDLE);
        if (handleMat != null) {
            handleMultiplier = MaterialStatsManager.getStats(handleMat)
                    .flatMap(MaterialToolStats::handle)
                    .map(MaterialToolStats.HandleStats::miningSpeedMultiplier)
                    .orElse(1.0f);
        }

        float hasteBonus = data.getModifierLevel(Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "haste")) * 1.5f;

        return Math.max(1.0f, (baseSpeed * handleMultiplier) + hasteBonus);
    }

    public static float getAttackDamage(ItemStack stack, float baseDamage) {
        ModularToolData data = getData(stack);
        Identifier headMat = data.getPartMaterial(PartSlot.HEAD);
        if (headMat == null) return baseDamage;

        float headDamage = MaterialStatsManager.getStats(headMat)
                .flatMap(MaterialToolStats::head)
                .map(MaterialToolStats.HeadStats::attackDamage)
                .orElse(1.0f);

        float attachmentBonus = 0.0f;
        Identifier tipMat = data.getPartMaterial(PartSlot.TIP);
        if (tipMat != null) {
            attachmentBonus = MaterialStatsManager.getStats(tipMat)
                    .flatMap(MaterialToolStats::attachment)
                    .map(MaterialToolStats.AttachmentStats::attackDamageBonus)
                    .orElse(0.0f);
        }

        float sharpnessBonus = data.getModifierLevel(Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "sharpness")) * 1.25f;

        return baseDamage + headDamage + attachmentBonus + sharpnessBonus;
    }

    public static String getHarvestTier(ItemStack stack) {
        ModularToolData data = getData(stack);
        Identifier tipMat = data.getPartMaterial(PartSlot.TIP);
        if (tipMat != null) {
            Optional<String> override = MaterialStatsManager.getStats(tipMat)
                    .flatMap(MaterialToolStats::attachment)
                    .flatMap(MaterialToolStats.AttachmentStats::tierOverride);
            if (override.isPresent()) {
                return override.get();
            }
        }

        Identifier headMat = data.getPartMaterial(PartSlot.HEAD);
        if (headMat != null) {
            return MaterialStatsManager.getStats(headMat)
                    .flatMap(MaterialToolStats::head)
                    .map(MaterialToolStats.HeadStats::harvestTier)
                    .orElse("iron");
        }
        return "wood";
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return getMaxDurability(stack);
    }

    @Override
    public int getDamage(ItemStack stack) {
        return getData(stack).damage();
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        ModularToolData data = getData(stack);
        setData(stack, data.withDamage(Math.min(damage, getMaxDamage(stack))));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getDamage(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int max = getMaxDamage(stack);
        int current = getDamage(stack);
        return Math.round(13.0F - (float) current * 13.0F / (float) max);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        int max = getMaxDamage(stack);
        int current = getDamage(stack);
        float f = Math.max(0.0F, ((float) max - (float) current) / (float) max);
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (mineableTag != null && state.is(mineableTag)) {
            return getMiningSpeed(stack);
        }
        return 1.0F;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        if (mineableTag != null && state.is(mineableTag)) {
            String tier = getHarvestTier(stack);
            if (state.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return tier.equalsIgnoreCase("diamond") || tier.equalsIgnoreCase("netherite") || tier.equalsIgnoreCase("titanium");
            } else if (state.is(BlockTags.NEEDS_IRON_TOOL)) {
                return !tier.equalsIgnoreCase("wood") && !tier.equalsIgnoreCase("stone");
            } else if (state.is(BlockTags.NEEDS_STONE_TOOL)) {
                return !tier.equalsIgnoreCase("wood");
            }
            return true;
        }
        return false;
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        applyDamage(stack, 2, attacker);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        if (!level.isClientSide() && state.getDestroySpeed(level, pos) != 0.0F) {
            applyDamage(stack, 1, entity);
        }
        return true;
    }

    protected void applyDamage(ItemStack stack, int amount, LivingEntity entity) {
        ModularToolData data = getData(stack);
        int unbreaking = data.getModifierLevel(Identifier.fromNamespaceAndPath(ModernMachines.MOD_ID, "reinforced"));
        if (unbreaking > 0 && entity.getRandom().nextInt(1 + unbreaking) > 0) {
            return; // Durability saved
        }
        stack.hurtAndBreak(amount, entity, EquipmentSlot.MAINHAND);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        ModularToolData data = getData(stack);
        if (data.parts().isEmpty()) {
            tooltip.accept(Component.translatable("tooltip.modernmachines.unassembled_tool").withStyle(ChatFormatting.RED));
            return;
        }

        // Parts Breakdown
        tooltip.accept(Component.translatable("tooltip.modernmachines.parts_header").withStyle(ChatFormatting.GOLD));
        data.parts().forEach((slot, matId) -> {
            tooltip.accept(Component.literal("  ")
                    .append(Component.translatable("part_slot.modernmachines." + slot.getSerializedName()).withStyle(ChatFormatting.GRAY))
                    .append(": ")
                    .append(Component.translatable("material." + matId.getNamespace() + "." + matId.getPath()).withStyle(ChatFormatting.WHITE)));
        });

        // Stats Breakdown
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

        // Modifiers & Upgrade Slots
        int usedSlots = data.getUsedModifierSlots();
        int maxSlots = data.getMaxModifierSlots();
        tooltip.accept(Component.translatable("tooltip.modernmachines.modifiers_header", usedSlots, maxSlots).withStyle(ChatFormatting.AQUA));
        for (ModifierEntry mod : data.modifiers()) {
            tooltip.accept(Component.literal("  - ")
                    .append(Component.translatable("modifier." + mod.id().getNamespace() + "." + mod.id().getPath()))
                    .append(" " + mod.level()).withStyle(ChatFormatting.LIGHT_PURPLE));
        }

        super.appendHoverText(stack, context, display, tooltip, flag);
    }
}
