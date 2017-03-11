package org.learning

import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by gbaldeck on 3/9/2017.
 */

class Email

fun loadEmails(person: Any): List<Email> {
  println("Load emails for ${person}")
  return listOf()
}

//This uses the backing property technique to lazily load emails
//into the _email backing property. It is then accessable through the
//emails property
class VerboseLazyPerson(val name: String) {
  private var _emails: List<Email>? = null //lazily loading emails so initialize as null
  val emails: List<Email>
    get() {
      if (_emails == null) {
        _emails = loadEmails(this)
      }
      return _emails!! //assert not null
    }
}

//using the by keyword is setting up a delegated property pg. 189/216
//the object being delegated to from the property must have getValue and setValue functions
//the lazy function returns an object that has a method called getValue with the proper signature
//the lambda passed to it isn't called until the property is accessed
class LazyPerson(val name: String) {
  val emails by lazy { loadEmails(this) } //the emails property is initialized with the lambda when the property is accessed
}

open class PropertyChangeAware {
  protected val changeSupport = PropertyChangeSupport(this)
  fun addPropertyChangeListener(listener: PropertyChangeListener) {
    changeSupport.addPropertyChangeListener(listener)
  }

  fun removePropertyChangeListener(listener: PropertyChangeListener) {
    changeSupport.removePropertyChangeListener(listener)
  }
}

//change detection pg. 192/219
//leaving val or var off of the parameter makes it a normal parameter and does
//not make it into a property with getters and setters
class PersonPropertyChange(val name: String, age: Int, salary: Int) : PropertyChangeAware() {

  //property with getters and setters initialized here
  var age: Int = age
    set(newValue) {
      val oldValue = field //using a built in backing field
      field = newValue
      changeSupport.firePropertyChange("age", oldValue, newValue)
    }

  var salary: Int = salary
    set(newValue) {
      val oldValue = field
      field = newValue
      changeSupport.firePropertyChange("salary", oldValue, newValue)
    }
}

fun test11() {

  val p = PersonPropertyChange("Dmitry", 34, 2000)

  p.addPropertyChangeListener(
    PropertyChangeListener { event ->
      println("Property ${event.propertyName} changed " +
        "from ${event.oldValue} to ${event.newValue}")
    }
  )
  p.age = 35
  p.salary = 2100
}

//this is an object that will be delegated to since it has getValue and setValue methods
class ObservablePropertyNoBy(val propName: String, var propValue: Int, val changeSupport: PropertyChangeSupport) {
  fun getValue(): Int = propValue
  fun setValue(newValue: Int) {
    val oldValue = propValue
    propValue = newValue
    changeSupport.firePropertyChange(propName, oldValue, newValue)
  }
}

//without using the by keyword to delegate. This is how delegation works under the covers
//using the by keyword
class PersonNoBy(val name: String, age: Int, salary: Int) : PropertyChangeAware() {

  val _age = ObservablePropertyNoBy("age", age, changeSupport)
  var age: Int
    get() = _age.getValue()
    set(value) {
      _age.setValue(value)
    }

  val _salary = ObservablePropertyNoBy("salary", salary, changeSupport)
  var salary: Int
    get() = _salary.getValue()
    set(value) {
      _salary.setValue(value)
    }
}

//setup to match with kotlin conventions and use with the by keyword
class ObservablePropertyWithBy(var propValue: Int, val changeSupport: PropertyChangeSupport) {
  operator fun getValue(p: PersonWithBy, prop: KProperty<*>): Int = propValue
  operator fun setValue(p: PersonWithBy, prop: KProperty<*>, newValue: Int) {
    val oldValue = propValue
    propValue = newValue
    changeSupport.firePropertyChange(prop.name, oldValue, newValue)
  }
}

class PersonWithBy(val name: String, age: Int, salary: Int) : PropertyChangeAware() {
  var age: Int by ObservablePropertyWithBy(age, changeSupport)
  var salary: Int by ObservablePropertyWithBy(salary, changeSupport)
}

//This uses the Kotlin standard library function Delegates.observable that does the same thing as above
//Kproperty is how the property is represented in the delegate
//pg. 196/223
//You can customize where the value of the property is stored (in a map, in a database table, or in
//the cookies of a user session) and also what happens when the property is accessed (to
//add validation, change notifications, and so on)
class PersonWithBuiltInStdLib(val name: String, age: Int, salary: Int) : PropertyChangeAware() {
  private val observer =
    {
      prop: KProperty<*>, oldValue: Int, newValue: Int ->
      changeSupport.firePropertyChange(prop.name, oldValue, newValue)
    }
  var age: Int by Delegates.observable(age, observer)
  var salary: Int by Delegates.observable(salary, observer)
}

open class IdTable {
  protected fun varchar(name: String, length: Int): IdTable = this
  protected fun integer(age: String){}

  fun index(): Int = 1
}

//see pg 198/225 for using while creating a framework
class EntityID
open class Entity(entityID: EntityID)

class Column<T> {
  operator fun getValue(o: Entity, desc: KProperty<*>): T {
    return Unit;
  }
  operator fun setValue(o: Entity, desc: KProperty<*>, value: T) {
// update the value in the database
  }
}

object Users : IdTable() {
  val name: Column<String> = varchar("name", 50).index()
  val age: Column<Int> = integer("age")
}

class AUser(id: EntityID) : Entity(id) {
  var name: String by Users.name
  var age: Int by Users.age
}