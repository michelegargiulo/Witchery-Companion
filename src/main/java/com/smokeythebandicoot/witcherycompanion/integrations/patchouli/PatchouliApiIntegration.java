package com.smokeythebandicoot.witcherycompanion.integrations.patchouli;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.progress.*;
import com.smokeythebandicoot.witcherycompanion.api.spiriteffect.SpiritEffectApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.bookcomponents.ColorableImage;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.items.ItemTornPage;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.*;
import com.smokeythebandicoot.witcherycompanion.network.ProgressSync;
import com.smokeythebandicoot.witcherycompanion.utils.ReflectionHelper;
import com.smokeythebandicoot.witcherycompanion.utils.RomanNumbers;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.msrandom.witchery.brewing.action.BrewAction;
import net.msrandom.witchery.infusion.spirit.SpiritEffectRecipe;
import net.msrandom.witchery.infusion.symbol.SymbolEffect;
import net.msrandom.witchery.init.data.recipes.WitcheryRecipeTypes;
import net.msrandom.witchery.recipe.CauldronRecipe;
import net.msrandom.witchery.resources.BrewActionManager;
import net.msrandom.witchery.util.WitcheryUtils;
import vazkii.patchouli.api.BookContentsReloadEvent;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookLanding;
import vazkii.patchouli.client.book.template.BookTemplate;
import vazkii.patchouli.client.book.text.BookTextParser;
import vazkii.patchouli.client.book.text.SpanState;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.smokeythebandicoot.witcherycompanion.api.progress.CapabilityWitcheryProgress.WITCHERY_PROGRESS_CAPABILITY;


public class PatchouliApiIntegration {

    private static boolean readyToReload = false;

    private PatchouliApiIntegration() { }

    public static void registerCustomComponents() {
        BookTemplate.registerComponent("colored_image", ColorableImage.class);
    }

    public static void registerCustomMacros() {
        ReflectionHelper.invokeStaticMethod(BookTextParser.class, "register",
                new Class<?>[] { BookTextParser.FunctionProcessor.class, String[].class },
                false, new RomanNumberFunction(), new String[] {"roman"});
    }

    public static void updateFlag(String flag, boolean value) {
        PatchouliAPI.instance.setConfigFlag(WitcheryCompanion.prefix(flag), value);
    }

