package org.learning

import java.io.Serializable

/**
 * Created by gramb on 2/19/2017.
 */

interface Clickable {
  fun click() //members in interfaces are always open
  fun showOff() = println("I'm clickable!")
}

interface Focusable {
  fun showOff() = println("I'm focusable!")
}

class ButtonOne : Clickable, Focusable {
  override fun click() = println("I was clicked!")

  override fun showOff() {
    super<Clickable>.showOff()
    super<Focusable>.showOff()
  }
}

//all classes are final by default, use the open keyword to make them extendable
open class RichButton: Clickable {
  //this function is final, meaning it is not overridable
  fun disable(){}

  //this function is overridable
  open fun animate(){}

  //this function is originally open because of the clickable interface
  //but this implementation of it is made final for all classes that inherit from RichButton
  final override fun click() {}
}

abstract class Animated {
  abstract fun animate()


  open fun stopAnimating(){}
  fun animateTwice(){}
}

//internal means private to the kotlin module, which is a set of compiled kotlin files, in this case the project
internal open class TalkativeButton : Focusable {
  private fun yell() = println("Hey!")
  protected fun whisper() = println("Let's talk!")
}

//an extension function must have the same visibility as the class it is extending
//so it must be declared internal as well
internal fun TalkativeButton.giveSpeech() {
  //yell() //extention functions do not get to access a classes private or protected members
  //whisper()
}

//private classes are private to this file only
private class ImPrivate {}

interface State : Serializable

interface View {
  fun getCurrentState() : State
  fun restoreState(state: State){}
}

class ButtonTwo : View {
  override fun getCurrentState(): State = ButtonState()

  override fun restoreState(state: State) {
    super.restoreState(state)
  }

  //no reference to the outer class is stored here
  class ButtonState : State {

  }

  //using the inner keyword stores a reference to the outer class and it can be
  //accessed like below
  inner class ButtonStateTwo : State {
    fun getOuterClassReference(): ButtonTwo = this@ButtonTwo
  }
}

//sealed classes require you to define subclasses in the class itself or the same file
sealed class ExprTwo {
  class Num(val value: Int) : ExprTwo()
  class Sum(val left: ExprTwo, val right: ExprTwo) : ExprTwo()
}

fun evalTwo(e: ExprTwo): Int =
  when (e) {
    is ExprTwo.Num -> e.value
    is ExprTwo.Sum -> evalTwo(e.right) + evalTwo(e.left)
    is SumSum -> 1
  }

//Kotlin 1.1 allows subclasses of sealed classes anywhere in the same file
class SumSum() : ExprTwo()

//in the primary constructor the constructor keywork is required if you're using annotations
open class User constructor(_nickname: String, val isSubscribed: Boolean = true){
  val nickname: String

  init {
    nickname = _nickname
  }
}

class TwitterUser(nickname: String) : User(nickname){

}