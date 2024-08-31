package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.api.symboleffect.ISymbolEffectAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.proxy.ClientProxy;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.infusion.symbol.BranchStroke;
import net.msrandom.witchery.infusion.symbol.StrokeArray;
import net.msrandom.witchery.infusion.symbol.StrokeSet;
import net.msrandom.witchery.infusion.symbol.SymbolEffect;
import net.msrandom.witchery.resources.SymbolEffectManager;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/** This processor is responsible for generating a list of strokes for a SymbolStrokesComponent from a Symbol Effect **/
public class SymbolEffectProcessor implements IComponentProcessor {

    private String symbolId = null;
    private boolean shouldShow = true;
    private SymbolEffectInfo currentSymbol = null;

    private static Map<String, SymbolEffectInfo> symbolMap = null;


    /** ========== OVERRIDES ========== **/
    @Override
    public void setup(IVariableProvider<String> iVariableProvider) {

        // Read first output
        if (iVariableProvider.has("symbol")) {
            this.symbolId = iVariableProvider.get("symbol");
        }

        // Init data structures for first time
        if (symbolMap == null || symbolMap.isEmpty()) {
            updateSymbolMap();
        }

        // Retrieve symbol and its information
        if (!symbolMap.containsKey(symbolId))
            return;

        // Store current symbol variables
        this.currentSymbol = symbolMap.get(symbolId);

        this.shouldShow = shouldShow(currentSymbol);
    }

    @Override
    public String process(String key) {
        if (this.currentSymbol == null)
            return null;
        switch (key) {
            case "symbol_name":
                return this.currentSymbol.effectName;
            case "symbol_description":
                return this.currentSymbol.effectDescription;
            case "stroke_description":
                return this.currentSymbol.strokeDescription;
            case "guard":
                return this.shouldShow ? "true" : "";
            case "strokes":
                if (this.currentSymbol.drawInfo == null) return null;
                return ProcessorUtils.serializeStrokeArray(this.currentSymbol.drawInfo.strokeArray);
            case "start_x":
                if (this.currentSymbol.drawInfo == null) return null;
                return String.valueOf(this.currentSymbol.drawInfo.startingX);
            case "start_y":
                if (this.currentSymbol.drawInfo == null) return null;
                return String.valueOf(this.currentSymbol.drawInfo.startingY);
        }
        return null;
    }

    @Override
    public boolean allowRender(String group) {
        if (group.equals("secret_group"))
            return this.currentSymbol == null || this.currentSymbol.secret;
        else if (group.equals("knowledge_group"))
            return this.currentSymbol == null || this.currentSymbol.hasKnowledge;
        return true;
    }


    private static void updateSymbolMap() {
        symbolMap = new HashMap<>();

        Iterator<Map.Entry<ResourceLocation, SymbolEffect>> iterator = SymbolEffect.REGISTRY.iterator();
        while (iterator.hasNext()) {
            Map.Entry<ResourceLocation, SymbolEffect> entry = iterator.next();
            ResourceLocation location = entry.getKey();
            String effectId = location.path;
            SymbolEffect effect = entry.getValue();
            SymbolEffectInfo info = new SymbolEffectInfo(effect);
            symbolMap.put(effectId, info);
        }
    }

    private boolean shouldShow(SymbolEffectInfo info) {
        // If not secret, it means it is written in the manual, so show it
        if (!info.secret)
            return true;

        // Otherwise, Check if secrets should always be shown
        ModConfig.IntegrationConfigurations.PatchouliIntegration.EPatchouliSecretPolicy policy = ModConfig.IntegrationConfigurations.PatchouliIntegration.common_showSecretsPolicy;
        if (policy == ModConfig.IntegrationConfigurations.PatchouliIntegration.EPatchouliSecretPolicy.ALWAYS_SHOW)
            return true;

        // If policy is not ALWAYS HIDDEN, then check progress to see if visible
        return policy == ModConfig.IntegrationConfigurations.PatchouliIntegration.EPatchouliSecretPolicy.PROGRESS && hasUnlockedProgress(info);
    }

    private static boolean hasUnlockedProgress(SymbolEffectInfo info) {
        // Get secret key and return true if the corresponding element has been found
        String key = ProgressUtils.getSymbolEffectSecret(info.effectId);
        return ClientProxy.getLocalWitcheryProgress().hasProgress(key);
    }

    /** This class holds information about symbol effects, such as name, description and its strokes **/
    public static class SymbolEffectInfo {

        public final String effectName;
        public final String effectId;
        public final String effectDescription;
        public final String strokeDescription;
        public final boolean hasKnowledge;
        public final boolean secret;
        public final SymbolDrawInfo drawInfo;

        public SymbolEffectInfo(SymbolEffect effect) {
            String name = "<no name>";
            String desc = "<no desc>";
            String strokeDesc = "<no symbols>";
            boolean secret = false;
            if (effect != null) {
                String[] splits = effect.getDescription().split("\n\n");
                if (splits.length > 2) {
                    name = ProcessorUtils.reformatPatchouli(splits[0], true);
                    desc = splits[1];
                    strokeDesc = ProcessorUtils.reformatPatchouli(splits[2], false);
                }
                secret = !effect.isVisible();
            }

            if (effect instanceof ISymbolEffectAccessor) {
                ISymbolEffectAccessor accessor = (ISymbolEffectAccessor) effect;
                this.hasKnowledge = accessor.hasKnowledge();
            } else {
                this.hasKnowledge = false;
            }

            this.effectId = name.toLowerCase().replace(" ", "_");
            this.effectName = name;
            this.effectDescription = desc;
            this.strokeDescription = strokeDesc;
            this.secret = secret;

            // Stroke info
            StrokeSet strokes = SymbolEffectManager.INSTANCE.getDefaultStrokes(effect);

            if (strokes != null) {
                this.drawInfo = new SymbolDrawInfo(strokes.getStrokes());
                this.drawInfo.process();
            } else {
                this.drawInfo = null;
            }
        }
    }

    /** This class computes and holds information about which stroke to draw and where to draw it.
     * SymbolComponents are responsible to actually render them on screen **/
    public static class SymbolDrawInfo {

        private int startingX = 0;
        private int startingY = 0;

        private final StrokeArray strokeArray;

        public SymbolDrawInfo(StrokeArray strokes) {
            this.strokeArray = strokes;
        }

        public void process() {
            int curX = 0;
            int curY = 0;

            int minX = 0;
            int minY = 0;

            for (BranchStroke stroke : strokeArray) {
                switch (stroke) {
                    case DOWN:
                        curY += 1;
                        break;
                    case UP:
                        curY -= 1;
                        break;
                    case RIGHT:
                        curX += 1;
                        break;
                    case LEFT:
                        curX -= 1;
                        break;
                    case UP_LEFT:
                        curX -= 1;
                        curY -= 1;
                        break;
                    case UP_RIGHT:
                        curX += 1;
                        curY -= 1;
                        break;
                    case DOWN_LEFT:
                        curX -= 1;
                        curY += 1;
                        break;
                    case DOWN_RIGHT:
                        curX += 1;
                        curY += 1;
                        break;
                }

                // Update bounds
                if (curX < minX) minX = curX;
                if (curY < minY) minY = curY;
            }

            startingX = -minX;
            startingY = -minY;
        }

        public int getStartingY() {
            return startingY;
        }

        public int getStartingX() {
            return startingX;
        }
    }

}
