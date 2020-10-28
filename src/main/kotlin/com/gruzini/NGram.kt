package com.gruzini

import java.util.*

data class NGram(val words: String, val freq: Int = -1) : Comparable<NGram> {
    override fun compareTo(other: NGram): Int {
        val x = words.compareTo(other.words)
        return if (x != 0 || freq == -1) {
            x
        } else {
            freq.compareTo(other.freq)
        }
    }
}
//go through a string, if not last char take that char and next one, if second to last, do nothing -> function separates string into pairs "bartek" -> "ba ar rt te ek"
fun String.pairwise() = mapIndexed { index, char -> if (index + 1 <= lastIndex) "$char${this[index + 1]}" else "" }.dropLast(1)

fun NavigableSet<NGram>.complete(input: String): List<String> {

    val canonicalInput = input.toLowerCase()
    val sequence = (canonicalInput.lastIndex + 1 downTo 0).asSequence().map {
        canonicalInput.substring(0, it) to canonicalInput.substring(it)
    }
    fun NavigableSet<NGram>.query(s: String) = tailSet(NGram(s)).headSet(NGram(s + Char.MAX_SURROGATE))
    val queried = sequence.map { query(it.first) to it.second }

    val matches = queried.dropWhile { it.first.isEmpty() }
    val (candidates, remaining) = matches.first()

    val charPairs = if (remaining.length == 1) listOf(remaining) else remaining.pairwise()
    val prefix = input.substring(0, input.length - remaining.length)

    return candidates.filter { ngram ->
        val postfix = ngram.words.substring(prefix.length)
        charPairs.map { pair ->
            when {
                pair.length == 1 -> postfix.contains(pair)
                else -> postfix.indexOf(pair[0]).let { it > 0 && it < postfix.lastIndexOf(pair[1]) }
            }
        }.all { it }
    }.sortedByDescending { it.freq }.map { it.words }
}