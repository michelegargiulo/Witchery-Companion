package com.smokeythebandicoot.witcherycompanion.mixins.resources;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.mutations.IMutationManagerAccessor;
import com.smokeythebandicoot.witcherycompanion.api.mutations.MutationRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.resources.JsonReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.msrandom.witchery.mutation.MutationPattern;
import net.msrandom.witchery.resources.MutationManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;


/**
 * Mixins:
 * [Feature] Add an ID to identity the mutation. Ignore the private included data structure and use an HashMap instead
 * [Feature] Implement accessor to access the mutation by ID (ResourceLocation)
 */
@Mixin(MutationManager.class)
public abstract class MutationManagerMixin extends JsonReloadListener implements IMutationManagerAccessor {

    @Unique
    private static final HashMap<ResourceLocation, MutationPattern> witchery_Patcher$mutations = new HashMap<>();

    @Unique
    private ResourceLocation witchery_Patcher$currentKey = null;

    @Unique
    private String[][] witchery_Patcher$currentPattern = null;


    private MutationManagerMixin(Gson gson, String folder) {
        super(gson, folder);
    }


    /** This Mixin clears the new data structure instead of the HashSet **/
    @WrapOperation(method = "apply(Ljava/util/Map;Lnet/minecraft/resources/ResourceManager;)V", remap = false,
            at = @At(value = "INVOKE", target = "Ljava/util/HashSet;clear()V"))
    private void apply(HashSet<?> instance, Operation<Void> original) {
        witchery_Patcher$mutations.clear();
        MutationRegistry.mutations.clear();
    }

    /** This Mixin grabs the Json key (the filename) **/
    @WrapOperation(method = "apply(Ljava/util/Map;Lnet/minecraft/resources/ResourceManager;)V", remap = false,
            at = @At(value = "INVOKE", target = "Ljava/util/Map$Entry;getKey()Ljava/lang/Object;", ordinal = 0))
    private Object grabJsonKey(Map.Entry<?, ?> instance, Operation<ResourceLocation> original) {
        ResourceLocation result = original.call(instance);
        this.witchery_Patcher$currentKey = result;
        return result;
    }

    /** This Mixin adds the new mutation to the new data structure instead of the Hashset **/
    @WrapOperation(method = "apply(Ljava/util/Map;Lnet/minecraft/resources/ResourceManager;)V", remap = false,
            at = @At(value = "INVOKE", target = "Ljava/util/HashSet;add(Ljava/lang/Object;)Z"))
    private boolean addToHashmap(HashSet<MutationPattern> instance, Object pattern, Operation<Boolean> original) {
        if (this.witchery_Patcher$currentKey != null) {
            witchery_Patcher$mutations.put(this.witchery_Patcher$currentKey, (MutationPattern) pattern);
            return true;
        }
        return false;
    }

    /** This Mixin prints the correct number of mutations in the log. Also releases the grabbed ResourceLocation
     * key and computed pattern to prepare the variables for the next iteration, as the HashSet.size()
     * is one of the last instruction of the method **/
    @WrapOperation(method = "apply(Ljava/util/Map;Lnet/minecraft/resources/ResourceManager;)V", remap = false,
            at = @At(value = "INVOKE", target = "Ljava/util/HashSet;size()I"))
    private int printCorrectSize(HashSet<MutationPattern> instance, Operation<Integer> original) {
        this.witchery_Patcher$currentKey = null; // Release the key for next iteration
        this.witchery_Patcher$currentPattern = null;
        return witchery_Patcher$mutations.size();
    }

    /** This Mixin replaces the HashSet iterator with the HashMap.values() iterator **/
    @WrapOperation(method = "findMutation", remap = false, at = @At(value = "INVOKE",
            target = "Ljava/util/HashSet;iterator()Ljava/util/Iterator;"))
    private Iterator<MutationPattern> buildIterator(HashSet<MutationPattern> instance, Operation<Iterator<MutationPattern>> original) {
        return witchery_Patcher$mutations.values().iterator();
    }

    @Override
    public MutationPattern getMutation(ResourceLocation id) {
        return witchery_Patcher$mutations.getOrDefault(id, null);
    }


