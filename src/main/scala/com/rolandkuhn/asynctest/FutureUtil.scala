package com.rolandkuhn.asynctest

import java.util.Timer
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.Promise
import java.util.TimerTask
import java.util.concurrent.TimeoutException

object FutureUtil {
  private val timer = new Timer(true)

  implicit class WithTimeout[T](val f: Future[T]) extends AnyVal {
    def withTimeout(d: FiniteDuration): Future[T] = {
      val p = Promise[T]()
      val t = new TimerTask {
        override def run: Unit = p.tryFailure(new TimeoutException(s"Future with timeout $d timed out"))
      }
      timer.schedule(t, d.toMillis)
      p.completeWith(f)
      p.future
    }
  }

}