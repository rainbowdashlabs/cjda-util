package de.chojo.jdautil.interactions.slash.structure.builder.argument;

import de.chojo.jdautil.interactions.slash.Argument;

public interface ArgumentBuilder {
    ArgumentBuilder asRequired();

    public Argument build();
}
