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

class StringList : ExampleList<String> {
  override fun get(index: Int): String = "Hola"
}

//Now the generic type parameter T of ArrayList is a type argument for List
class ExampleArrayList<T> : ExampleList<T> {
  override fun get(index: Int): T = index as T
}

//A class can refer to itself as an argument as well
interface ExampleComparable<T> {
  fun compareTo(other: T): Int
}

class ExampleString : Comparable<ExampleString> {
  //can do the greater than check below because both objects implement comparable
  override fun compareTo(other: ExampleString): Int = if (this > other) 1 else 2
}

//Type parameter upper bounds <T : Number>
//Same as <T extends Number> in Java
fun <T : Number> oneHalf(value: T): Double {
  return value.toDouble() / 2.0
}

fun <T : Comparable<T>> max(first: T, second: T): T {
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

fun testProcOne() {
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
//(meaning you have to specify the <TYPE> for a class or interface that is using generic type arguments)
//So use the star projection * syntax
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

//Kotlin generics are erased at runtime, for both functions, classes, and interfaces
//So here, since at runtime we don't know what T is since it's erased,
//we cannot check for the type that it is
fun <T> isA(value: Any) = value is T

//This problem can be avoided by using inline functions.
//By making a type parameter of an inline function 'reified', you will be able to refer
//to the type arguments at runtime

//Remember that making the function inline may improve performance if this function
//uses lambdas as arguments: the lambda code can be inlined as well, so no anonymous
//class will be created.

//By declaring the isA function as inline and marking the type parameter as 'reified',
//you can check 'value' to see if it's an instance of 'T' at runtime
inline fun <reified T> isAReified(value: Any) = value is T

fun testReified1() {
  println(isAReified<String>("abc"))
  //prints true

  println(isAReified<String>(123))
  //prints false
}

//simplified version of filterIsInstance stdlib function that returns only items
//in a list of the specified type
//how this works on pg.234/261
inline fun <reified T> filterIsInstance(list: List<*>): List<T> {
  val destination = mutableListOf<T>()
  for (element in list) {
    if (element is T) { //Here it checks to see if the element is a certain type, this would not be possible without inline and reified
      destination.add(element)
    }
  }
  return destination
}

//see performance related talk on pg. 235/262
//Even though performance is only enhanced with inlined function when using lambdas
//as parameters. You don't always have to inline for performance, you can also inline
//in order to use reified types.
//If your inlined function starts getting too large though it's best to extract the portion
//that needs inlining and make it into its own inline function
fun testReified2(){
  val myList = mutableListOf("Hello", 1, 5, "ten");

  //returns a list containing 1 and 5
  val listA = filterIsInstance<Number>(myList);

  //returns a list containing "Hello" and "ten"
  val listB = filterIsInstance<String>(myList);
}