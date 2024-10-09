package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.BaseProcessor;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.IVariableProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CirclesProcessor extends BaseProcessor {

    protected String title;
    protected String description;
    protected static final Map<ECircleType, ResourceLocation> circleMap = new HashMap<>();
    protected static final String CIRCLES_PATH = "witcherycompanion:textures/patchouli/circles/";
    protected static final String EMPTY_CIRCLES_PATH = "witcherycompanion:textures/patchouli/circles/empty";

    protected transient List<String> images;

    static {
        circleMap.put(ECircleType.TINY_RITUAL, new ResourceLocation(CIRCLES_PATH + "tiny_ritual.png"));
        circleMap.put(ECircleType.MINI_RITUAL, new ResourceLocation(CIRCLES_PATH + "mini_ritual.png"));
        circleMap.put(ECircleType.SMALL_RITUAL, new ResourceLocation(CIRCLES_PATH + "small_ritual.png"));
        circleMap.put(ECircleType.MEDIUM_RITUAL, new ResourceLocation(CIRCLES_PATH + "medium_ritual.png"));
        circleMap.put(ECircleType.LARGE_RITUAL, new ResourceLocation(CIRCLES_PATH + "large_ritual.png"));

        circleMap.put(ECircleType.TINY_INFERNAL, new ResourceLocation(CIRCLES_PATH + "tiny_infernal.png"));
        circleMap.put(ECircleType.MINI_INFERNAL, new ResourceLocation(CIRCLES_PATH + "mini_infernal.png"));
        circleMap.put(ECircleType.SMALL_INFERNAL, new ResourceLocation(CIRCLES_PATH + "small_infernal.png"));
        circleMap.put(ECircleType.MEDIUM_INFERNAL, new ResourceLocation(CIRCLES_PATH + "medium_infernal.png"));
        circleMap.put(ECircleType.LARGE_INFERNAL, new ResourceLocation(CIRCLES_PATH + "large_infernal.png"));

        circleMap.put(ECircleType.TINY_OTHERWHERE, new ResourceLocation(CIRCLES_PATH + "tiny_otherwhere.png"));
        circleMap.put(ECircleType.MINI_OTHERWHERE, new ResourceLocation(CIRCLES_PATH + "mini_otherwhere.png"));
        circleMap.put(ECircleType.SMALL_OTHERWHERE, new ResourceLocation(CIRCLES_PATH + "small_otherwhere.png"));
        circleMap.put(ECircleType.MEDIUM_OTHERWHERE, new ResourceLocation(CIRCLES_PATH + "medium_otherwhere.png"));
        circleMap.put(ECircleType.LARGE_OTHERWHERE, new ResourceLocation(CIRCLES_PATH + "large_otherwhere.png"));
    }

    @Override
    public void setup(IVariableProvider<String> provider) {
        images = new ArrayList<>();
        final String circles = readVariable(provider, "circles");
        for (String circle : circles.split(",")) {
            ECircleType info = ECircleType.fromString(circle);
            if (info != null) {
                images.add(circleMap.get(info).toString());
            }
        }

        this.title = readVariable(provider, "title");
        this.description = readVariable(provider, "description");
    }

    @Override
    public String process(String key) {
        int index = ProcessorUtils.splitKeyIndex(key);

        if (key.startsWith("image")) {
            if (index < 0 || index >= images.size()) return EMPTY_CIRCLES_PATH;
            return images.get(index);
        } else if (key.startsWith("guard")) {
            return String.valueOf(index >= 0 && index < images.size());
        } else if (key.equals("title")) {
            return this.title;
        } else if (key.equals("description")) {
            return this.description;
        }

        return null;
    }

    @Override
    protected String getSecretKey() {
        return null;
    }

    @Override
    protected void obfuscateFields() { }

    @Override
    protected void hideFields() { }


    public enum ECircleType {

        TINY_RITUAL,
        MINI_RITUAL,
        SMALL_RITUAL,
        MEDIUM_RITUAL,
        LARGE_RITUAL,
        TINY_INFERNAL,
        MINI_INFERNAL,
        SMALL_INFERNAL,
        MEDIUM_INFERNAL,
        LARGE_INFERNAL,
        TINY_OTHERWHERE,
        MINI_OTHERWHERE,
        SMALL_OTHERWHERE,
        MEDIUM_OTHERWHERE,
        LARGE_OTHERWHERE,
        ;

        public static ECircleType fromString(String str) {
            String lowerCaseName = str.toLowerCase();
            for (ECircleType value : values()) {
                if (value.name().toLowerCase().equals(lowerCaseName)) {
                    return value;
                }
            }
            return null;
        }
    }

}
