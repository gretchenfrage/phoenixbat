trait Tester {

  //def apply(submission: Submission): Map[String, String]
  def apply(submission: Submission): Seq[ProblemResult]

  def name: String

}

case class ProblemResult(name: String, result: String, tests: Seq[TestResult])
sealed trait TestResult
case class Passed(input: String, output: String) extends TestResult
case class IncorrectResult(input: String, output: String) extends TestResult
case class TargetException(input: String, t: Throwable) extends TestResult
case object FileNotFound extends TestResult
case class ClassLoadFail(t: Throwable) extends TestResult
case object MethodNotFound extends TestResult