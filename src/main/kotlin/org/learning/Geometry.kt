package org.learning

/**
 * Created by gramb on 2/10/2017.
 */
import java.util.Random

class Rectangle(height: Int, val width: Int) {
    val isSquare: Boolean
        get() = height == width

    private val height: Int = height
        get() = field + 2 //using a backing field

    fun whatsTheHeight() : Int = height;

}
fun createRandomRectangle(): Rectangle {
    val random = Random()
    return Rectangle(random.nextInt(), random.nextInt())
}