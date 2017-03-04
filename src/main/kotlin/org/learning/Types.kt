package org.learning

/**
 * Created by gramb on 3/4/2017.
 */

//this function is not expected to have a null argument
fun strLenNoNull(s: String) = s.length

//this function allows a null argument
//putting a question mark after the type indicates that it can store a null reference
// ?. is the safe-call operator.
// "s?.length" is equal to "if(s!=null) s.length else null"
fun strLenNull(s: String?) = s?.length

//class with a nullable property
class Employee(val name: String, val manager: Employee?)

//here manager.getName() is only called if manager is not null. Else it returns null
//The return type is String? which means that a String object or null can be returned
fun managerName(employee: Employee): String? = employee.manager?.name

class Address(val streetAddress: String, val zipCode: Int, val city: String, val country: String)
class Company(val name: String, val address: Address?)
class Person(val name: String, val company: Company?)

//the safe-call operator can also be chained
fun Person.countryName(): String {
  val country = this.company?.address?.country

  return if (country != null) country else "Unknown"
}

//the elvis operator ?: is used to provide default values
// 's ?: ""' is equal to 'if (s!=null) s else ""'
fun foo(s: String?) = s ?: ""

//the elvis operator can be used with the safe-call operator like below
// "s?.length ?: 0" is equal to:
// val length = if (s!=null) s.length else null --this is the safe-call operator
// if (length != null) length else 0 --this is the result of the safe-call operator used in the elvis operator
fun strLenSafe(s: String?) = s?.length ?: 0

//here is the function Person.countryName but on one line
fun Person.elvisCountryName(): String = company?.address?.country ?: "Unknown" //company has an implied "this" before it

//throw and return are expressions so they can be used on the right side of the elvis operator
fun printShippingLabel(person: Person) {
  val address = person.company?.address
    ?: throw IllegalArgumentException("No address")
  with (address) {
    println(streetAddress)
    println("$zipCode $city, $country")
  }
}

//the as? operator tries to cast a value to the specified type and returns null if
//the value doesn't have the proper type. A common use is with the elvis operator
class SafeCastPerson(val firstName: String, val lastName: String){
  override fun equals(o: Any?): Boolean{

    // this line says if casting o to SafeCastPerson returns null instead of
    // o as type SafeCastPerson then have the function return false
    val otherPerson = o as? SafeCastPerson ?: return false

    //after the safe-cast the variable otherPerson is smart-cast to the SafeCastPerson type
    return otherPerson.firstName == firstName && otherPerson.lastName == lastName
  }
}

fun testSafeCastPerson(): Boolean{
  val p1 = SafeCastPerson("Dmitry", "Jemerov")
  val p2 = SafeCastPerson("Dmitry", "Jemerov")

  return p1 == p2 //The == operator calls the equals method
}

//the !! operator asserts that a value is not null and throws an exception if it is
//This function converts a nullable type to a not null one
//if s is null then an exception is thrown on the line with s!!
fun ignoreNulls(s: String?) {
  val sNotNull: String = s!!
  println(sNotNull.length)
}