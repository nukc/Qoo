package com.github.nukc.scanner.matcher

/**
 * base matcher
 * @author Nukc.
 */
interface Matcher<T> {

    /**
     * Filters item emitted by a Publisher by only emitting those that satisfy a specified predicate.
     */
    fun filter(item: T): Boolean = true
}