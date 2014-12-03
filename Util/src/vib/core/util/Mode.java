/*
 * This file is part of VIB (Virtual Interactive Behaviour).
 */
package vib.core.util;

/**
 * This is an enumeration of animations mixing modes.
 *
 * @author Andre-Marie Pez
 */
public enum Mode {

    /**
     * The associated animation must replace the current animation
     */
    replace,
    /**
     * The associated animation must be blened with the current animation
     */
    blend,
    /**
     * The associated animation must be added after the current animation
     */
    append;

    public static Mode interpret(String name, Mode defaultMode) {
        try {
            return valueOf(name);
        } catch (Throwable t) {
            return defaultMode;
        }
    }

    public static Mode interpret(String name) {
        return interpret(name, blend);
    }
}
