package mctmods.resourcedatapackloader.content.types;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.properties.IProperty;
import java.util.Collection;
import javax.annotation.Nonnull;

public final class PropertyVariant implements IProperty<String> {
    public static final String NAME = "blocks";
    private final ImmutableSet<String> values;

    public PropertyVariant(Collection<String> values) { this.values = ImmutableSet.copyOf(values); }

    @Override @Nonnull public String getName() { return NAME; }

    @Override @Nonnull public Collection<String> getAllowedValues() { return values; }

    @Override @Nonnull public Class<String> getValueClass() { return String.class; }

    @Override @Nonnull public Optional<String> parseValue(@Nonnull String value) { return values.contains(value) ? Optional.of(value) : Optional.absent(); }

    @Override @Nonnull public String getName(@Nonnull String value) { return value; }
}