    public static void updateFlags(Map<String, Boolean> flags) {
        for (String flag : flags.keySet()) {
            PatchouliAPI.instance.setConfigFlag(WitcheryCompanion.prefix(flag), flags.get(flag));
        }
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
                            cauldronRecipe -> cauldronRecipe.getId().getPath(),
                            Function.identity()
                        )
                    ).entrySet().iterator(),
                Function.identity(),
                "content/cauldron_recipes/"
    );

    public static final FlagReloader<ResourceLocation, SpiritEffectRecipe> spiritEffectReloader = new FlagReloader<>(
            SpiritEffectApi::getIterator,
            ResourceLocation::getPath,
            "content/spirit_effect_recipes/"
    );

    /** When book reload is requested, clear caches and reload processors **/
    @SubscribeEvent
    public static void onBookReload(BookContentsReloadEvent event) {
        // Clear cache of all the processors that implement caching
        if (!event.book.getNamespace().equals(WitcheryCompanion.MODID)) return;
        if (readyToReload) {
            CapacityBrewActionProcessor.clearCache();
            ModifierBrewActionProcessor.clearCache();
            UpgradeBrewActionProcessor.clearCache();
            DispersalBrewActionProcessor.clearCache();
            IncrementBrewActionProcessor.clearCache();
            MultiblockRegistry.reloadMultiblocks();
            WitcheryCompanion.logger.debug("Patchouli Book Reloaded");
            //Utils.logChat("Patchouli Book Reloaded");
        }

        /* Maybe in the future, implement a custom book reloader. Probably Patchouli code will have to
            be changed with Mixins or a Fork. Inject into BookContents.reload(), some work needs to be
            put in loadEntry and entry.build(). For now, modpack makers that change brew effect levels
            will need to override the entire category of the book.
        Book book = ItemModBook.getBook(ItemModBook.forBook(event.book.toString()));
        BookContents contents = book.contents;
        for (BookCategory category : contents.categories.values()) {
            if (category.getName().equals("level_1")) {
                category.getEntries().clear();
                category.addEntry(new BookEntry());
            }
        }
        */
    }

    /** Only when player has logged-in in a world (and book content can be displayed) we should be able
     * to call a book reload for Witchery Companion, as Witchery loads content on this very event **/
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerJoinedWorld(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        readyToReload = true;

        // Update reloaders flags
        cauldronRecipeReloader.reloadFlags(false);
        spiritEffectReloader.reloadFlags(false);
        brewActionReloader.reloadFlags(false);
        symbolEffectReloader.reloadFlags(false);
        // Reloading here is not necessary

        // Update Immortal Book flags if revamp is enabled
        if (ModConfig.IntegrationConfigurations.PatchouliIntegration.common_replaceImmortalsBook) {
            updateImmortalBookFlags(player);
        }
    }

    /** Anytime the progress is updated, some Patchouli pages need to be reloaded, so mark the book as dirty **/
    @SubscribeEvent
    public static void onProgressReset(WitcheryProgressResetEvent event) {
        updateImmortalBookFlags(event.player, 0);
    }

    /** Anytime the progress is updated, some Patchouli pages need to be reloaded, so mark the book as dirty **/
    @SubscribeEvent
    public static void onProgressLock(WitcheryProgressLockEvent event) {
        if (event.progressKey.contains("observations/immortal/level_"))
            updateImmortalBookFlags(event.player);
    }

    /** Anytime the progress is updated, some Patchouli pages need to be reloaded, so mark the book as dirty **/
    @SubscribeEvent
    public static void onProgressUpdate(WitcheryProgressUnlockEvent event) {
        if (event.progressKey.contains("observations/immortal/level_"))
            updateImmortalBookFlags(event.player);
    }

    /** This is only called server-side, but Patchouli handles it client-side
     * This is a special case for the ImmortalBook progress reloader **/
    public static void updateImmortalBookFlags(EntityPlayer player, int progressNum) {
        for (int i = 1; i <= 10; i++) {
            String progressKey = ItemTornPage.VAMPIRE_PAGE_PROGRESS + i;
            PatchouliAPI.instance.setConfigFlag(progressKey, i <= progressNum);
        }
        //PatchouliAPI.instance.reloadBookContents(); // Must reload here when the book is closed
    }

    /** This is only called server-side, but Patchouli handles it client-side
     * This is a special case for the ImmortalBook progress reloader. This function
     * computes the current progress from the capability **/
    public static void updateImmortalBookFlags(EntityPlayer player) {
        int progressNum = 1;
        IWitcheryProgress progress = player.getCapability(WITCHERY_PROGRESS_CAPABILITY, null);
        if (progress != null) {
            while (progressNum <= 10) {
                String progressKey = ItemTornPage.VAMPIRE_PAGE_PROGRESS + progressNum;
                if (!progress.hasProgress(progressKey)) {
                    break;
                }
                progressNum++;
            }
        }

        updateImmortalBookFlags(player, progressNum);
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


    /** Class that holds information about how to information about a registry and
     * reloads Patchouli flags based on its contents. Used to update flags that enable/disable content
     * (pages, components, etc) based on what content is enabled */
    public static class FlagReloader<K, V> {

        private final String prefix;
        private final Supplier<Iterator<Map.Entry<K, V>>> iteratorSupplier;
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
            this.prefix = prefix; // Witchery:Companion modid should not be added
            this.iteratorSupplier = iteratorSupplier;
        }

        public void reloadFlags(boolean reloadBook) {
            // Clear all the flags
            for (String flag : flags.keySet()) {
                PatchouliAPI.instance.setConfigFlag(flag, false);
            }
            flags.clear();

            // Reset iterator and Rebuild them
            Iterator<Map.Entry<K, V>> iterator = iteratorSupplier.get();
            while (iterator.hasNext()) {
                Map.Entry<K, V> entry = iterator.next();
                String flagId = prefix + transformer.apply(entry.getKey());
                FlaggerContext<K, V> context = new FlaggerContext<>(flagId, entry.getKey(), entry.getValue());
                flags.put(flagId, flagger.apply(context));
            }

            // Use the PatchouliAPIIntegration to update all flags and reload book contents
            updateFlags(flags);

            // Reload book if necessary
            // Recommended when multiple reloaders are called in sequence
            if (reloadBook) {
                PatchouliAPI.instance.reloadBookContents();
            }
        }

        public void reloadFlags() {
            reloadFlags(true);
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
