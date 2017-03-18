package org.learning

import sun.misc.Lock
import java.io.BufferedReader
import java.io.FileReader

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

fun test9() {
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

fun testJoinToString() {
  val letters = listOf("Alpha", "Beta")
  println(letters.joinToString())
  // above prints Alpha, Beta
  println(letters.joinToString { it.toLowerCase() })
  // above prints alpha, beta
  println(letters.joinToString(separator = "! ", postfix = "! ", transform = { it.toUpperCase() }))
  // above prints ALPHA! BETA!
}

//another way using nullability
fun <T> Collection<T>.joinTooString(
  separator: String = ", ",
  prefix: String = "",
  postfix: String = "",
  transform: ((T) -> String)? = null //nullable function type
): String {
  val result = StringBuilder(prefix)
  for ((index, element) in this.withIndex()) {
    if (index > 0) result.append(separator)
    //(if transform is null return null else transform.invoke()) is null return element.toString()
    val str = transform?.invoke(element) ?: element.toString()
    result.append(str)
  }
  result.append(postfix)
  return result.toString()
}

//this returns a function from a function
enum class Delivery { STANDARD, EXPEDITED }

class Order(val itemCount: Int)

fun getShippingCostCalculator(delivery: Delivery): (Order) -> Double {
  if (delivery == Delivery.EXPEDITED) {
    return { order -> 6 + 2.1 * order.itemCount }
  }
  return { order -> 1.2 * order.itemCount }
}

fun testDelivery() {
  val calculator = getShippingCostCalculator(Delivery.EXPEDITED)

  println("Shipping costs ${calculator(Order(3))}")
  //prints Shipping costs 12.3
}

//awesome use of functions, study this
fun contactList() {
  data class Person(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String?
  )

  class ContactListFilters {
    var prefix: String = ""
    var onlyWithPhoneNumber: Boolean = false

    fun getPredicate(): (Person) -> Boolean {
      val startsWithPrefix = { p: Person ->
        p.firstName.startsWith(prefix) || p.lastName.startsWith(prefix)
      }
      if (!onlyWithPhoneNumber) {
        return startsWithPrefix
      }
      return {
        startsWithPrefix(it)
          && it.phoneNumber != null
      }
    }
  }

  val contacts = listOf(Person("Dmitry", "Jemerov", "123-4567"), Person("Svetlana", "Isakova", null))
  val contactListFilters = ContactListFilters()

  with(contactListFilters) {
    //notice use of with, similar to apply but doesn't return the object
    prefix = "Dm"
    onlyWithPhoneNumber = true
  }

  println(contacts.filter(contactListFilters.getPredicate()))
  // prints [Person(firstName=Dmitry, lastName=Jemerov, phoneNumber=123-4567)]
}

enum class OS { WINDOWS, LINUX, MAC, IOS, ANDROID }

//using lambdas to remove duplication
fun removeCodeDups() {
  data class SiteVisit(
    val path: String,
    val duration: Double,
    val os: OS
  )

  val log = listOf(
    SiteVisit("/", 34.0, OS.WINDOWS),
    SiteVisit("/", 22.0, OS.MAC),
    SiteVisit("/login", 12.0, OS.WINDOWS),
    SiteVisit("/signup", 8.0, OS.IOS),
    SiteVisit("/", 16.3, OS.ANDROID)
  )

  val averageWindowsDuration = log.filter { it.os == OS.WINDOWS }
    .map(SiteVisit::duration).average()

  //if you need to calculate the same statistics for MAC users you can improve readability
  //and avoid duplication by extracting the os as a parameter in an extension function
  fun List<SiteVisit>.averageDurationForSingle(os: OS) =
    filter { it.os == os }.map(SiteVisit::duration).average()

  println(log.averageDurationForSingle(OS.WINDOWS))
  //prints 23.0
  println(log.averageDurationForSingle(OS.MAC))
  //prints 22.0

  //more complex because of set
  val averageMobileDuration = log.filter { it.os in setOf(OS.IOS, OS.ANDROID) }
    .map(SiteVisit::duration).average()

  //now a simple parameter representing the os doesn't do the job
  //so pass in a lambda instead
  fun List<SiteVisit>.averageDurationFor(predicate: (SiteVisit) -> Boolean) =
    filter(predicate).map(SiteVisit::duration).average()

  //since the lambda's parameter and return type are defined above those types can be inferred here
  println(log.averageDurationFor { it.os in setOf(OS.ANDROID, OS.IOS) })
  //prints 12.15
  println(log.averageDurationFor { it.os == OS.IOS && it.path == "/signup" })
  //prints 8.0
}


//by using the inline keyword the body of the function is "inlined"
//or substituted into the places where the function is called instead of
//being invoked through the normal process. This is much more efficient
inline fun <T> synchronized(lock: Lock, action: () -> T): T {
  lock.lock()
  try {
    return action()
  } finally {
    lock.unlock()
  }
}

//normal coding, before the inline process happens
fun foo(l: Lock) {
  println("Before sync")

  synchronized(l) {
    println("Action")
  }

  println("After sync")
}

//the generated code after the inline process happens
fun __foo__(l: Lock) {
  println("Before sync")

  //start - body of synchronized function
  l.lock()
  try {
    println("Action") //notice the lambda parameter is inlined here as well
  } finally {
    l.unlock()
  }
  //end - body of synchronized function

  println("After sync")
}

//here a variable of a function is being passed into the
//inlined function. In this case the lambda that the is stored in the
//variable is not inlined. Only when a lambda with its body is passed in
//directly does it get inlined.
//For the lambda to be inlined the lambda's code body must be available at the site
//where the inline function is called
class LockOwner(val lock: Lock) {
  //before inlining
  fun runUnderLock(body: () -> Unit) {
    synchronized(lock, body)
  }

  //after inlining
  fun __runUnderLock__(body: () -> Unit) {
    lock.lock()
    try {
      body()
    } finally {
      lock.unlock()
    }
  }
}

//pg. 213/240 Restrictions on Inline Functions
//Generally, the parameter can be inlined if it's called directly or
//passed as an argument to another inline function


//use the noinline modifier when you do not what to inline one the lambdas
inline fun foo(inlined: () -> Unit, noinline notInlined: () -> Unit) {
// ...
}

//pg.215/242 using asSequence when chaining filter, map, etc. should only be used
//for large collections because it does not allow inlining.

//Do not use inlining everywhere, it is only beneficial to performance with functions
//that take lambdas as arguments
//pg. 216/243 You should still pay attention to the code size when deciding whether to use
//the inline modifier. If the function you want to inline is large, copying its bytecode
//into every call site could be expensive in terms of bytecode size.


//the use function is an extension function called on a closable resource
//it receives a lambda as an argument and ensures that the resource is closed
//regardless of whether the lambda completes romally or throws an exception
fun readFirstLineFromFile(path: String): String {
  BufferedReader(FileReader(path)).use { br ->
    return br.readLine() //this is a non-local return that returns a value from the readFirstLineFromFile function
  }
}

/////Returning in lambdas

fun lookForAliceLoop(people: List<Person>) {
  for (person in people) {
    if (person.name == "Alice") {
      println("Found!")
      return //in a for loop, returns normally
    }
  }
  println("Alice is not found")
}

//If you use the return keyword in a lambda, it returns from the function in which you called
//the lambda, not just from the lambda itself.
//Note that the return from the outer function is possible only if the function that takes
//the lambda as an argument is inlined.
fun lookForAlice(people: List<Person>) {
  people.forEach {
    //since forEach is inlined, the return keyword works and returns from lookForAlice
    if (it.name == "Alice") {
      println("Found!")
      return //"non-local return" - even though its in a lambda, the return statement still returns from lookForAlice
    }
  }
  println("Alice is not found")
}

//Using the return expression in lambdas passed to non-inline functions isn’t allowed.

//To return from lambdas use labels. You can label a lambda expression from which you
//want to return, and then refer to this label after the return keyword
fun lookForAliceLabel(people: List<Person>) {
  people.forEach label@ {
    if (it.name == "Alice") return@label
  }
  println("Alice might be somewhere")
}

//Alternatively, the name of the function that takes this lambda as an argument
//can be use as a label.
fun lookForAliceFunctionLabel(people: List<Person>) {
  people.forEach {
    if (it.name == "Alice") return@forEach //refers to the forEach function this lambda is passed to
  }
  println("Alice might be somewhere")
}

//Note that if you specify the label of the lambda expression explicitly, labeling using
//the function name doesn’t work. A lambda expression can’t have more than one label.

//pg. 220/247 Using labels with the 'this' keyword
fun labelsWithThis() {
  println(StringBuilder().apply sb@ {
    listOf(1, 2, 3).apply {
      this@sb.append(this.toString()) //'this' refers to the list and 'this@sb' refers to the StringBuilder instance
    }
  })
}

//Using the non-local return syntax can get verbose. The solution is an alternative syntax called anonymous functions

//Anonymous functions: local returns by default

fun lookForAliceAnonFunction(people: List<Person>) {
  people.forEach(fun(person) { //uses the fun keyword to define an anonymous function
    if (person.name == "Alice") return
    println("${person.name} is not Alice")
  })

  people.filter(fun(person): Boolean { //return type must be explicitely defined
    return person.name.length < 30 //returns from this function instead of having to use a label
  })
}

//pg. 221/248
//The rule is simple, 'return' returns from the closest function declared using the 'fun' keyword

//Despite that an anonymous function looks like a regular function, it is actually a lambda.
//In other words, it is another syntactic form of a lambda expression.
//Therefor the inlining rules of lambdas also applies to anonymous functions.