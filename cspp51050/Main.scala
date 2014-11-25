package cspp51050
/*
 * Main function to start the application
 */
object Main {
  def main(args: Array[String]): Unit = {
    TestPushFeature
    TestPullFeature
  }
}

/*
 * This tests pushing data through the pipe
 */
object TestPushFeature {
  var source = new FileSource("hw2_data.csv")
  var caps = new CapsFilter()
  var sink = new PipeSink()
  var pipe = new Pipe(source, caps)
  var pipe2 = new Pipe(caps, sink)
  source.read()
}
/*
 * This tests pulling data from the pipe
 */
object TestPullFeature {
  var source = new FileSource("hw2_data.csv")
  var caps = new CapsFilter()
  var sink = new PipeSink()
  var pipe = new Pipe(source, caps)
  var pipe2 = new Pipe(caps, sink)

  var line = sink.pull()
  while (line != None) {
    line = sink.pull()
    println("Pulling " + line + " from the data sink")
  }
}