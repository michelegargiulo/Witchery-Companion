package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.components.base;

import com.google.gson.annotations.SerializedName;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.beans.base.AbstractDTO;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.VariableHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Function;


public abstract class  BaseComponent<D extends AbstractDTO> implements ICustomComponent {

    @SerializedName("serialized")
    @VariableHolder
    public String serializedDto;

    @SerializedName("is_secret")
    @VariableHolder
    public String _isSecret;

    @SerializedName("secret_text")
    @VariableHolder
    public String _secretText;

    @SerializedName("secret_tooltip")
    @VariableHolder
    public String _secretTooltip;


    protected transient int x = 0;
    protected transient int y = 0;
    protected transient int pageNum = 0;
    protected transient D dto = null;

    @Override
    public final void build(int componentX, int componentY, int pageNum) {
        this.x = componentX;
        this.y = componentY;
        this.pageNum = pageNum;

        if (this.serializedDto != null) {
            // Hacky way to avoid using Field reflection or even hackier ways to get the DTO type for deserialization
            Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            this.dto = ProcessorUtils.deserializeDto(serializedDto, type);
        }

        this.onBuild();
    }

    protected abstract void onBuild();

    protected final <T> T getTransform(
            @Nullable String override,
            @Nonnull Function<String, T> overrideTransform,
            @Nullable D dto,
            @Nonnull Function<D, T> dtoField,
            @Nonnull T defaultValue) {

        return Optional.ofNullable(override)
                .map(overrideTransform)
                .orElseGet(() -> Optional.ofNullable(dto)
                        .map(dtoField) // It is like dto -> dtoField.apply(dto). Should return the correct DTO field
                        .orElse(defaultValue)
                );
    }

    protected final String getTransform(
            @Nullable String override,
            @Nullable D dto,
            @Nonnull Function<D, String> dtoField,
            @Nonnull String defaultValue) {

        return Optional.ofNullable(override)
                .orElseGet(() -> Optional.ofNullable(dto)
                        .map(dtoField) // It is like dto -> dtoField.apply(dto). Should return the correct DTO field
                        .orElse(defaultValue)
                );
    }
}
