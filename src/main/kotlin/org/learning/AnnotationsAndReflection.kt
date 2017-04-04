package org.learning

import java.util.*
import kotlin.reflect.*
import kotlin.reflect.full.createInstance

/**
 * Created by gbaldeck on 3/29/2017.
 */

//Types of annotation parameters pg.255/282

//Annotations can have parameters of the following types only: primitive types,
//strings, enums, class references, other annotation classes, and arrays thereof.

//1.) To specify a class as an annotation argument, put ::class after the class name:
//  @MyAnnotation(MyClass::class)

//2.) To specify another annotation as an argument, don’t put the @ character before the
//annotation name.

//3.)To specify an array as an argument, use the arrayOf function:
//  @RequestMapping(path = arrayOf("/foo", "/bar"))

//To use a property as an annotation argument, you need to mark it with a const modifier,
//which tells the compiler that the property is a compile-time constant.
const val TEST_TIMEOUT = 100L

@Test(timeout = TEST_TIMEOUT) fun testMethod() {}

//Use-site targets are used to specify where the annotation should be applied
//For example a getter, setter, or constructor
//pg. 257/284 Has the full list of supported use-site targets

@get:Rule //@get applies the target to a getter only

class HasTempFolder {
  @get:Rule //Here it's saying to only apply the @Rule annotation to the getter of the property
  val folder = TemporaryFolder()

  @Test
  fun testUsingTempFolder() {
    val createdFile = folder.newFile("myfile.txt")
    val createdFolder = folder.newFolder("subfolder")
  }
}

//Declaring annotations
//Annotations classes do not contain a body because it is only the representation
//of the metadata structure associated with declarations and expressions
annotation class JsonExclude1(val name: String)

//Meta-annotations are annotations that can be applied to annotations classes
//The @Target meta-annaotations is used to specify the valid targets of an annotation
@Target(AnnotationTarget.PROPERTY)
annotation class JsonExclude2

//To define your own meta-annotation use ANNOTATION_CLASS
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class BindingAnnotation

@BindingAnnotation
annotation class MyBinding

//Using a class as an annotation parameter
annotation class DeserializeInterface(val targetClass: KClass<out Any>)

interface CompanyAnno {
  val name: String
}

data class CompanyImplAnno(override val name: String) : CompanyAnno

data class PersonAnno(
  val name: String,
  @DeserializeInterface(CompanyImplAnno::class) val company: CompanyAnno
)

//pg.263/290 Generic classes as annotation parameters
//Whenever you need to use a class as an annotation argument do it like this
//explained on pg 263/290

//only use the <*> if your class takes a type argument
annotation class YourAnnotation(
  val yourProperty: KClass<out YourClass<*>>
)

//example
interface ValueSerializer<T> {
  fun toJsonValue(value: T): Any?
  fun fromJsonValue(jsonValue: Any?): T
}

annotation class CustomSerializer(
  val serializerClass: KClass<out ValueSerializer<*>>
)

class PersonBasic(val name: String, var age: Int)

fun reflectionExamples1() {
  //get a class at runtime
  val person = PersonBasic("Alice", 29)
  val kClass = person.javaClass.kotlin //same as person::class

  //returns a lists of KCallables which have the function call(varargs args:Any?)
  kClass.members.forEach { println(it.name) }

  //calling a function with reflection
  fun foo(x: Int) = println(x)

  val kFunction = ::foo
  kFunction.call(42)

  //use KFunction1, KFunction2, and so on to catch errors with arguments at compile time
  //They are synthetic compiler-generated types and don't exist until runtime.
  //Since they are auto-generated you can use them for functions with any number of arguments
  fun sum(x: Int, y: Int) = x + y

  val kFunction2: KFunction2<Int, Int, Int> = ::sum
  println(kFunction2.invoke(1, 2) + kFunction2(3, 4))
  kFunction2(1)

  //reflection with properties
  //returns a KProperty0 interface with setter and get methods
  val kProperty = person::age
  kProperty.setter.call(21)
  println(kProperty.get())

  //a member property is represented by KProperty1 interface and has a
  //one-argument get method. To access the value, you must provide the
  //object instance for which you need the value.
  val memberProperty = PersonBasic::age
  println(memberProperty.get(person))
  //prints 29


}

//Use private to make sure that this extention function is only available in this
//particular context
private fun StringBuilder.serializeObject1(obj: Any) {
  val kClass = obj.javaClass.kotlin
  val properties = kClass.memberProperties
  properties.joinToStringBuilder(this, prefix = "{", postfix = "}")
  {
    prop ->
    serializeString(prop.name)
    append(": ")
    serializePropertyValue(prop.get(obj))
  }
}

//delegates to serializeObject extention function
//buildString creates a stringBuilder and returns its toString()
fun serialize(obj: Any): String = buildString { serializeObject1(obj) }


//this function finds the specified annotation in the list of annotations
//for a KAnnotatedElement
inline fun <reified T> KAnnotatedElement.findAnnotation(): T?
  = annotations.filterIsInstance<T>().firstOrNull()

private fun StringBuilder.serializeObject2(obj: Any) {
  val kClass = obj.javaClass.kotlin

  //creates a list of all properties that do not have the JsonExclude annotation
  val properties = kClass.memberProperties
    .filter { it.findAnnotation<JsonExclude>() == null }



  properties.joinToStringBuilder(this, prefix = "{", postfix = "}")
  {
    prop ->
    //finds the JsonName annotation or null
    val jsonNameAnno = prop.findAnnotation<JsonName>()
    //if the JsonName annotation exists then return name else return prop.name
    val propName = jsonNameAnno?.name ?: prop.name
    serializeString(propName)
    append(": ")
    serializePropertyValue(prop.get(obj))
  }
}

//customSerializer example again but with annotations
fun customSerializer(){
  annotation class CustomSerializer(
    val serializerClass: KClass<out ValueSerializer<*>>
  )

  class DateSerializer: ValueSerializer<Date>{
    override fun fromJsonValue(jsonValue: Any?): Date = Date()
    override fun toJsonValue(value: Date): Any? = null
  }

  data class PersonS(
    val name: String,
    @CustomSerializer(DateSerializer::class) val birthDate: Date
  )

  fun KProperty<*>.getSerializer(): ValueSerializer<Any?>? {

    //Since KProperty extends KAnnotatedElement we can use findAnnotation to find
    //the CustomSerializer annotation and if it doesn't exist then return from the function
    val customSerializerAnno = findAnnotation<CustomSerializer>() ?: return null

    //Gets the serializerClass property from the CustomSerializer annotation
    val serializerClass = customSerializerAnno.serializerClass

    //if the class is an object it gets its instance, if not it creates a new instance of the class
    val valueSerializer = serializerClass.objectInstance ?: serializerClass.createInstance()

    @Suppress("UNCHECKED_CAST")
    return valueSerializer as ValueSerializer<Any?>
  }
}