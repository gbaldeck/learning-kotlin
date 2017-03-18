package org.learning

/**
 * Created by gbaldeck on 3/18/2017.
 */

//Types arguments are the letters in the angle-brackets
//<T> for a List<T> or <T, K> for Map<T, K>
//pg. 224/251 In Kotlin type arguments must always be inferred or defined

//Two ways to explicitely define it for an empty list
fun taTest1() {
  val readersA: MutableList<String> = mutableListOf() //equivalent to below
  val readersB = mutableListOf<String>() //equivalent to above

  val authors = listOf("Dmitry", "Svetlana")
  val readers = mutableListOf<String>(/* ... */)

  //Declaration of filter extension function
  //fun <T> List<T>.filter(predicate: (T) -> Boolean): List<T>

  //Using the type argument and parameter T the type from the list is inferred as a String
  readers.filter { it !in authors }
}