package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@IsTag( type = "Feature", value = "TestNG",
    description = "tests can be be executed with TestNG" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureTestNg {

}
