package de.chojo.jdautil.interactions.slash.structure.builder.argument;

public interface CompletableArgumentBuilder extends ArgumentBuilder {
    @Override
    CompletableArgumentBuilder asRequired();

    CompletableArgumentBuilder withAutoComplete();
}
