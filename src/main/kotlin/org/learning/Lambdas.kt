package org.learning

/**
 * Created by gramb on 2/23/2017.
 */

//pg. 107/134 for how lambda syntax works in arguments
fun findTheOldestAllTheShortcuts(people : List<User>) = println(people.maxBy(User::age))

//the it keyword is generated only if no parameter in the lambda is specified
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

//outer variables can be accessed in the lambda scope
fun printMessagesWithPrefix(messages: Collection<String>, prefix: String) =
  messages.forEach { "$prefix $it" }

fun memberReferences(){
  //this is the same as ...
  val getAge = { user: User -> user.age }

  //...this
  val getAgeRef = User::age

  //functions outside of classes can be referenced as well
  fun salute() = println("Salute!")
  val saluteNow = ::salute
}

data class TempPerson(val name: String, val age: Int)

fun constructorReferences(){
  //this stores the action of creating an instance of a class in a variable
  val createPerson = ::TempPerson
  val person = createPerson("Alice", 29)

  //extension functions can be referenced the same way
  fun TempPerson.isAdult() = age >= 21
  val isAdult = TempPerson::isAdult

  //bound member reference for a specific instance
  val p = TempPerson("Dmitry", 34)
  val dmitrysAgeFunction = p::age
  dmitrysAgeFunction() //will return 34
}

fun functionalProgramming(){
  val list = listOf(1, 2, 3, 4)
  println(list.filter { it % 2 == 0 })
  println(list.map { it * it })

  //chaining
  val people = listOf(TempPerson("Alice", 29), TempPerson("Jobe", 73))
  println(people.filter { it.age > 30 }.map(TempPerson::name))

  //this performs the maxBy function for every person in the collection! (no good)
  people.filter { it.age == people.maxBy(TempPerson::age)?.age }

  //this performs maxBy once! (good)
  val maxAge = people.maxBy(TempPerson::age)?.age
  people.filter { it.age == maxAge }
}