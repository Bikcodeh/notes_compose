package com.bikcodeh.notes_compose.domain.commons

/**
 * Interface to map some class from one type to another type
 * Where I means INPUT, the class base
 * Where O means OUTPUT, the class to be mapped
 */
interface Mapper<I, O> {
    fun map(input: I): O
    fun mapInverse(input: O): I
}

// Non-nullable to Non-nullable
interface ListMapper<I, O>: Mapper<List<I>, List<O>>