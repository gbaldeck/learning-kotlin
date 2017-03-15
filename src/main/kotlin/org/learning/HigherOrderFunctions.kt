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

fun test9(){
  println("ab1c".filter { it in 'a'..'z' }) //prints "abc"
}

//Default parameter value is a lambda
fun <T> Collection<T>.joinToString(
  separator: String = ", ",
  prefix: String = "",
  postfix: String = "",
  transform: (T) -> String = { it.toString() } //this default parameter is a lambda
): String {
  val result = StringBuilder(prefix)
  for ((index, element) in this.withIndex()) { //notice the withIndex() function
    if (index > 0) result.append(separator)
    result.append(transform(element)) //calls the transform lambda
  }
  result.append(postfix)
  return result.toString()
}

fun testJoinToString(){
  val letters = listOf("Alpha", "Beta")
  println(letters.joinToString())
  // above prints Alpha, Beta
  println(letters.joinToString { it.toLowerCase() })
  // above prints alpha, beta
  println(letters.joinToString(separator = "! ", postfix = "! ", transform = { it.toUpperCase() }))
  // above prints ALPHA! BETA!
}