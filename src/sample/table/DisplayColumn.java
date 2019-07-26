package sample.table;


import sample.table.celldescriptions.ITableColumnCell;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface DisplayColumn {
    String name_column();

    int index() default 0;

    String date_format() default "dd.MM.yyyy HH:mm:ss";

    int width() default 200;

    Class<? extends ITableColumnCell> ClassTableCell() default ITableColumnCell.class;
}