    @WrapOperation(method = "apply(Ljava/util/Map;Lnet/minecraft/resources/ResourceManager;)V", remap = false,
    at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonObject;get(Ljava/lang/String;)Lcom/google/gson/JsonElement;", ordinal = 0))
    private JsonElement readPatternFromJson(JsonObject instance, String memberName, Operation<JsonElement> original) {
        JsonElement pattern = original.call(instance, memberName);

        // Only 1 layer
        if (pattern.isJsonArray()) {
            this.witchery_Patcher$currentPattern = new String[1][];
            this.witchery_Patcher$currentPattern[0] = witchery_Patcher$readJsonArray(pattern.getAsJsonArray());
        }

        // Multiple layers
        else {
            this.witchery_Patcher$currentPattern = new String[3][];
            JsonObject p = pattern.getAsJsonObject();
            JsonArray bottom = p.getAsJsonArray("bottom");
            JsonArray middle = p.getAsJsonArray("middle");
            JsonArray top = p.getAsJsonArray("top");

            String[] b = witchery_Patcher$readJsonArray(bottom);
            String[] m = witchery_Patcher$readJsonArray(middle);
            String[] t = witchery_Patcher$readJsonArray(top);

            // Middle is always present
            if (b.length > 0 && t.length > 0) {
                witchery_Patcher$currentPattern = new String[3][];
                witchery_Patcher$currentPattern[2] = b;
                witchery_Patcher$currentPattern[1] = m;
                witchery_Patcher$currentPattern[0] = t;
            } else if (b.length == 0 && t.length > 0) {
                witchery_Patcher$currentPattern = new String[2][];
                witchery_Patcher$currentPattern[1] = m;
                witchery_Patcher$currentPattern[0] = t;
            } else if (b.length > 0) {
                witchery_Patcher$currentPattern = new String[2][];
                witchery_Patcher$currentPattern[1] = b;
                witchery_Patcher$currentPattern[0] = m;
            } else {
                witchery_Patcher$currentPattern = new String[1][];
                witchery_Patcher$currentPattern[0] = m;
            }
        }

        // Return the original element
        return pattern;
    }

    @WrapOperation(method = "apply(Ljava/util/Map;Lnet/minecraft/resources/ResourceManager;)V", remap = false,
    at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonObject;getAsJsonObject(Ljava/lang/String;)Lcom/google/gson/JsonObject;", ordinal = 1))
    private JsonObject readCharmapFromJson(JsonObject instance, String memberName, Operation<JsonObject> original) {
        JsonObject result = original.call(instance, memberName);
        if (result == null) return result;

        // Clear hashmap
        HashMap<Character, IBlockState> charMap = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : result.entrySet()) {

            char chr = entry.getKey().charAt(0);
            JsonObject obj = entry.getValue().getAsJsonObject();

            // Proxy predicate: meaning that it is a block placeholder that can be
            // replaced by other blocks. We go in the pattern and cyclically replace this letter
            // with one of the letters in "requires"
            JsonArray reqs = obj.getAsJsonArray("requires");
            if (reqs != null) {
                String[] replacements = witchery_Patcher$readJsonArray(reqs);
                replacePatternLetter(chr, replacements);
                continue;
            }

            // Otherwise, proceed as normal
            IBlockState state = parseBlockState(entry.getValue().getAsJsonObject());
            charMap.put(chr, state);
        }

        // Now that we have final key, pattern and charMap, add to registry
        MutationRegistry.MutationInfo info = new MutationRegistry.MutationInfo(
                witchery_Patcher$currentPattern, charMap);
        MutationRegistry.mutations.put(witchery_Patcher$currentKey, info);

        return result;
    }

    @Unique
    private String[] witchery_Patcher$readJsonArray(JsonArray arr) {
        if (arr == null) {
            return new String[0];
        }
        int arrSize = arr.size();
        String[] result = new String[arrSize];
        for (int i = 0; i < arrSize; i++) {
            result[i] = arr.get(i).getAsString();
        }
        return result;
    }

    private IBlockState parseBlockState(JsonObject object) {
        // In the "block" section contains the block and its properties, defining a blockstate
        JsonElement blockObj = object.get("block");
        if (blockObj == null) return null;
        String blockStr = blockObj.getAsString();

        // Try to get the block. If null, the block is unknown
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockStr));
        if (block == null) return null;

        // Start from the default state
        IBlockState state = block.getDefaultState();

        JsonObject propertyJsonObj = object.getAsJsonObject("properties");
        if (propertyJsonObj == null) return state;

        // BlockStateContainer can get properties by name
        BlockStateContainer stateContainer = block.getBlockState();

        // Iterate over properties and set them to the state
        for (Map.Entry<String, JsonElement> property : propertyJsonObj.entrySet()) {
            String propertyKey = property.getKey();
            JsonElement propertyValue = property.getValue();
            IProperty<?> prop = stateContainer.getProperty(propertyKey);
            if (prop != null) {
                // If propertyValue is a Json compound object, then we don't know how the property is parsed.
                // since we have to return a single blockstate, we leave this property as default
                // For example, Witchery uses "min" and "max" for some integer properties (like crop age)
                if (propertyValue.isJsonPrimitive()) {
                    Optional<?> value = prop.parseValue(propertyValue.getAsString());
                    state = setValue(state, prop, value.toString());
                    if (state == null) return state;
                }
            }
        }

        // Return the final state
        return state;
    }

    private void replacePatternLetter(char target, String[] replacements) {
        if (this.witchery_Patcher$currentPattern == null || replacements.length == 0)
            return;

        int index = 0;
        StringBuilder buf;

        // Iterate over all current pattern strings and replace the char cyclically with the replacements
        for (int y = 0; y < this.witchery_Patcher$currentPattern.length; y++) {
            for (int z = 0; z < this.witchery_Patcher$currentPattern[y].length; z++) {

                buf = new StringBuilder(this.witchery_Patcher$currentPattern[y][z]);
                for (int j = 0; j < buf.length(); j++) {
                    if (buf.charAt(j) == target) {
                        buf.setCharAt(j, replacements[index].charAt(0));
                        index = (index + 1) % replacements.length;

                    }
                }
                this.witchery_Patcher$currentPattern[y][z] = buf.toString();

            }
        }
    }

    private static <T extends Comparable<T>> IBlockState setValue(IBlockState state, final IProperty<T> prop, String val) {
        Optional<T> value = prop.parseValue(val);
        if (value.isPresent()) {
            return state.withProperty(prop, value.get());
        }
        return state;
    }



}
