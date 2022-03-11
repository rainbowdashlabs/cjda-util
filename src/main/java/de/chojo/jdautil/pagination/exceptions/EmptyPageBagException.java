package de.chojo.jdautil.pagination.exceptions;

public class EmptyPageBagException extends RuntimeException {
    public EmptyPageBagException() {
        super("The page bag is empty");
    }
}
