package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.api.symboleffect.ISymbolEffectAccessor;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.BaseProcessor;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.WitcheryResurrected;
import net.msrandom.witchery.infusion.symbol.BranchStroke;
import net.msrandom.witchery.infusion.symbol.StrokeArray;
import net.msrandom.witchery.infusion.symbol.StrokeSet;
import net.msrandom.witchery.infusion.symbol.SymbolEffect;
import net.msrandom.witchery.resources.SymbolEffectManager;
import vazkii.patchouli.api.IVariableProvider;


public class SymbolEffectProcessor extends BaseProcessor {

    protected String title;
    protected String description;
    protected String strokesDescription;
    protected boolean hasKnowledge;
    protected String hasKnowledgeText;
    protected String knowledgeDescription;
    protected String secretKey = "";
    protected SymbolDrawInfo drawInfo;


    @Override
    public void setup(IVariableProvider<String> provider) {

        String symbolId = readVariable(provider, "symbol_id");
        if (symbolId == null) return;

        SymbolEffect effect = SymbolEffect.REGISTRY.get(new ResourceLocation(WitcheryResurrected.MOD_ID, symbolId));
        if (effect == null) return;

        // Override title and description if not provided by the symbol effect
        this.title = readVariable(provider, "title");
        this.description = readVariable(provider, "description");
        this.strokesDescription = readVariable(provider, "stroke_description");
        this.hasKnowledgeText = readVariable(provider, "has_knowledge_text");

        String[] splits = effect.getDescription().split("\n\n");
        if (splits.length >= 2) {
            if (this.title == null) this.title = ProcessorUtils.reformatPatchouli(splits[0], true);
            if (this.description == null) this.description = splits[1];
            if (this.strokesDescription == null) this.strokesDescription = ProcessorUtils.reformatPatchouli(splits[2], false);
        }

        // Read hasKnowledge vars
        if (effect instanceof ISymbolEffectAccessor) {
            ISymbolEffectAccessor accessor = (ISymbolEffectAccessor) effect;
            this.hasKnowledge = accessor.hasKnowledge();
        }

        // Create information for drawing symbols
        StrokeSet strokes = SymbolEffectManager.INSTANCE.getDefaultStrokes(effect);
        if (strokes != null) {
            this.drawInfo = new SymbolDrawInfo(strokes.getStrokes());
            this.drawInfo.process();
        } else {
            this.drawInfo = null;
        }

        this.isSecret = !effect.isVisible();
        this.secretKey = symbolId;

        this.knowledgeDescription = getDescription();

        super.setup(provider);
    }

    @Override
    public String process(String key) {
        switch (key) {
            case "symbol_name":
                return this.title;
            case "symbol_description":
                return this.description;
            case "stroke_description":
                return this.strokesDescription;
            case "has_knowledge":
                return String.valueOf(this.hasKnowledge);
            case "knowledge_description":
                return this.knowledgeDescription;
            case "strokes":
                if (this.drawInfo == null) return null;
                return ProcessorUtils.serializeStrokeArray(this.drawInfo.strokeArray);
            case "start_x":
                if (this.drawInfo == null) return null;
                return String.valueOf(this.drawInfo.getStartingX());
            case "start_y":
                if (this.drawInfo == null) return null;
                return String.valueOf(this.drawInfo.getStartingY());
            default:
                return super.process(key);
        }
    }

    @Override
    protected String getSecretKey() {
        return ProgressUtils.getSymbolEffectSecret(this.secretKey);
    }

    @Override
    protected void obfuscateFields() {
        this.title = obfuscate(this.title, EObfuscationMethod.MINECRAFT);
        this.description = obfuscate(this.description, EObfuscationMethod.PATCHOULI);
        this.strokesDescription = obfuscate(this.strokesDescription, EObfuscationMethod.PATCHOULI);
        this.hasKnowledge = false;
        this.hasKnowledgeText = "";
        this.drawInfo = null;
    }

    @Override
    protected void hideFields() {
        this.title = "";
        this.description = "";
        this.strokesDescription = "";
        this.hasKnowledge = false;
        this.hasKnowledgeText = "";
        this.drawInfo = null;
    }

    public String getDescription() {
        if (isSecret && hasKnowledge) return secretText + ", " + hasKnowledgeText;
        if (isSecret) return secretText;
        if (hasKnowledge) return hasKnowledgeText;
        return "";
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