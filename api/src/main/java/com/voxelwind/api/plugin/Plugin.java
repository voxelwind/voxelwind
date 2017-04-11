package com.voxelwind.api.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to find plugins when the plugin is initialized.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Plugin {
    /**
     * The ID for this plugin. This should be an alphanumeric name. Slashes are also allowed.
     * @return the ID for this plugin
     */
    String id();

    /**
     * The author of this plugin.
     * @return the plugin's author
     */
    String author();

    /**
     * The version of this plugin.
     * @return the version of this plugin
     */
    String version();

    /**
     * The website of this plugin.
     * @return the websute of this plugin
     */
    String website() default "";

    /**
     * The array of plugin IDs that this plugin requires in order to load.
     * @return the dependencies
     */
    String[] dependencies() default {};

    /**
     * The array of plugin IDs that this plugin optionally depends on.
     * @return the soft dependencies
     */
    String[] softDependencies() default {};
}
