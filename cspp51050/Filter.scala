package cspp51050
import scala.collection.immutable.Queue

/**
 * DataSource [Y] - Y represents the data type of the 
 * objects coming from the data source.
 * 
 * A DataSource is the input into a pipe and can also
 * be the output from a pipe. 
 */
trait DataSource[Y] {
  var source: Pipe[Y] = _
  def setSink(pipe: Pipe[Y]) = this.source = pipe
  def pull(): Option[Y]
}
/**
 * Data Sink [X] - X represents the data stored in the data sink. 
 * The data sink object is the output stream back into a pipe or
 * the file output from a pipe. 
 */
trait DataSink[X] {
  var sink: Pipe[X] = _
  def setSource(pipe: Pipe[X]) = this.sink = pipe
  def full() = this.sink.full()
  def push(x: Option[X])
}

/**
 *  filter trait operates as an abstract interface for the
 *  filter class. Some simple functions are defined while
 *  the apply method is meant to be defined within the class. 
 */
trait Filter[X, Y] extends DataSource[Y] with DataSink[X] {

  def push(x: Option[X]) = {
    source.push(apply(x))
  }
  def pull(): Option[Y] = {
    apply(sink.pull())
  }
  def apply(x: Option[X]): Option[Y]
 
}
/**
 * FileSource -
 * @input file:String = file name/path to read
 * 
 * FileSource reads a file and sends the file contents to 
 * a pipe. 
 */
class FileSource(val file: String) extends DataSource[String] {
  var fileIterator = scala.io.Source.fromFile(file).getLines()
  def read() = {
    scala.io.Source.fromFile(file).getLines().foreach { line => this.push(Option(line)) }
  }
  def pull(): Option[String] = {
    if (fileIterator.hasNext) {
      var line = fileIterator.next()
      println("Pulling " + line + " into the pipe")
      Option(line)
    } else {
      println("Nothing to pull into the pipe")
      None
    }
  }

  def push(x: Option[String]) = {
    if (this.source != null) {
      println("Pushing " + x + " to the pipe")
      source.push(x)
    }
  }

  def flush() = {}
}
/**
 * PipeSink - Class to hold the final data output from a pipe
 */
class PipeSink() extends DataSink[String] {
  protected var queue: Queue[String] = Queue.empty[String]
  def pull(): Option[String] = {
    if (!queue.isEmpty) {
      Option(this.queue.dequeue._1)
    } else {
      this.sink.pull()
    }
  }
  def push(x: Option[String]) = {
    if (this.queue != null) {
      println("Enqueing pipe output: " + x)
      queue.enqueue(x)
    }
  }
  def flush() = {}
}
/**
 * CapsFilter - Filter to output capitalized Strings into a pipe. 
 */
class CapsFilter() extends Filter[String, String] {
  def apply(x: Option[String]): Option[String] = x match {
    case Some(s) => Option(s.toUpperCase)
    case None => None
  }
}