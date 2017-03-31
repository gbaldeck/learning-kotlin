package org.learning

import kotlin.reflect.KClass

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
@Test(timeout = TEST_TIMEOUT) fun testMethod() {  }

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