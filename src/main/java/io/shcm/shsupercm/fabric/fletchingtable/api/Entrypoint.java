package io.shcm.shsupercm.fabric.fletchingtable.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Repeatable(Entrypoint.List.class)
public @interface Entrypoint {
    String CLIENT = "client";

    String value();

    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
    @interface List {
        Entrypoint[] value();
    }
}
