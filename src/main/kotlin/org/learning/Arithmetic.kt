package org.learning

/**
 * Created by gramb on 2/10/2017.
 */

interface Expr
class Num(val value: Int) : Expr
class Sum(val left: Expr, val right: Expr) : Expr