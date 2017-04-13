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