package org.learning

/**
 * Created by gbaldeck on 2/7/2017.
 */

fun main(args: Array<String>) {
  println(Color.BLUE.rgb());
  println(getMnemonic(Color.BLUE))
  println(getWarmth(Color.ORANGE))

  println(eval(Sum(Num(2), Sum(Num(5), Num(7)))))

  for (i in 1..100) {
    print(fizzBuzz(i))
  }
  println("");
  for(i in 100 downTo 1 step 2){
    print(fizzBuzz(i))
  }
  println("");
  binaryReps();
}