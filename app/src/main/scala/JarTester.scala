import java.io.File
import java.net.{URI, URL, URLClassLoader}
import java.util.zip.ZipInputStream

import scala.collection.mutable.ArrayBuffer

class JarTester(jar: File) extends Tester {

  override def name: String = jar.getName

  val tests: Seq[Submission => ProblemResult] = {
    // we will build a map of test names and tests
    val tests = new ArrayBuffer[(Int, Submission => ProblemResult)]

    // first, let's find the names of all classes
    val classNames = new ArrayBuffer[String]
    val zip = new ZipInputStream(jar.toURL.openStream())
    var entry = zip.getNextEntry
    while (entry != null) {
      if (!entry.isDirectory && entry.getName.endsWith(".class")) {
        val name = entry.getName.replace('/', '.')
        classNames += name.substring(0, name.length - ".class".length)
      }
      entry = zip.getNextEntry
    }

    // for each class in the jar
    val loader = new URLClassLoader(Array[URL](jar.toURI.toURL))
    for (className <- classNames) {
      val runtimeClass = loader.loadClass(className)

      // match on its nullable problem annotation
      Option(runtimeClass.getAnnotation(classOf[Problem])) match {
        // if the class is a problem
        case Some(problemAnnotation: Problem) =>

          // get the name of the problem and an instance of the problem
          val problemName = problemAnnotation.name
          val problemInstance = runtimeClass.newInstance()

          // function to test the problems
          def test(submission: Submission): ProblemResult = {
            def obscure(res: TestResult): TestResult = res match {
              case Passed(input, output) => Passed("(hidden)", "(hidden)")
              case IncorrectResult(input, output) => IncorrectResult("(hidden)", "(hidden)")
              case TargetException(input, t) => TargetException("(hidden)", t)
              case FileNotFound => FileNotFound
              case ClassLoadFail(t) => ClassLoadFail(t)
              case MethodNotFound => MethodNotFound
            }

            // get all methods annotated to be equality tests
            val eqTestResults: Seq[(Int, TestResult)] =
              runtimeClass.getMethods.toSeq.filter(_.getAnnotation(classOf[EqualityTest]) != null)
                .map(method => {
                  val bean: EqualityBean = method.invoke(problemInstance).asInstanceOf[EqualityBean]
                  (
                    method.getAnnotation(classOf[EqualityTest]).ordinal(),
                    bean.args.toSeq,
                    bean.expected,
                    method.getAnnotation(classOf[Hidden]) != null
                  )
                })
                .map({ case (ord, args, expected, hidden) => {
                  val res = submission.apply(problemName, args, _ == expected)
                  if (hidden) ord -> obscure(res)
                  else ord -> res
                }})

            val acTestResults: Seq[(Int, TestResult)] =
              runtimeClass.getMethods.toSeq.filter(_.getAnnotation(classOf[AcceptanceTest]) != null)
                .map(method => {
                  val bean = method.invoke(problemInstance).asInstanceOf[AcceptanceBean]
                  (
                    method.getAnnotation(classOf[AcceptanceTest]).ordinal(),
                    bean.args.toSeq,
                    bean.test.test: AnyRef => Boolean,
                    method.getAnnotation(classOf[Hidden]) != null
                  )
                })
                .map({ case (ord, args, test, hidden) => {
                  val res = submission.apply(problemName, args, a => test(a.asInstanceOf[AnyRef]))
                  if (hidden) ord -> obscure(res)
                  else ord -> res
                } })

            val testResults: Seq[TestResult] = (acTestResults ++: eqTestResults).sortBy(_._1).map(_._2)

            val problemResult =
              testResults.forall({
                case Passed(_, _) => true
                case _ => false
              })

            ProblemResult(problemName, problemResult, testResults)
          }

          // add the the test sequence
          tests += (problemAnnotation.ordinal() -> test)
        case None =>
      }
    }

    tests.sortBy(_._1).map(_._2)
  }

  println("tests = " + tests)

  override def apply(submission: Submission): Seq[ProblemResult] =
    tests map (_ apply submission)

}
