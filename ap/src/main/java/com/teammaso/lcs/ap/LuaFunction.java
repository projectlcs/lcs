package com.teammaso.lcs.ap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface LuaFunction {
    String name() default "!";
}
