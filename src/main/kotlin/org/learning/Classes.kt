package org.learning

import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
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
open class User constructor(_nickname: String, val isSubscribed: Boolean = true, val age: Int = 0){
  val nickname: String

  init {
    nickname = _nickname
  }
}

class TwitterUser(nickname: String) : User(nickname){

}

open class Vue {
  constructor(ctx: Any){

  }
  constructor(ctx: Any, attr: Any){

  }
}

class MyButton : Vue {
  constructor(ctx: Any) : super(ctx){}
  constructor(ctx: Any, attr: Any) : super(ctx, attr){}

  //calls a constructor in the same class
  constructor() : this(Any())
}

interface ItsAUser{
  //abstract property. Implementing classes must initialize nickname with a value
  val nickname: String
}

class PrivateUser(override val nickname: String) : ItsAUser

class SubscribingUser(val email: String) : ItsAUser{
  override val nickname: String
    get() = email.substringBefore('@')
}

class FacebookUser(val accountId: Int) : ItsAUser{
  override val nickname: String = getFacebookName(accountId)

  private fun getFacebookName(accountId: Int): String{
    return ""
  }
}

interface NoBackingField{
  val email: String

  //this is possible because no backing field is referenced
  //this can be inherited in classes that implement NoBackingField
  val nickname: String
    get() = email.substringBefore('@')
}

//If you provide custom accessor implementations that don’t use field (for the
//getter if the property is a val and for both accessors if it’s a mutable property), the
//backing field won’t be present.
class BackingFieldUser(val name: String){
  var address: String = "unspecified"
    set(value: String) {
      println("Address was changed for $name: \"$field\" -> \"$value\".")
      field = value
    }
}

class LengthCounter{
  var counter: Int = 0
    private set

  fun addWord(word: String){
    counter += word.length

    //The == operator calls .equals under the hood
    LengthCounter() == LengthCounter()

    //The === operator compares the references of the two objects
    LengthCounter() === LengthCounter()
  }
}

class Client(val name: String, val postalCode: Int) {
  override fun equals(other: Any?): Boolean {
    if (other == null || other !is Client)
      return false
    return name == other.name &&
        postalCode == other.postalCode
  }
  override fun toString() = "Client(name=$name, postalCode=$postalCode)"
  override fun hashCode(): Int = name.hashCode() * 31 + postalCode

  fun copy(name: String = this.name, postalCode: Int = this.postalCode) = Client(name, postalCode)
}

//creating a data class automatically override equals, hashCode, and toString
//The equals and hashCode methods take into account all the properties declared in the primary constructor
//try to keep dataclasses immutable by using val
//use the generated copy method to create a new instance with different values
data class ClientData(val name: String, val postalCode: Int)

//this creates a decorator pattern object, see pg 91/118
class DelegatingCollection<T>(innerList: Collection<T> = ArrayList<T>()) : Collection<T> by innerList{}

//the by keyword tells the class to implement the interfaces methods by calling the same methods on innerSet
//This is possible since innerSet is also a MutableCollection
//then those same functions can be overridden as needed
class CountingSet<T>(val innerSet: MutableCollection<T> = HashSet<T>()) : MutableCollection<T> by innerSet {
  var objectsAdded = 0
  override fun add(element: T): Boolean {
    objectsAdded++
    return innerSet.add(element)
  }
  override fun addAll(c: Collection<T>): Boolean {
    objectsAdded += c.size
    return innerSet.addAll(c)
  }
}

//The object keyword. Objects are classes that are singletons
//The object keyword defines a class and a variable of that class in a single statement
//an object has no constructor because it is initialized immediately
//dependency injection is still the better option because it allows constructors
object Payroll {
  val allEmployees = arrayListOf<Any>()

  fun calculateSalary(){
    for (person in allEmployees){

    }
  }
}


//Objects can be defined inside a class and then accessed like ObjectInside.InternalObject.name
class ObjectInside {
  object InternalObject {
    val name = "Jobe"
  }
}

//Factory pattern with companion object
//Companion objects are different than an object defined in a class in that
//you can access its member like static functions and variable on the class
class NextUser private constructor(val nickname: String) {
  companion object {
    fun newSubscribingUser(email: String) = User(email.substringBefore('@'))
    fun newFacebookUser(accountId: Int) = User(accountId.toString())
  }
}

//Extension function on a companion object. It can then be used like a normal companion object function on the class
//I didn't give the companion object a name so it can be referred to by Companion
//I can give it a name like 'companion object CompanionOne' in its declaration
fun NextUser.Companion.printHello() {
  println("Hello!")
}

object Window{
  fun addMouseListener(listener: MouseAdapter){}
}

//This shows how to use object to create an anonymous class that extends MouseAdapter()
fun createMouseListener(){
  Window.addMouseListener(
      object : MouseAdapter() {
        override fun mouseClicked(e: MouseEvent){}
        override fun mouseEntered(e: MouseEvent?) {}
      }
  )
}

//closure with anonymous object
fun countClicks(window : Window){
  var clickCount = 0

  window.addMouseListener(
      object : MouseAdapter() {
        override fun mouseClicked(e: MouseEvent?) {
          clickCount++ //click count is in the closure
        }
      }
  )
}