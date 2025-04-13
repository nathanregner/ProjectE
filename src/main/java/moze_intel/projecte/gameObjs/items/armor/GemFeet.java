package moze_intel.projecte.gameObjs.items.armor;

import com.google.common.base.Suppliers;
import java.util.List;
import java.util.function.Supplier;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.common.NeoForgeMod;

import org.jetbrains.annotations.NotNull;

public class GemFeet extends GemArmorBase {

	private static final boolean STEP_ASSIST_DEFAULT = false;

	private final Supplier<ItemAttributeModifiers> defaultModifiers;
	private final Supplier<ItemAttributeModifiers> defaultWithStepAssistModifiers;

	public GemFeet(Properties props) {
		super(ArmorItem.Type.BOOTS, props.component(PEDataComponentTypes.STEP_ASSIST, STEP_ASSIST_DEFAULT));
		this.defaultModifiers = Suppliers.memoize(() -> super.getDefaultAttributeModifiers()
				.withModifierAdded(
						Attributes.MOVEMENT_SPEED,
						new AttributeModifier(PECore.rl("armor"), 1.0, Operation.ADD_MULTIPLIED_TOTAL),
						EquipmentSlotGroup.FEET
				)
				.withModifierAdded(
						NeoForgeMod.CREATIVE_FLIGHT,
						new AttributeModifier(PECore.rl("gem_flight"), 1, Operation.ADD_VALUE),
						EquipmentSlotGroup.FEET
				));
		this.defaultWithStepAssistModifiers = Suppliers.memoize(() -> getDefaultAttributeModifiers().withModifierAdded(
				Attributes.STEP_HEIGHT,
				new AttributeModifier(PECore.rl("gem_step_assist"), 0.4, Operation.ADD_VALUE),
				EquipmentSlotGroup.FEET
		));
	}

	@NotNull
	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers() {
		return this.defaultModifiers.get();
	}

	@NotNull
	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers(@NotNull ItemStack stack) {
		return isStepAssist(stack) ? this.defaultWithStepAssistModifiers.get() : super.getDefaultAttributeModifiers(stack);
	}

	public static void toggleStepAssist(ItemStack boots, Player player) {
		boolean oldValue = isStepAssist(boots);
		boots.set(PEDataComponentTypes.STEP_ASSIST, !oldValue);
		player.sendSystemMessage(getComponent(!oldValue));
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, context, tooltip, flags);
		tooltip.add(PELang.GEM_LORE_FEET.translate());
		tooltip.add(PELang.STEP_ASSIST_PROMPT.translate(ClientKeyHelper.getKeyName(PEKeybind.BOOTS_TOGGLE)));
		tooltip.add(getComponent(isStepAssist(stack)));
	}

	private static boolean isStepAssist(ItemStack stack) {
		return stack.getOrDefault(PEDataComponentTypes.STEP_ASSIST, STEP_ASSIST_DEFAULT);
	}

	private static Component getComponent(boolean enabled) {
		if (enabled) {
			return PELang.STEP_ASSIST.translate(ChatFormatting.GREEN, PELang.GEM_ENABLED);
		}
		return PELang.STEP_ASSIST.translate(ChatFormatting.RED, PELang.GEM_DISABLED);
	}
}
