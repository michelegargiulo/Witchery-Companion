package com.smokeythebandicoot.witcherycompanion.integrations.patchouli;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.PatchouliIntegration.Flags;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.bookcomponents.ColorableImage;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.SymbolEffectProcessor;
import com.smokeythebandicoot.witcherycompanion.utils.ReflectionHelper;
import com.smokeythebandicoot.witcherycompanion.utils.RomanNumbers;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.msrandom.witchery.infusion.symbol.StrokeArray;
import net.msrandom.witchery.infusion.symbol.SymbolEffect;
import net.msrandom.witchery.resources.SymbolEffectManager;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.template.BookTemplate;
import vazkii.patchouli.client.book.text.BookTextParser;
import vazkii.patchouli.client.book.text.SpanState;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mod.EventBusSubscriber()
public class PatchouliApiIntegration {

    private PatchouliApiIntegration() { }

    public static void registerCustomComponents() {
        BookTemplate.registerComponent("colored_image", ColorableImage.class);
    }

    public static void registerCustomMacros() {
        ReflectionHelper.invokeStaticMethod(BookTextParser.class, "register",
                new Class<?>[] { BookTextParser.FunctionProcessor.class, String[].class },
                false, new RomanNumberFunction(), new String[] {"roman"});
    }

    // Called from Proxy
    public static void registerFlags() {
        PatchouliAPI.IPatchouliAPI api = PatchouliAPI.instance;

        registerFlag("brewing/expertise",  Flags.brewing_enableExpertiseExtension);
        registerFlag("brewing/rituals", Flags.brewing_enableRitualsExtension);
        registerFlag("brewing/show_ceiling", Flags.brewing_revealRemoveCeiling);
        registerFlag("conjuring/show_extra", Flags.conjuring_showExtraEntity);
        registerFlag("conjuring/extended_intro", Flags.conjuring_enableExtendedIntro);
        registerFlag("conjuring/extended_fetish", Flags.conjuring_enableFetishExtension);
        registerFlag("symbology/extended_intro", Flags.symbology_enableExtendedIntro);
        registerFlag("symbology/stroke_visualization", Flags.symbology_enableStrokeVisualization);
        registerFlag("symbology/show_secret", Flags.symbology_showSecret);
        registerFlag("symbology/show_knowledge", Flags.symbology_showKnowledge);

    }

    private static void registerFlag(String flag, boolean flagValue) {
        PatchouliAPI.instance.setConfigFlag(WitcheryCompanion.prefix(flag), flagValue);
    }

    public static void updateFlag(String flag, boolean value) {
        PatchouliAPI.instance.setConfigFlag(WitcheryCompanion.prefix(flag), value);
        PatchouliAPI.instance.reloadBookContents();
    }

    public static void updateFlags(Map<String, Boolean> flags) {
        for (String flag : flags.keySet()) {
            PatchouliAPI.instance.setConfigFlag(WitcheryCompanion.prefix(flag), flags.get(flag));
        }
        PatchouliAPI.instance.reloadBookContents();
    }

    /** This function reloads all Witchery Content Flags (enabled Spell effects, items, etc)
     * Should be called onWorldLoad as late as possible, when Witchery Registries are populated */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void reloadWitcheryFlags(WorldEvent.Load event) {
        updateSymbolEffectFlags();
    }

    /** Sets the flag "witcherycompanion:symbols/<symbolId> to true for all enabled symbol effects */
    public static void updateSymbolEffectFlags() {
        Iterator<Map.Entry<ResourceLocation, SymbolEffect>> iterator = SymbolEffect.REGISTRY.iterator();
        while (iterator.hasNext()) {
            Map.Entry<ResourceLocation, SymbolEffect> entry = iterator.next();
            ResourceLocation location = entry.getKey();
            String effectId = location.path;
            registerFlag("symbols/" + effectId, true);
        }
    }


    /** Custom FunctionProcessor for Patchouli's TextParser
     * Usage: $(roman)<string>$(). While active, all numbers within the 'string' will be converted
     * into roman numbers. Useful for dynamic Enchantment or Potion levels */
    private static class RomanNumberFunction implements BookTextParser.FunctionProcessor {

        private static final Pattern pattern = Pattern.compile("[0-9]+");

        @Override
        public String process(String s, SpanState spanState) {
            Matcher matcher = pattern.matcher(s);
            StringBuffer buffer = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(buffer, RomanNumbers.toRoman(Integer.parseInt(matcher.group())));
            }
            matcher.appendTail(buffer);
            return buffer.toString();
        }
    }

}
