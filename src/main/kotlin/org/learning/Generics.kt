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
  fun <T> List<T>.filter(predicate: (T) -> Boolean): List<T>? = null

  //Using the type argument and parameter T the type from the list is inferred as a String
  readers.filter { it !in authors }
}

//Generic extension property
//pg. 226/253 note that Generic non-extension properties (just a normal property) are not possible
val <T> List<T>.penultimate: T
  get() = this[size - 2]

fun genericExtensionProperty() {
  println(listOf(1, 2, 3, 4).penultimate)
  //prints 3
}

//Generic class and interface
interface ExampleList<T> {
  operator fun get(index: Int): T
// ...
}

class StringList: ExampleList<String> {
  override fun get(index: Int): String = "Hola"
}

//Now the generic type marameter T of ArrayList is a type argument for List
class ExampleArrayList<T> : ExampleList<T> {
  override fun get(index: Int): T = index as T
}

//A class can refer to itself as an argument as well
interface ExampleComparable<T> {
  fun compareTo(other: T): Int
}
class ExampleString : Comparable<ExampleString> {
  override fun compareTo(other: ExampleString): Int = other as Int
}

//Type parameter upper bounds <T : Number>
//Same as <T extends Number> in Java
fun <T : Number> oneHalf(value: T): Double {
  return value.toDouble() / 2.0
}

fun <T: Comparable<T>> max(first: T, second: T): T {
  //We can do the below check because first and second's upperbound is Comparable
  return if (first > second) first else second
}

//Multiple type parameter constraints with the 'where' keyword
fun <T> ensureTrailingPeriod(seq: T) where T : CharSequence, T : Appendable {
  if (!seq.endsWith('.')) { //endsWith is an extension function on the CharSequence interface
    seq.append('.') //Calls the method from the Appendable interface
  }
}

//a type parameter with no upper bound specified will have the upper bound of Any?
class ProcessorAnyorNull<T> {
  fun process(value: T) {
    value?.hashCode() //so a safe call must be used because T could be null
  }
}

fun testProcOne(){
  //As you can we use String? which is the nullable version of String
  val nullableStringProcessor = ProcessorAnyorNull<String?>()
  nullableStringProcessor.process(null)
}

//Now it can't be null because Any is the upperbound of T
class ProcessorAny<T : Any> {
  fun process(value: T) {
    value.hashCode()
  }
}

//pg. 231/258
//It is not possible to use types with type arguments in 'is' checks
fun typeIsCheck(value: Any) {
  if (value is List<String>) {
    return
  }
}

//Since Kotlin doesn't let you use a generic type without specifying type arguments
//use the star projection * syntax
fun typeIsStar(value: Any) {
  if (value is List<*>) { //Here the the generic type is replaced by the start projection syntax
    return
  }
}

//Note that the Kotlin compiler is smart enough to allow is checks when the corresponding
//type information is already known at compile time
fun printSum(c: Collection<Int>) {
//  the check whether c has type List<Int> is possible because you know
//  at compile time that this collection (no matter whether it’s a list or another kind of
//  collection) contains integer numbers
  if (c is List<Int>) {
    println(c.sum())
  }
}