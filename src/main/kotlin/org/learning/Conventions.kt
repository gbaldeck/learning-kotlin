package org.learning

import java.math.BigDecimal
import java.time.LocalDate

/**
 * Created by gbaldeck on 3/9/2017.
 */

data class Point(val x: Int, val y: Int) {

  operator fun plus(other: Point): Point {
    return Point(x + other.x, y + other.y)
  }

}

//it can also be declared as an extension function
operator fun Point.plus(other: Point): Point {
  return Point(x + other.x, y + other.y)
}

fun test1(){
  val p1 = Point(10, 30)
  val p2 = Point(12, 111)

  val p3 = p1 + p2 //this calls the operator function 'plus' defined in the Point class
}

//List of all the binary operators you can define and the corresponding function names
//Expression    Function name
//a * b         times
//a / b         div
//a % b         mod
//a + b         plus
//a - b         minus

//you can use different types in the operator functions as well
operator fun Point.times(scale: Double): Point {
  return Point((x * scale).toInt(), (y * scale).toInt())
}

fun test2(){
  var point = Point(20, 40)

  //the order must be object first then value e.g. you can't change it to '1.5 * point'
  // if we wanted to do value first we would have to
  //overload the times function for the Double class
  point = point * 1.5
}

//notice object type, argument type, and return type are all different
operator fun Char.times(count: Int): String {
  return toString().repeat(count)
}

fun test3(){
  //charString will be of type String even though operand is a Char and the other is an Int
  //this is because of the Char.times extension function above
  val charString = 'a' * 25
}

//Note that you can overload operator functions like regular functions: you can
//define multiple methods with different parameter types for the same method name


//If you define a function named plusAssign with the Unit return type, Kotlin will
//call it when the += operator is used. Other binary arithmetic operators have similarly
//named counterparts: minusAssign, timesAssign, and so on.
operator fun <T> MutableCollection<T>.plusAssign(element: T) {
  this.add(element)
}

fun unaryOperators(){
  operator fun Point.unaryMinus(): Point {
    return Point(-x, -y)
  }

  val p = Point(10, 20)

  val negP = -p //returns Point(-10, -20)

//  Overloadable unary arithmetic operators
//  Expression          Function name
//  +a                  unaryPlus
//  -a                  unaryMinus
//  !a                  not
//  ++a, a++            inc
//  --a, a--            dec


//  When you define the inc and dec functions to overload increment and decrement
//  operators, the compiler automatically supports the same semantics for pre- and postincrement
//  operators as for the regular number types.

  //once a method is marked as 'operator' all methods that implement or override it are 'operator' also
  operator fun BigDecimal.inc() = this + BigDecimal.ONE

  var bd = BigDecimal.ZERO
  println(bd++) //prints zero, bd increments after the println executes
  println(++bd) //prints one, bd increments before the println executes
}

fun compareTo(){
  class Person(val firstName: String, val lastName: String) : Comparable<Person> {
    override fun compareTo(other: Person): Int {

      //this evaluates the given callbacks in order, and compares values
      return compareValuesBy(this, other, Person::lastName, Person::firstName)
    }
  }
}

//this allow you to use the index operator [] like with an array or map
//you can also overload the function with different return types and argument types
//you can also use multiple arguments to make multi-dimensional objects e.g. arr[x, y]
operator fun Point.get(index: Int): Int {
  return when(index) {
    0 -> x
    1 -> y
    else ->
      throw IndexOutOfBoundsException("Invalid coordinate $index")
  }
}

fun test4(){
  val p = Point(10, 20)
  println(p[1]) //prints 10 because of the 'get' operator function above
}

data class MutablePoint(var x: Int, var y: Int)

//you can now set the values like point[0] = 20 (sets x to 20)
//the last parameter in set always receive the value
operator fun MutablePoint.set(index: Int, value: Int) {
  when(index) {
    0 -> x = value
    1 -> y = value
    else ->
      throw IndexOutOfBoundsException("Invalid coordinate $index")
  }
}

data class Rect(val upperLeft: Point, val lowerRight: Point) :Comparable<Rect> {
  override fun compareTo(other: Rect): Int {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}

//the contains convention works with the 'in' operator
operator fun Rect.contains(p: Point): Boolean {
  //the until stdlib function builds an open range then the in operator checks if a point belongs to the range
  //an open range is a range that doesn't include its ending point
  //a close range '10..20' includes both 10, 20, and the numbers inbetween
  //an open range '10 until 20' includes 10 and then the numbers between 10 and 20. It does not include 20.
  return p.x in upperLeft.x until lowerRight.x &&
      p.y in upperLeft.y until lowerRight.y
}

fun test5(){
  val rec = Rect(Point(10, 20), Point(50, 50))

  println(Point(20, 30) in rec) //prints true
  println(Point(5, 5) in rec) //prints false
}

//see page 185/212 for an explanation of overloading the rangeTo operator '..'
operator fun Rect.rangeTo(obj: Rect) : ClosedRange<Rect>?{
  return null;
}

//for using the 'in' operator in a for loop e.g. for(x in "hello there")
//use the iterator method
operator fun ClosedRange<LocalDate>.iterator(): Iterator<LocalDate> =
    object : Iterator<LocalDate> { //the object implements an Iteraotr over LocalDate elements
      var current = start

      override fun hasNext() = current <= endInclusive //note the compareTo convention used for dates.

      override fun next() = current.apply { //returns the current date as a result before changing it
        current = plusDays(1) //increments the current date by one day
      }
    }

fun test6(){
  val newYear = LocalDate.ofYearDay(2017, 1) //first day of the year
  val daysOff = newYear.minusDays(1)..newYear //creates the ClosedRange (last value is included)
  for (dayOff in daysOff) { println(dayOff) } //prints newYear-1 and newYear
}