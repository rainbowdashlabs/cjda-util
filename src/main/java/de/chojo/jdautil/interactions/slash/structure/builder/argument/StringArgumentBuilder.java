package de.chojo.jdautil.interactions.slash.structure.builder.argument;

public interface StringArgumentBuilder extends CompletableArgumentBuilder {
    @Override
    StringArgumentBuilder asRequired();

    @Override
    StringArgumentBuilder withAutoComplete();

    StringArgumentBuilder minLength(int min);

    StringArgumentBuilder maxLength(int max);

}
