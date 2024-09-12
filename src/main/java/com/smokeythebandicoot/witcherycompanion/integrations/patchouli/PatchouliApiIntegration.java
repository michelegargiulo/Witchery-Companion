package com.smokeythebandicoot.witcherycompanion.integrations.patchouli;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.PatchouliIntegration.Flags;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.bookcomponents.ColorableImage;
import com.smokeythebandicoot.witcherycompanion.utils.ReflectionHelper;
import com.smokeythebandicoot.witcherycompanion.utils.RomanNumbers;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.brewing.action.BrewAction;
import net.msrandom.witchery.infusion.symbol.SymbolEffect;
import net.msrandom.witchery.init.data.recipes.WitcheryRecipeTypes;
import net.msrandom.witchery.recipe.CauldronRecipe;
import net.msrandom.witchery.resources.BrewActionManager;
import net.msrandom.witchery.util.WitcheryUtils;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.template.BookTemplate;
import vazkii.patchouli.client.book.text.BookTextParser;
import vazkii.patchouli.client.book.text.SpanState;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


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
     * Should be called onWorldLoad as late as possible, when Witchery Registries are populated.
     * Should be updated when recipes are reloaded on when players log in*/
    public static void reloadAllFlags() {
        symbolEffectReloader.reloadFlags();
        brewActionReloader.reloadFlags();
        PatchouliAPI.instance.reloadBookContents();
    }

    public static final FlagReloader<ResourceLocation, SymbolEffect> symbolEffectReloader = new FlagReloader<>(
            SymbolEffect.REGISTRY::iterator,
            ResourceLocation::getPath,
            "content/symbols/"
    );

    public static final FlagReloader<String, BrewAction> brewActionReloader = new FlagReloader<>(
            () -> BrewActionManager.INSTANCE.getActions().stream()
                    .collect(Collectors.toMap(
                            // Key mapper
                            brewAction -> ItemStackUtil.serializeStack(brewAction.getKey().toStack()),
                            // Value mapper
                            Function.identity())
                    ).entrySet().iterator(),
            Function.identity(), "content/brew_actions/"
    );

    public static final FlagReloader<String, CauldronRecipe> cauldronRecipeReloader = new FlagReloader<>(
            () -> WitcheryUtils.getRecipeManager(null).getRecipesForType(WitcheryRecipeTypes.CAULDRON).stream()
                    .collect(Collectors.toMap(
                            cauldronRecipe -> cauldronRecipe.getId().path,
                            Function.identity()
                        )
                    ).entrySet().iterator(),
                Function.identity(),
                "content/cauldron_recipes/"
    );


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



    public static class FlagReloader<K, V> {

        private final String prefix;
        private final Iterator<Map.Entry<K, V>> iterator;
        private final Function<K, String> transformer;
        private final Function<FlaggerContext<K, V>, Boolean> flagger;
        private final Map<String, Boolean> flags;

        /**
         * @param iteratorSupplier a function that takes no inputs and outputs an Iterator of the correct type. Used to
         *                         store the way to retrieve the iterator, for example () -> SymbolEffect.REGISTRY.iterator()
         * @param transformer a Function that transforms the key (of type K) into a unique string, for flag IDs
         * @param prefix Should be in the format "<domain>/<subdomain>/.../". For example brewing/items/
         *               It should not be prefixed, as it is done automatically. Flag ID is appended at the end
         */
        public FlagReloader(Supplier<Iterator<Map.Entry<K, V>>> iteratorSupplier, Function<K, String> transformer, String prefix) {
            this(iteratorSupplier, transformer, prefix, key -> true);
        }

        /**
         * @param iteratorSupplier a function that takes no inputs and outputs an Iterator of the correct type. Used to
         *                         store the way to retrieve the iterator, for example () -> SymbolEffect.REGISTRY.iterator()
         * @param transformer a Function that transforms the key (of type K) into a unique string, for flag IDs
         * @param prefix Should be in the format "<domain>/<subdomain>/.../". For example brewing/items/
         *               It should not be prefixed, as it is done automatically. Flag ID is appended at the end
         * @param flagger A function that takes a FlaggerContext as input (a POJO class containing information about
         *                element Key, Value and flagId) and returns a boolean. To use in case flags should not be set
         *                all to true (like enabled content)
         */
        public FlagReloader(Supplier<Iterator<Map.Entry<K, V>>> iteratorSupplier, Function<K, String> transformer, String prefix, Function<FlaggerContext<K, V>, Boolean> flagger) {
            flags = new HashMap<>();
            this.transformer = transformer;
            this.flagger = flagger;
            this.prefix = WitcheryCompanion.prefix(prefix);
            this.iterator = iteratorSupplier.get();
        }

        public boolean getFlag(String flag) {
            if (flags.containsKey(flag)) return false;
            Boolean flagValue = flags.get(this.prefix + flag);
            if (flagValue == null) return false;
            return flagValue;
        }

        public void reloadFlags() {

            // Clear all the flags
            for (String flag : flags.keySet()) {
                PatchouliAPI.instance.setConfigFlag(flag, false);
            }
            flags.clear();

            // Rebuild them
            while (iterator.hasNext()) {
                Map.Entry<K, V> entry = iterator.next();
                String flagId = prefix + transformer.apply(entry.getKey());
                FlaggerContext<K, V> context = new FlaggerContext<>(flagId, entry.getKey(), entry.getValue());
                flags.put(flagId, flagger.apply(context));
            }

            // Use the PatchouliAPIIntegration to update all flags and reload book contents
            updateFlags(flags);
        }


        public static class FlaggerContext<K, V> {
            String flagId;
            K key;
            V value;

            public FlaggerContext(String flagId, K key, V value) {
                this.flagId = flagId;
                this.key = key;
                this.value = value;
            }
        }

    }

}
