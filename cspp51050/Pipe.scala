package cspp51050
import scala.collection.immutable.Queue

/**
 * Pipe[X] - X is the data type of the input/output of the pipe
 * @source - the input source for the pipe
 * @sink - where the pipe outputs the data received. 
 * 
 * One thing to note: the pipe is built to either have
 * items pulled or pushed so the flow of 
 * data can be controlled on either side of the pipe. A
 * process at the beginning of the pipe/filter sequence
 * can push data if needed or a process at the end can
 * pull data from the pipe. The pipe/filter interfaces
 * wrap the data structures sent through the pipe using 
 * Scala Option[_]. If an element exists then the pipe/filter
 * will output "Some(_)" if an object doesn't have any elements
 * to output then it will send a "None" object. 
 *
 */
class Pipe[X](val source: DataSource[X], val sink: DataSink[X]) {
  source.setSink(this) // Set the pipe for the source
  sink.setSource(this) // Set the pipe for the sink
  protected var queue: Queue[X] = Queue.empty[X]

  /*
   * Push a value Option[X] or enqueue if sink is full
   */
  def push(x: Option[X]): Unit = {
    if (this.sink.full()) {
      queue.enqueue(x)
    } else {
      sink.push(x)
    }
  }
  /*
   * Return true is the pipe is "full"
   */
  def full(): Boolean = this.queue.size >= 1000
  /*
   * pull object from the queue if an element is present
   * otherwise try pulling from the source. 
   * 
   * If the source has nothing than send a None object accross 
   * the pipe.
   */
  def pull(): Option[X] = {
    if (!queue.isEmpty) {
      Option(queue.dequeue._1)
    } else {
      var x = source.pull()
      x match {
        case Some(z) => Option(z)
        case _ => None
      }
    }
  }
  /*
   * flush the pipe
   */
  def flush() = {
    while (!queue.isEmpty) {
      sink.push(Option(queue.dequeue._1));
    }
  }
}
