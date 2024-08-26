package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import net.msrandom.witchery.infusion.symbol.BranchStroke;
import net.msrandom.witchery.infusion.symbol.StrokeArray;
import net.msrandom.witchery.infusion.symbol.StrokeSet;
import net.msrandom.witchery.infusion.symbol.SymbolEffect;
import net.msrandom.witchery.resources.SymbolEffectManager;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;

import java.util.HashMap;
import java.util.Map;


/** This processor is responsible for generating a list of strokes for a SymbolStrokesComponent from a Symbol Effect **/
public class SymbolProcessor implements IComponentProcessor {

    private String symbolId = null;

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
            case "strokes":
                return ProcessorUtils.serializeStrokeArray(this.currentSymbol.drawInfo.strokeArray);
            case "start_x":
                return String.valueOf(this.currentSymbol.drawInfo.startingX);
            case "start_y":
                return String.valueOf(this.currentSymbol.drawInfo.startingY);
        }
        return null;
    }

    private static void updateSymbolMap() {
        symbolMap = new HashMap<>();
        for (StrokeArray strokes : SymbolEffectManager.INSTANCE.getEffects().keySet()) {
            StrokeSet strokeSet = SymbolEffectManager.INSTANCE.getEffect(strokes);
            if (strokeSet != null) {
                SymbolEffect effect = strokeSet.getResult();
                StrokeSet defaultStrokes = SymbolEffectManager.INSTANCE.getDefaultStrokes(effect);
                SymbolEffectInfo symbolEffectInfo = new SymbolEffectInfo(effect,
                        defaultStrokes == null ? strokes : defaultStrokes.getStrokes());
                symbolMap.put(symbolEffectInfo.effectId, symbolEffectInfo);
            }
        }
    }


    /** This class holds information about symbol effects, such as name, description and its strokes **/
    public static class SymbolEffectInfo {

        public final String effectName;
        public final String effectId;
        public final String effectDescription;
        public final SymbolDrawInfo drawInfo;

        public SymbolEffectInfo(SymbolEffect effect, StrokeArray strokes) {
            String name = "<no name>";
            String desc = "<no desc>";
            if (effect != null) {
                String[] splits = effect.getDescription().split("\n\n");
                if (splits.length > 2) {
                    name = splits[0]
                            .replace("§n", "")
                            .replace("§r", "")
                            .trim();
                    desc = splits[1];
                    // strokes is splits[2], but we have StrokeArray anyway
                }
            }
            this.effectId = name.toLowerCase().replace(" ", "_");
            this.effectName = name;
            this.effectDescription = desc;
            this.drawInfo = new SymbolDrawInfo(strokes);
            this.drawInfo.process();
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
