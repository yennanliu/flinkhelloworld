package examples

// https://github.com/apache/flink/blob/master/flink-examples/flink-examples-streaming/src/main/scala/org/apache/flink/streaming/scala/examples/join/WindowJoin.scala

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time

import scala.collection.JavaConverters._

object WindowJoin extends App {

  case class Grade(name: String, grade: Int)

  case class Salary(name: String, salary: Int)

  case class Person(name: String, grade: Int, salary: Int)

  val params = ParameterTool.fromArgs(args)
  val windowSize = params.getLong("windowSize", 2000)
  val rate = params.getLong("rate", 3)

  println("Using windowSize=" + windowSize + ", data rate=" + rate)
  println("To customize example, use: WindowJoin " +
    "[--windowSize <window-size-in-millis>] [--rate <elements-per-second>]")

  // obtain execution environment, run this example in "ingestion time"
  val env = StreamExecutionEnvironment.getExecutionEnvironment
  env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime)

  // make parameters available in the web interface
  env.getConfig.setGlobalJobParameters(params)

  // create the data sources for both grades and salaries
  val grades = WindowJoinSampleData.getGradeSource(env, rate)
  val salaries = WindowJoinSampleData.getSalarySource(env, rate)

  // join the two input streams by name on a window.
  // for testability, this functionality is in a separate method.
  val joined = joinStreams(grades, salaries, windowSize)

  // print the results with a single thread, rather than in parallel
  joined.print().setParallelism(1)

  // execute program
  env.execute("Windowed Join Example")

  // let's define a helper method
  def joinStreams(
                   grades: DataStream[Grade],
                   salaries: DataStream[Salary],
                   windowSize: Long): DataStream[Person] = {

    grades.join(salaries)
      .where(_.name)
      .equalTo(_.name)
      .window(TumblingEventTimeWindows.of(Time.milliseconds(windowSize)))
      .apply { (g, s) => Person(g.name, g.grade, s.salary) }
  }

}
