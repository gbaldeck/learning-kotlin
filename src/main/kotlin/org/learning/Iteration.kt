package org.learning

import java.util.*

/**
 * Created by gbaldeck on 2/15/2017.
 */
fun fizzBuzz(i: Int) =
  when {
    i % 15 == 0 -> "FizzBuzz "
    i % 3 == 0 -> "Fizz "
    i % 5 == 0 -> "Buzz "
    else -> "$i "
  }

fun binaryReps() {
  val binreps = TreeMap<Char, String>()

  for (c in 'A'..'F') {
    val binary = Integer.toBinaryString(c.toInt())
    binreps[c] = binary;
  }

  for ((letter, binary) in binreps) {
    println("$letter = $binary")
  }
}

fun isLetter(c: Char) = c in 'a'..'z' || c in 'A'..'Z'
fun isNotDigit(c: Char) = c !in '0'..'9'