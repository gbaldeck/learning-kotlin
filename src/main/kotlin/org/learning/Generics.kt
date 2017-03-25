package org.learning

import java.util.*
import javax.xml.ws.Service

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

//Note that the Kotlin compiler is smart enough to allow 'is' checks when the corresponding
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

//How to represent a Java class
val serviceImpl = ServiceLoader.load(Service::class.java)

//Now with reified types
inline fun <reified T> loadService(): ServiceLoader<T> {
  return ServiceLoader.load(T::class.java)
}

//Now we can do the same thing like this, looks much nicer
val reifiedServiceImpl = loadService<Service>()

//How you CAN use reified type parameters:
// In type checks and casts (is, !is, as, as?)
// To use the Kotlin reflection APIs, as we’ll discuss in chapter 10 (::class)
// To get the corresponding java.lang.Class (::class.java)
// As a type argument to call other functions

//How you CAN'T use reified type parameters:
// Create new instances of the class specified as a type parameter
// Call methods on the companion object of the type parameter class
// Use a non-reified type parameter as a type argument when calling a function
//    with a reified type parameter
// Mark type parameters of classes, properties, or non-inline functions as reified

//Remember if you need to un-inline lambdas in an inline function you can use the
//'noninline' modifier

//Is it safe to pass a list of strings to a function that expects a list of Any objects?
//It’s not safe if the function adds or replaces elements in the list, because this
//creates the possibility of type inconsistencies. It’s safe otherwise.

//pg. 238/265 Subtypes and SuperTypes
//A non-null type is a subtype of its nullable version
//For example String is a subtype of String?

//pg 240/267 Definition of invariant and covariant

//A covariant class is a generic class (we’ll use Producer<T> as an example) for which
//the following holds: Producer<A> is a subtype of Producer<B> if A is a subtype of B.
//We say that the subtyping is preserved.

//Use the 'out' keyword before the name of the type parameter to declare the class
//as covariant
interface CovariantProducer<out T> {
  fun produce(): T
}

//Example of Covariance
open class Animal {
  fun feed() {  }
}

//Constructor parameters are in neither the in nor the out position. Even if a type
//parameter is declared as out, you can still use it in a constructor parameter
//declaration
class Herd<T : Animal> (vararg animals: T) {
  val size: Int
    get() = herd.size

  private val herd = mutableListOf(*animals) //spread operator

  operator fun get(i: Int): T = herd[i]
}

fun feedAll(animals: Herd<Animal>) {
  for (i in 0 until animals.size) {
    animals[i].feed()
  }
}

class Cat : Animal() {
  fun cleanLitter() {  }
}
fun takeCareOfCats(cats: Herd<Cat>) {
  for (i in 0 until cats.size) {
    cats[i].cleanLitter()
    feedAll(cats) //inferred type is Herd<Cat> but Herd<Animal> is expected, so we use 'out' on 'T' in 'Herd' to fix that
  }
}

//Because no variance modifier like 'out' was used on the type 'T' parameter
//on the 'Herd' class, the herd of cats isn't a subclass of the herd of animals.
//Using an explicit cast would fix it, but its not ideal. Its error prone and verbose.

//pg. 241/268 for explanation of 'in' and 'out' variance
//Given a class that declared type parameter T and has a function that uses T
//If T is used as a return type of a function the it's in the 'out' position
//If T is used as the type of a function parameter, then it's in the 'in' position


//The concept of contravariance can be thought of as a mirror to covariance: for a contravariant
//class, the subtyping relation is the opposite of the subtyping relations of
//classes used as its type arguments.

interface ContraComparator<in T> {
  fun compare(e1: T, e2: T): Int { return 1 } //'T' is used only in the 'in' positions
}

//Here both Contra and Co Variant are used
interface Function1<in P, out R> {
  operator fun invoke(p: P): R
}

fun enumerateCats(f: (Cat) -> Number) {  }
fun Animal.getIndex(): Int = 1

fun animTest() {
//  This code is legal in Kotlin. Animal is a supertype of Cat, and Int is a subtype of Number.
  enumerateCats(Animal::getIndex)
}

