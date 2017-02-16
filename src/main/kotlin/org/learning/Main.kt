package org.learning

import org.learning.lastChar as last //giving the function a different name in this file

/**
 * Created by gbaldeck on 2/7/2017.
 */

fun main(args: Array<String>) {
  println(Color.BLUE.rgb());
  println(getMnemonic(Color.BLUE))
  println(getWarmth(Color.ORANGE))

  println(eval(Sum(Num(2), Sum(Num(5), Num(7)))))

  for (i in 1..100) {
    print(fizzBuzz(i))
  }
  println("");
  for (i in 100 downTo 1 step 2) {
    print(fizzBuzz(i))
  }
  println("");
  binaryReps();

  val listOne = listOf(1, 2, 3);
  //Join to string is an extension function on the Collections class
  //All remaining parameters after the first named parameter must be named also
  println(listOne.joinToString(seperator = "; ", prefix = "(", postfix = ")"))

  //set default parameter values in the function so I can omit some parameters and not have to
  //do function overloading
  println(listOne.joinToString())
  println(listOne.joinToString("; "))

  //using default constructor values and named parameters I can omit some of the parameters and
  //put the named parameters in any order I want
  println(listOne.joinToString(postfix = ";", prefix = "# "))

  println("Kotlin".last())
  println(listOf("one", "two", "eight").join(" ")) // the join extension function requires strings

  //using extension properties
  val sb = StringBuilder("Kotlin");
  println(sb.firstChar)
  sb.firstChar = 'C'
  println(sb)

  printStuff("value one", "two", "seven", "twenty four")
}