package ru.xsrv.strings.model;

/**
 *
 * Created by calc on 10.01.15.
 */
public class Lang {
    private String name;

    public Lang(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
