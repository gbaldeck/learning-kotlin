package org.learning

import kotlinx.html.*
import kotlinx.html.stream.createHTML
import java.time.LocalDate
import java.time.Period

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


fun applyWithTest() {
  val map = mutableMapOf(1 to "one")

  map.apply { this[2] = "two" } //returns the receiver ('map' in this case)
  with(map) { this[3] = "three" } //returns the result of the lambda after running operations on the receiver
  //If you don't care whats returned they are interchangeable

  println(map)
// prints  {1=one, 2=two, 3=three}
}

//Here is an example of an HTML DSL
fun createSimpleTable() = createHTML().
  table {
    tr {
      td { +"cell" }
    }
  }

//And here is its basic implementation that only allows 'tr' inside of 'table' and 'td' inside of 'tr'
//TABLE, TR, and TD are utility classes that shouldn't appear explicitly in the code, and
//that’s why they’re named in capital letters
fun implBasic() {
  open class Tag

  class TABLE : Tag() {
    fun tr(init: TR.() -> Unit) = TR().apply(init)
  }

  class TR : Tag() {
    fun td(init: TD.() -> Unit) = TD().apply(init)
  }

  class TD : Tag()
}

//Here is the same listing making all the receivers explicit
fun createSimpleTableExplicit() = createHTML().
  table {
    (this@table).tr {
      (this@tr).td {
        +"cell"
      }
    }
  }

//Create your own HTML DSL below
open class Tag(val name: String) {
  private val children = mutableListOf<Tag>()

  protected fun <T: Tag> doInit(child: T, init: T.() -> Unit) {
    child.init()
    children.add(child)
  }

  override fun toString() = "<$name>${children.joinToString("")}</$name>"
}

//creates a new table and applies the lambda
fun table(init: TABLE.() -> Unit) = TABLE().apply(init)

class TABLE : Tag("table") {
  fun tr(init: TR.() -> Unit) = doInit(TR(), init)
}

class TR : Tag("tr") {
  fun td(init: TD.() -> Unit) = doInit(TD(), init)
}

class TD : Tag("td")

fun createTable() =
  table {
    tr {
      td {
      }
    }
  }

fun testCreateTable(){
  println(createTable())
  //prints <table><tr><td></td></tr></table>
}

fun createAnotherTable() = table {
  for (i in 1..2) {
    tr { //each call to 'tr' creates a new TR tag and adds it to the children of TABLE
      td {
      }
    }
  }
}

fun testCreateAnotherTable() {
  println(createAnotherTable())
//prints <table><tr><td></td></tr><tr><td></td></tr></table>
}

//Bootstrap example in normal HTML
//<div class="dropdown">
//  <button class="btn dropdown-toggle">
//    Dropdown
//    <span class="caret"></span>
//  </button>
//  <ul class="dropdown-menu">
//    <li><a href="#">Action</a></li>
//    <li><a href="#">Another action</a></li>
//    <li role="separator" class="divider"></li>
//    <li class="dropdown-header">Header</li>
//    <li><a href="#">Separated link</a></li>
//  </ul>
//</div>

//Bootstrap example in kotlinx.html
fun buildDropdown() = createHTML().div(classes = "dropdown") {
  button(classes = "btn dropdown-toggle") {
    +"Dropdown"
    span(classes = "caret")
  }
  ul(classes = "dropdown-menu") {
    li { a("#") { +"Action" } }
    li { a("#") { +"Another action" } }
    li { role = "separator"; classes = setOf("divider") }
    li { classes = setOf("dropdown-header"); +"Header" }
    li { a("#") { +"Separated link" } }
  }
}

//Now extract them into different functions

fun <R> TagConsumer<R>.dropdown(ext: DIV.() -> Unit) = div(classes = "dropdown", block = ext)

fun DIV.dropdownButton(ext: BUTTON.() -> Unit) = button(classes = "btn dropdown-toggle", block = ext)

fun DIV.dropdownMenu(ext: UL.() -> Unit) = ul(classes = "dropdown-menu", block = ext)

fun UL.item(href: String, name:String) = li { a(href) { +name } }

