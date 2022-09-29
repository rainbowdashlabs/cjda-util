package de.chojo.jdautil.interactions.slash.structure.builder.argument;

public interface IntegerArgumentBuilder extends CompletableArgumentBuilder {

    @Override
    IntegerArgumentBuilder asRequired();

    @Override
    IntegerArgumentBuilder withAutoComplete();

    IntegerArgumentBuilder min(long min);

    IntegerArgumentBuilder max(long max);
}
