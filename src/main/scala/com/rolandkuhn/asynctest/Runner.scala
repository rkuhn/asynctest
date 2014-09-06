package com.rolandkuhn.asynctest

import java.lang.reflect.Method
import org.junit.Test
import org.junit.runner.Description
import org.junit.runner.notification.RunNotifier
import scala.concurrent.Future
import java.util.concurrent.LinkedBlockingQueue
import scala.util.Success
import scala.util.Failure
import org.junit.runner.notification
import scala.concurrent.duration._
import scala.annotation.tailrec
import java.util.concurrent.ExecutionException

class Runner(clazz: Class[_]) extends org.junit.runner.Runner {

  import concurrent.ExecutionContext.Implicits.global
  import FutureUtil._

  val tests: Map[Method, Description] =
    clazz.getMethods().filter(isTest)
      .map(m => m -> Description.createTestDescription(clazz, m.getName()))(collection.breakOut)

  private def isTest(m: Method): Boolean = m.getAnnotation(classOf[Test]) != null
  private val cF = classOf[Future[_]]
  private def isFuture(c: Class[_]): Boolean = cF isAssignableFrom c

  override val getDescription: Description = {
    val d = Description.createSuiteDescription(clazz)
    tests foreach (t => d.addChild(t._2))
    d
  }

  override def run(_notifier: RunNotifier): Unit = {
    type Notifier = Either[RunNotifier => Unit, Unit]
    val q = new LinkedBlockingQueue[Notifier]
    def notify(thunk: RunNotifier => Unit): Unit = q.offer(Left(thunk))

    val obj = clazz.newInstance()
    val futures = for ((m, d) <- tests) yield {
      notify(_.fireTestStarted(d))
      val f = if (isFuture(m.getReturnType())) {
        Future(m.invoke(obj).asInstanceOf[Future[_]]).flatMap(identity)
      } else {
        Future(m.invoke(obj))
      }
      f.withTimeout(1.5.seconds).recover {
        case ex: ExecutionException => notify(_.fireTestFailure(new notification.Failure(d, ex.getCause)))
        case ex                     => notify(_.fireTestFailure(new notification.Failure(d, ex)))
      }.andThen {
        case _ => notify(_.fireTestFinished(d))
      }
    }
    Future.sequence(futures).foreach(_ => q.offer(Right()))

    @tailrec def pull(): Unit = q.take() match {
      case Left(thunk) =>
        thunk(_notifier)
        pull()
      case Right(_) => // finished
    }
    pull()
  }

}