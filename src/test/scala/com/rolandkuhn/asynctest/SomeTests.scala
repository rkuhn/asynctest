package com.rolandkuhn.asynctest

import org.junit.runner.RunWith
import org.junit.Test
import scala.async.Async._
import scala.concurrent.Promise

@RunWith(classOf[Runner])
class SomeTests {

  import scala.concurrent.ExecutionContext.Implicits.global

  private def op = async { Thread.sleep(300); 42 }
  private def flop = async { assert(false, "this is FALSE") }
  private def top = Promise[String]().future

  @Test def one = async {
    val x = await(op)
    val y = await(op).toString
    val z = await(op)
    val w = x + z
    y + w
  }

  @Test def two = async {
    val x = await(op)
    val y = await(flop).toString
    val z = await(op)
    val w = x + z
    y + w
  }

  @Test def three = async {
    val x = await(op)
    val y = await(op).toString
    val z = await(op)
    val w = x + z
    y + w
    1 / 0
  }

  @Test def four = async {
    val x = await(op)
    val y = await(op).toString
    val z = await(op)
    val w = x + z
    y + w
  }

  @Test def five = async {
    val x = await(op)
    val y = await(op).toString
    val z = await(op)
    val w = x + z
    y + w
  }

  @Test def six = async {
    val x = await(op)
    val y = await(op).toString
    val z = await(top)
    val w = x + z
    y + w
  }

}