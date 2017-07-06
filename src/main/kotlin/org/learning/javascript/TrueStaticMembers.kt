package org.learning.javascript

/**
 * Created by gbaldeck on 7/6/2017.
 */
inline fun <reified T : Any> addStaticMembersTo(source: Any) {
  val c = T::class.js.asDynamic()
  val ownNames = js("Object").getOwnPropertyNames(source) as Array<String>
  val protoNames = js("Object").getOwnPropertyNames(source.asDynamic().constructor.prototype) as Array<String>

  for (name in ownNames + protoNames) {
    c[name] = source.asDynamic()[name]
  }
}

class A {
  companion object {
    init {
      addStaticMembersTo<A>(object {
        val bar = 1
        fun foo() {}
      })
    }
  }
}

class B {
  companion object {
    val bar = 1
    fun foo() {}

    // should be at the end of companion object
    init {
      addStaticMembersTo<B>(this)
    }
  }
}

fun main(args: Array<String>) {
  A()
  println(js("Object").getOwnPropertyNames(A::class.js))
  println(js("Object").getOwnPropertyNames(B::class.js))
  B.Companion
  println(js("Object").getOwnPropertyNames(B::class.js))
}