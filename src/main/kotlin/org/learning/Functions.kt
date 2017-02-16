package org.learning

/**
 * Created by gbaldeck on 2/16/2017.
 */

//If a member function and an extension function have the same name the
//member function always takes precedence over the extension function

//extension function with default parameters
fun <T> Collection<T>.joinToString(
    seperator: String = ", ",
    prefix: String = "",
    postfix: String = ""
): String {

  val result = StringBuilder(prefix)

  for ((index, element) in this.withIndex()) {
    if (index > 0) result.append(seperator)
    result.append(element)
  }

  result.append(postfix)
  return result.toString()
}

//extension function using another extension function
fun Collection<String>.join(
    seperator: String = ", ",
    prefix: String = "",
    postfix: String = ""
) = joinToString(seperator, prefix, postfix) //the extended function can be use in other extended functions

fun String.lastChar(): Char = this.get(this.length - 1);

//extension property does not have any state or backing field, therefore a getter must
//always be explicitly defined
//Since String is immutable we cant use a var with a setter
val String.firstChar: Char
  get() = get(0)

//StringBuilder is not immutable so we can use a setter
var StringBuilder.firstChar: Char
  get() = get(0)
  set(value: Char) = this.setCharAt(0, value)

//vararg means a variable number of arguments are aloud
fun <T> printStuff(vararg values: T) {
  //* is the spread operator, it unpacks all the values from the array. this is required
  val list = listOf("one final value", *values)
  list.forEach { value -> println(value) }
}