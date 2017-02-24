package org.learning

/**
 * Created by gramb on 2/23/2017.
 */

//pg. 107/134 for how lambda syntax works in arguments
fun findTheOldestAllTheShortcuts(people : List<User>) = println(people.maxBy(User::age))

fun findTheOldestKindaShortcuts(people : List<User>) = println(people.maxBy{ it.age })

fun findTheOldestOneShortcut(people : List<User>) = println(people.maxBy{ p -> p.age })

fun findTheOldestNoShortcuts(people : List<User>) = println(people.maxBy{ p: User -> p.age })

fun lambdasOne(){
  val sum = { x: Int, y: Int -> x + y}

  println(sum(2,6))

  //the run function runs a lambda
  run { println(42) }
}

fun myJoinToString(){
  //remember that all parameters must be named after your first named parameter
  val people = listOf(User("Alice", age=29), User("Jobe",age=57))

  val names = people.joinToString(separator = " ", transform = { it.nickname })

  val namesWithLambdaOutside = people.joinToString(" ") { it.nickname }
}