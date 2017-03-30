package org.learning

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