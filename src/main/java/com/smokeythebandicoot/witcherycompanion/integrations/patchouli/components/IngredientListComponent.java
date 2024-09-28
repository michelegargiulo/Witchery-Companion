package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.components;

import com.google.gson.annotations.SerializedName;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.VariableHolder;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.*;

public class IngredientListComponent implements ICustomComponent {

    // Json variables have to have the @SerializedName annotation
    /** ========== JSON VARIABLES ========== **/

    // The inputs. They are a String array, and can be formatted in multiple ways
    // If items are inserted manually into json, it is recommended to use one element per line
    // "inputs": ["item1", "item2", "item3", ... , "itemN"]
    // If processed by a processor, a String with multiple items can be processed instead. This is because
    // Patchouli limits Processors to return a single String (not String[] or other objects), so we serialize the list
    // So, this works, too
    // "inputs": ["item1,item2,item3", "item4", ... , "itemN"] -- spaces are removed
    // Inputs are not stacks, but ingredients, so they can contain "ore:ingotIron" and they'll be valid
    @SerializedName("inputs")
    @VariableHolder
    public String[] inputs = null;

    @SerializedName("input_spacing")
    @VariableHolder
    public String inputSpacing = null;

    @SerializedName("layout")
    @VariableHolder
    public String layout = null;

    @SerializedName("line_length")
    @VariableHolder
    public int lineLength = 100;

    @SerializedName("output")
    @VariableHolder
    public String output = null;

    @SerializedName("output_offset_x")
    @VariableHolder
    public String outputOffsetX = null;

    @SerializedName("output_offset_y")
    @VariableHolder
    public String outputOffsetY = null;


    /** ========== NON-JSON VARIABLES ========== **/
    private transient int x = 0;
    private transient int y = 0;
    private transient int spacing = 2;
    private transient int offsetX = 2;
    private transient int offsetY = 0;
    private transient List<Ingredient> stacks = null;
    private transient Ingredient outputStack = null;
    private transient GuiBookEntry guiBookEntry = null;


    /** ========== OVERRIDES ========== **/
    /** Called when this component is built. Take the chance to read variables and set
     * any local positions here. */
    @Override
    public void build(int componentX, int componentY, int pageNum) {

        if (inputs == null || output == null) return;

        // Store position variables
        this.x = componentX;
        this.y = componentY;

        try {
            if (inputSpacing != null) this.spacing = Integer.parseInt(inputSpacing);
            if (outputOffsetX != null) this.offsetX = Integer.parseInt(outputOffsetX);
            if (outputOffsetY != null) this.offsetY = Integer.parseInt(outputOffsetY);
        } catch (Exception ex) {
            WitcheryCompanion.logger.warn("Error parsing variables for component IngredientListComponent: ", ex);
        }

        stacks = new ArrayList<>();
        ProcessorUtils.deserializeIngredientList(inputs, stacks);

        if (output != null)
            outputStack = ItemStackUtil.loadIngredientFromString(output);

    }

    /** Called every render tick. No special transformations are applied, so you're responsible
     * for putting everything in the right place. */
    @Override
    public void render(IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
        if (stacks == null || stacks.isEmpty()) return;

        if (guiBookEntry == null && context instanceof GuiBookEntry) {
            this.guiBookEntry = (GuiBookEntry) context;
        }

        int curX = this.x;
        int curY = this.y;
        int lineIndex = 1;

        if (this.isVertical()) {
            for (Ingredient ingredient : stacks) {

                // Custom ingredient rendering, that also renders AIR stacks
                ItemStack[] matchingStacks = ingredient.matchingStacks;
                GlStateManager.pushMatrix();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 0.5f);
                if (matchingStacks.length > 0) {
                    context.renderItemStack(curX, curY, mouseX, mouseY,
                            matchingStacks[guiBookEntry.ticksInBook / 20 % matchingStacks.length]);
                }
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.popMatrix();

                curY += 16 + this.spacing;
                lineIndex++;
                if (lineIndex > lineLength) {
                    lineIndex = 0;
                    curY = this.y;
                    curX += 16 + this.spacing;
                }
            }

        } else {
            for (Ingredient ingredient : stacks) {
                context.renderIngredient(curX, curY, mouseX, mouseY, ingredient);

                curX += 16 + this.spacing;
                lineIndex++;
                if (lineIndex >= lineLength) {
                    lineIndex = 1;
                    curX = this.x;
                    curY += 16 + this.spacing;
                }
            }
        }

        if (outputStack != null) {
            context.renderIngredient(this.x + offsetX, this.y + offsetY, mouseX, mouseY, outputStack);
        }
    }

    protected boolean isVertical() {
        if (this.layout == null) return false;
        return this.layout.equals("vertical");
    }

}
