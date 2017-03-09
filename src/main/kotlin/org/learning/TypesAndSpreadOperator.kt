package org.learning

import java.io.BufferedReader

/**
 * Created by gramb on 3/6/2017.
 */

fun types() {
  data class Person(val name: String, val age: Int? = null) {

    // non-null Int can be made into a primitive type
    // null Int? cannot and is made into the wrapper type Integer
    fun isOlderThan(other: Person): Boolean? {
      if (age == null || other.age == null)
        return null
      return age > other.age
    }

  }

  //in kotlin int and Integer translate to Int and is handled under the covers for you
  //Int can have functions run on it like below, and you don't need to think about whether
  //it is an int or an Integer in java
  val i = 1
  //the below won't work because kotlin does not do implicit type conversion
  val l: Long = i

  //instead explicit type conversion must be done
  val lo: Long = i.toLong()

  val b: Byte = 1
  //arithmetic operators are overloaded to accept all appropriate numeric types
  val lon = b + 1L //see pg.156/183 for all primitive type literals like L for Long

  //The Kotlin standard library provides extension functions to convert Strings into primitive types
  "42".toInt()

  // 'Any' is the supertype of all non-nullable types in Kotlin (including primitive hybrids like Int)
  // 'Any?' is the same as 'Any' but it also allows 'null'
  val anyNotNull: Any = 42
  val anyNull: Any? = null

}

// The 'Unit' type is the same as the void type.
// It can be used as the return type of a function that has nothing to return
fun f(): Unit = println("Not doing much in this function...")

// here we can use Unit to show that the function does not require a return type
interface Processor<T> {
  fun process(): T
}
class NoResultProcessor : Processor<Unit> {
  override fun process() { // a return type doesn't need to be specified since we are using Unit
    //no return is needed because we're using the Unit type which is returned implicitly
  }
}
class IntResultProcessor : Processor<Int> {
  override fun process(): Int { // the return type must be specified since we are no longer using Unit
    return 42; // here the return is needed because we are no longer using the Unit type
  }
}

fun nothing(){
  // the Nothing type signifies functions that will never return normally (either with Unit or a specified type)
  // It only makes since to use Nothing as a return type or type argument for a type parameter that is used as
  // a generic function return type (like Unit is used above with the Processor interface)
  fun fail(message: String): Nothing {
    throw IllegalStateException(message)
  }

  val person: Person = Person("Name", null)

  //Because the 'Nothing' type is used the compiler infers that if the right side
  //of the elvis operator is reached then there will be no return coming from the fail function
  //so the name variable will never get filled
  //Thats why the fail functions is aloud on the same line as the assignment
  val name = person.company ?: fail("No Company is associated with this person")
}

fun nullabilityAndCollections(){

  //This says that the collection can have Int or null, 'nuff said
  fun readNumbers(reader: BufferedReader): List<Int?> {
    val result = ArrayList<Int?>()
    for (line in reader.lineSequence()) {
      try {
        val number = line.toInt()
        result.add(number)
      }
      catch(e: NumberFormatException) {
        result.add(null)
      }
    }
    return result
  }

  // refactored to use String.toIntOrNull()
  fun readNumbersToIntOrNull(reader: BufferedReader): List<Int?> {
    val result = ArrayList<Int?>()
    for (line in reader.lineSequence()) {
        val number = line.toIntOrNull()
        result.add(number)
    }
    return result
  }

  // this is a nullable list of nullable numbers, notice the two question marks
  val list: List<Int?>? = listOf(1, 2, null)

  // use these stdlib functions with functional programming to filter the nulls and sum the numbers
  fun addValidNumbers(numbers: List<Int?>) {
    val validNumbers = numbers.filterNotNull()
    println("Sum of valid numbers: ${validNumbers.sum()}")
    println("Invalid numbers: ${numbers.size - validNumbers.size}")
  }
}

fun mutableAndReadonlyCollections() {
  //this collection is read only (has not modifier functions) because it uses the Collection interface
  val collection: Collection<Int> = mutableListOf(1, 2, 3)

  //This casts collection as a mutable collection and now it is modifiable
  val mutableCollection = collection as MutableCollection<Int>

  //When creating a collection that can be modified always use the mutable version
  //like 'mutableListOf' instead of the immutable version 'listOf'
  //future versions of Kotlin may make 'listOf', 'mapOf', etc. truly immutable
}

fun arrays(){

  fun main(args: Array<String>) {
    for (i in args.indices) {
      println("Argument $i is: ${args[i]}")
    }
  }

  //initialize each element in an array with a lambda or use arrayOf.
  //here the index of the array is passed into the lambda as i
  val letters = Array<String>(26) { i -> ('a' + i).toString() }

  //arrayOfNulls creates an array containing the specified number of nulls
  val arrNulls = arrayOfNulls<Int?>(5) //the element type must be nullable

  val strings = listOf("a", "b", "c")

  //here the spread operator * is used for varargs and the strings collection is converted to an array
  println("%s/%s/%s".format(*strings.toTypedArray()))

  //to create arrays with a primitive type you must used the corresponding types array pg. 168/195
  val intArr = IntArray(20) { i -> i+1 };
  val intArrOf = intArrayOf(1, 2, 3)

  //use forEachIndexed to get the index as well as the element
  fun mainForEach(args: Array<String>) {
    args.forEachIndexed { index, element ->
      println("Argument $index is: $element")
    }
  }
}