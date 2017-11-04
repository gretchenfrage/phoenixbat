import java.net.{URI, URL, URLClassLoader}
import java.util.zip.ZipInputStream

import scala.collection.mutable.ArrayBuffer

class JarTester(jar: URI) extends Tester {



  /*
  override def name: String = {
    val url = jar.toString
    val slashIndex = url.lastIndexOf('/')
    val dotIndex = url.lastIndexOf('.', slashIndex)
    val name = if (dotIndex == -1) url.substring(slashIndex + 1)
          else url.substring(slashIndex + 1, dotIndex)
    println("jartester name = " + name)
    name
  }
  */

  override def name: String = jar.toString

  val tests: Seq[Submission => ProblemResult] = {
    // we will build a map of test names and tests
    val tests = new ArrayBuffer[Submission => ProblemResult]

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

    class Loader(uri: URI) extends URLClassLoader(Array[URL](uri.toURL)) {
      override def findClass(name: String): Class[_] = super.findClass(name)
    }

    // for each class in the jar
    val loader = new Loader(jar)
    for (className <- classNames) {
      val runtimeClass = loader.findClass(className)

      // match on its nullable problem annotation
      Option(runtimeClass.getAnnotation(classOf[Problem])) match {
        // if the class is a problem
        case Some(problemAnnotation) =>

          // get the name of the problem and an instance of the problem
          val problemName = problemAnnotation.name
          val problemInstance = runtimeClass.newInstance()

          // get the sequence of equality tests
          def test(submission: Submission): ProblemResult = {
            // get all methods annotated to be equality tests
            val testResults: Seq[TestResult] =
              runtimeClass.getMethods.toSeq.filter(_.getAnnotation(classOf[EqualityTest]) != null)
              // invoke the methods to get the arguments and expected result
              .map(method => {
              val bean: EqualityBean = method.invoke(problemInstance).asInstanceOf[EqualityBean]
              bean.args.toSeq -> bean.expected
            })
              .map({ case (args, expected) => {
                submission.apply(problemName, args, expected)
              }})
            val problemResult =
              testResults.forall({
                case Passed(_, _) => true
                case _ => false
              })
            ProblemResult(problemName, problemResult, testResults)
          }

          // add the the test sequence
          tests += test
        case None =>
      }
    }

    tests
  }

  println("tests = " + tests)

  override def apply(submission: Submission): Seq[ProblemResult] =
    tests map (_ apply submission)

}
