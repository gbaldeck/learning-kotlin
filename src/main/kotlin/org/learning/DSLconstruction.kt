package org.learning

/**
 * Created by gbaldeck on 4/11/2017.
 */
//function passing in a lambda
fun buildString(builderAction: (StringBuilder) -> Unit): String {
  val sb = StringBuilder()
  builderAction(sb) //a StringBuilder is passed to the lambda
  return sb.toString()
}

fun testbuildstring() {
  val s = buildString {
    it.append("Hello, ") //it is being used to refer to the StringBuilder passed in
    it.append("World!")
  }
}

//Below we have converted the StringBuilder parameter that would be passed into the lambda
//into a receiver
// StringBuilder.() -> Unit is an extention function type that is being passed in
//This can be done on only one of the parameters of the lambda
fun buildStringWithReceiver(builderAction: StringBuilder.() -> Unit): String {
  val sb = StringBuilder()
  sb.builderAction() //this passes a StringBuilder as a receiver to the lambda
  return sb.toString()
}

fun testbuildstringwithreceiver() {
  val s = buildStringWithReceiver {
    this.append("Hello, ") //'this' refers to the StringBuilder instance
    append("World!") //the 'this' keyword can be omitted
  }
}

//A receiver object is the receiver of an extension function
//An extension function type is essentially a block of code that can be
//called as an extension function

//You can also declare a variable of an extension function type.
fun extentionFunVariable() {
  val appendExcl: StringBuilder.() -> Unit = { this.append("!") }

  val stringBuilder = StringBuilder("Hi")
  stringBuilder.appendExcl()

  println(stringBuilder)
  println(buildStringWithReceiver(appendExcl))
}

//Improving buildString even more
//Since apply take a lambda with receiver we can pass the parameter directly into it
fun buildStringWithApply(builderAction: StringBuilder.() -> Unit): String =
    StringBuilder().apply(builderAction).toString()

//Here is the implementation of 'apply' in the KSL
//It is an extention function that takes an extention function as its parameter
//Since apply is an extention function itself we are already referring to the 'T'
//when we use the keyword 'this' and everything in block() will be referring to 'T'
//also since it is also an extention function on 'T'
inline fun <T> T.apply(block: T.() -> Unit): T {
  block()
  return this
}

//Here is the implementation of 'with' in the KSL
//It takes the receiver parameter 'T' and then the extention function on 'T' which is 'block'
//and then returns the value 'R' which is whatever 'block()' returns which could be 'T' or
//something completely different
inline fun <T, R> with(receiver: T, block: T.() -> R): R = receiver.block()

//If you don't care about the result 'apply' and 'with' can be used interchangeably
fun applyWithTest() {
  val map = mutableMapOf(1 to "one")

  map.apply { this[2] = "two" }
  with(map) { this[3] = "three" }

  println(map)
//  {1=one, 2=two, 3=three}
}