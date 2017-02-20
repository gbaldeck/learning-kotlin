package org.learning

import org.learning.lastChar as last //giving the function a different name in this file

/**
 * Created by gbaldeck on 2/7/2017.
 */

fun main(args: Array<String>) {
  println(Color.BLUE.rgb());
  println(getMnemonic(Color.BLUE))
  println(getWarmth(Color.ORANGE))

  println(eval(Sum(Num(2), Sum(Num(5), Num(7)))))

  for (i in 1..100) {
    print(fizzBuzz(i))
  }
  println("");
  for (i in 100 downTo 1 step 2) {
    print(fizzBuzz(i))
  }
  println("");
  binaryReps();

  val listOne = listOf(1, 2, 3);
  //Join to string is an extension function on the Collections class
  //All remaining parameters after the first named parameter must be named also
  println(listOne.joinToString(seperator = "; ", prefix = "(", postfix = ")"))

  //set default parameter values in the function so I can omit some parameters and not have to
  //do function overloading
  println(listOne.joinToString())
  println(listOne.joinToString("; "))

  //using default constructor values and named parameters I can omit some of the parameters and
  //put the named parameters in any order I want
  println(listOne.joinToString(postfix = ";", prefix = "# "))

  println("Kotlin".last())
  println(listOf("one", "two", "eight").join(" ")) // the join extension function requires strings

  //using extension properties
  val sb = StringBuilder("Kotlin");
  println(sb.firstChar)
  sb.firstChar = 'C'
  println(sb)

  printStuff("value one", "two", "seven", "twenty four")

  // 'to' is actually an infix function, not a reserved construct
  val tempMap = mapOf(1 to "one", 7 to "seven", 53 to "fifty-three")

  val (number, name) = 1 to "one" //returns a Pair(1, "one") and assigns 1 to 'number' and "one" to 'name'
  //This portion 'val (number, name) =...' is called the destructuring declaration and works with more than just pairs

  //Using my custom infix function. this is syntactic sugar for 1.plus(5)
  println(1 plus 5)

  //Kotlin stdlib regex! this is awesome!
  println("12.345-6.A".split("\\.|-".toRegex()))

  //Kotlin string helper functions
  parsePath("/Users/yole/kotlin-book/chapter.adoc")

  //How to use '$' in a triple quoted multiline string
  println("""${'$'}99.9""")

  //local functions
  class User(val id: Int, val name: String, val address: String)

  fun saveUser(user: User){

    //local function
    fun validate(user: User, value: String, fieldName: String){
      if (value.isEmpty())
        throw IllegalArgumentException("Can't save user ${user.id}: empty $fieldName")
    }

    validate(user, user.name, "Name")
    validate(user, user.address, "Address")
  }

  ButtonOne().click()
  ButtonOne().showOff()

  val alice = User("Alice")
  println(alice.isSubscribed)
  val bob = User("Bob", false)
  println(bob.isSubscribed)
  val carol = User("Carol", isSubscribed = false)
  println(carol.isSubscribed)
}