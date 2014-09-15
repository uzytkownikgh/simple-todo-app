package infrastructure

import java.util.concurrent.atomic.AtomicLong

class IdGenerator(start: Long) {
  val idSeq = new AtomicLong(start)

  def next() = idSeq.incrementAndGet()
}
