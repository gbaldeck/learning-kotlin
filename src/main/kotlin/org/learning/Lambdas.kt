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
class Book(val title: String, val authors: List<String>)

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
  val people = listOf(TempPerson("Alice", 27), TempPerson("Jobe", 73))
  println(people.filter { it.age > 30 }.map(TempPerson::name))

  //this performs the maxBy function for every person in the collection! (no good)
  people.filter { it.age == people.maxBy(TempPerson::age)?.age }

  //this performs maxBy once! (good)
  val maxAge = people.maxBy(TempPerson::age)?.age
  people.filter { it.age == maxAge }

  //maps to values
  val numbers = mapOf(0 to "zero", 1 to "one")
  numbers.mapValues { it.value.toUpperCase() } //there is also a mapKeys function

  //predicates
  val canBeInClub27 = { p: TempPerson -> p.age <= 27 }

  people.all(canBeInClub27) //returns true if all elements match the predicate
  people.any(canBeInClub27) //returns true if any of the elements match the predicate
  people.count(canBeInClub27) // same as people.filter(canBeInClub27).size
  people.find(canBeInClub27) //returns the first matching element or null if none match
  //find can be replaces with firstOrNull if it makes more sense

  people.groupBy { it.age } //returns a map where the keys are what the elements are grouped by
  //(in this case the keys are age) while the values are lists of the elements that match each key
  //So the returned object is Map<Int, List<TempPerson>>

  val strList = listOf("a", "ab", "c")
  strList.groupBy(String::first) //returns {a=[a, ab], c=[c]}

  val books = listOf(Book("Foundation", listOf("Isaac Asimov")), Book("Textbook", listOf("Some guy", "some girl", "them over there")))
  books.flatMap { it.authors }.toSet() //Set of all authors who wrote books in the "books" collection
  //flatMap maps each element to a collection and then flattens the collections into one. pg. 118/145
  //toSet removes the duplicates in the resulting collection

  //use flatten like listOfLists.flatten() if you only need to combine lists and not transform anything

}

fun sequences(){
  val people = listOf(TempPerson("Alice", 27), TempPerson("Jobe", 73))

  //this creates two temporary lists. One for map and one for filter. Inefficient
  people.map(TempPerson::name).filter { it.startsWith("A") }

  //pg.119/146
  //convert it to use Sequences. Sequences supports the same api as Collections
  //Sequences are more efficient because it doesn't create two lists
  people.asSequence()
      .map(TempPerson::name)
      .filter { it.startsWith("A") }
      .toList()

  //intermediate operation - returns a sequence that knows how to transform the elements of the original sequence
  //e.g. map and filter

  //terminal operation - returns a result, which may be a collection, element, etc.
  //It returns any object that's obtained by the sequence of transformations of the initial collection
  //e.g. toList()

  //sequences are lazily evaluated so terminal operations like toList() must be called
  //on the intermediate operations like map and filter in order for them to be run
  //terminal operations are always eager and intermediate operations are always lazy

  //generate a sequence with a lambda, it will be lazily done until sum is called
  val naturalNumbers = generateSequence(0) { it + 1 }
  val numbersTo100 = naturalNumbers.takeWhile { it <= 100 }
  numbersTo100.sum() //evaluated here, the above expressions are lazy
}