fun UL.divider() = li { role = "seperator"; classes = setOf("divider")}

fun UL.dropdownHeader(title: String) = li { classes = setOf("dropdown-header"); +title}

//And we have our own custom DSL for html components styled with bootstrap
fun dropdownExample() = createHTML().dropdown {
  dropdownButton { +"Dropdown" }
  dropdownMenu {
    item("#", "Action")
    item("#", "Another action")
    divider()
    dropdownHeader("Header")
    item("#", "Separated link")
  }
}

fun <T> propaComponent(html: TagConsumer<T>.() -> Unit){
  val tagConsumer = createHTML()
  tagConsumer.html()
}

//With the invoke convention, you can call a class or object as a function
//A class for which the invoke method with an operator modifier is defined can be called as a function.
class Greeter(val greeting: String) {
  operator fun invoke(name: String) {
    println("$greeting, $name!")
  }
}
//You can define it
//with any number of parameters and with any return type, or even define multiple
//overloads of invoke with different parameter types.
fun testGreeter() {
  val bavarianGreeter = Greeter("Servus")
  bavarianGreeter("Dmitry")
}

//pg.300/327 Using function types as interfaces
data class Issue(
  val id: String, val project: String, val type: String,
  val priority: String, val description: String
)

class ImportantIssuesPredicate(val project: String): (Issue) -> Boolean {
  override fun invoke(issue: Issue): Boolean {
    return issue.project == project && issue.isImportant()
  }

  private fun Issue.isImportant(): Boolean {
    return type == "Bug" &&
      (priority == "Major" || priority == "Critical")
  }
}

fun testIssue(){
  val i1 = Issue("IDEA-154446", "IDEA", "Bug", "Major", "Save settings failed")
  val i2 = Issue("KT-12183", "Kotlin", "Feature", "Normal",
    "Intention: convert several calls on the same receiver to with/apply")

  val predicate = ImportantIssuesPredicate("IDEA")

  for (issue in listOf(i1, i2).filter(predicate)) {
    println(issue.id)
  }
  //prints IDEA-154446
}

interface Matcher<T> {
  fun test(value: T)
}

class startWith(val prefix: String) : Matcher<String> {
  override fun test(value: String) {
    if (!value.startsWith(prefix))
      throw AssertionError("String $value does not start with $prefix")
  }
}

//remember infix calls
infix fun <T> T.should(matcher: Matcher<T>) = matcher.test(this)

fun testInfix(){
  "kotlin" should startWith("kot")
}

//chained infix calls pg 304/331
object start

infix fun String.should(x: start): StartWrapper = StartWrapper(this)

class StartWrapper(val value: String) {
  infix fun with(prefix: String) =
    if (!value.startsWith(prefix))
      throw AssertionError("String does not start with $prefix: $value")
    else Unit
}

fun testChaninedInfix(){
  //chained infix calls with the 'start' object as a parameter
  "kotlin".should(start).with("kot")

  //can become
  "kotlin" should start with "kot"
}

//now using extention properties for a different dsl on dates
val Int.days: Period
  get() = Period.ofDays(this)

val Period.ago: LocalDate
  get() = LocalDate.now() - this

val Period.fromNow: LocalDate
  get() = LocalDate.now() + this


fun testDateDsl(){
  println(1.days.ago)
  println(1.days.fromNow)

}

//pg. 308/335
//for making extention functions scoped you can do this
fun Table.select(where: SqlExpressionBuilder.() -> Op<Boolean>) : Query
object SqlExpressionBuilder {
  //this infix function can only be used in the context of the //sql expression builder object
  infix fun<T> Column<T>.eq(t: T) : Op<Boolean>

}
val result = (Country join Customer)
    .select { Country.name eq "USA" } //this line is possible because of the SqlExpressionBuilder
result.forEach { println(it[Customer.name]) }


//my own test
interface PropaComponent: (HTMLTag.() -> Unit) -> Unit{
  override fun invoke(ext: HTMLTag.() -> Unit) {
    (this as HTMLTag).ext()
  }
}