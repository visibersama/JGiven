package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@IsTag( type = "Feature", value = "Step Parameters", description = "Steps can have parameters" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureStepParameters {

}
