package org.learning

/**
 * Created by gbaldeck on 3/13/2017.
 */

//In this case, the compiler infers that both the sum and action variables have function types
val sum = { x: Int, y: Int -> x + y }
val action = { println(42) }

//Explicit type declaration for the function types
val sumExplicit: (Int, Int) -> Int = { x, y -> x + y } //takes two Ints and returns an Int
val actionExplicit: () -> Unit = { println(42) } //returns Unit which is equivalent to void

//Just like with any other function the return type can be nullable
var canReturnNull: (Int, Int) -> Int? = { x, y -> null }

//The function type itself can be nullable as well with the use of parenthesis
var funOrNull: ((Int, Int) -> Int)? = null


//Example of implementing filter on a String to include only the specified character in the String
fun String.filter(predicate: (Char) -> Boolean): String {
  val sb = StringBuilder()
  for (index in 0 until length) {
    val element = get(index)
    if (predicate(element)) sb.append(element)
  }
  return sb.toString()
}