package org.learning

import sun.misc.Lock

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
    println("Action") //the action parameter is inlined here
  } finally {
    l.unlock()
  }
  //end - body of synchronized function

  println("After sync")
}