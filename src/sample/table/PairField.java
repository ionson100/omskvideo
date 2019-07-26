package sample.table;

import java.lang.reflect.Field;

public class PairField {

    public PairField(String name, Field value) {
        this.name = name;
        this.value = value;
    }

    public final String name;

    public final Field value;

    @Override
    public String toString() {
        return this.name;
    }
}
