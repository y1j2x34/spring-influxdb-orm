package com.vgerbot.orm.influxdb.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.METHOD,
        ElementType.TYPE,
        ElementType.PACKAGE
})
public @interface CoberturaIgnore {
}
