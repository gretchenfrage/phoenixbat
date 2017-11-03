import java.net.{URI, URL, URLClassLoader}
import java.util.zip.ZipInputStream

import scala.collection.mutable.ArrayBuffer

class JarTester(jar: URI) extends Tester {


  override def name: String = {
    val url = jar.toString
    val slashIndex = url.lastIndexOf('/')
    val dotIndex = url.lastIndexOf('.', slashIndex)
    val name = if (dotIndex == -1) url.substring(slashIndex + 1)
          else url.substring(slashIndex + 1, dotIndex)
    println("jartester name = " + name)
    name
  }

  val tests: Map[String, Submission => String] = {
    // we will build a map of test names and tests
    val tests = new ArrayBuffer[(String, Submission => String)]

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
          val test: Submission => String =
            // get all methods annotated to be equality tests
            runtimeClass.getMethods.toSeq.filter(_.getAnnotation(classOf[EqualityTest]) != null)
            // invoke the methods to get the arguments and expected result
            .map(method => {
              val bean = method.invoke(problemInstance).asInstanceOf[EqualityBean]
              bean.args -> bean.expected
            })
            // map these tuples into Submission => Boolean functions that return whether the tests pass
            .map({ case (args, expected) => (submission: Submission) => {
              submission(problemName) match {
                case Some(submissionFunc) => submissionFunc(args.toSeq) == expected
                case None => false
              }}})
            // reduce these functions into one function that checks if they all pass
            .fold((_: Submission) => true)((func1, func2) => (sub: Submission) => func1(sub) && func2(sub))
            // map the boolean into a string
            .andThen({
              case true => "<span class=\"passed\">Passed</span>"
              case false => "<span class=\"failed\">Failed</span>"
            })

          // add the the test sequence
          tests += problemName -> test
        case None =>
      }
    }

    // convert the tests sequence into a map
    tests.toMap
  }

  println("tests = " + tests)

  override def apply(submission: Submission): Map[String, String] =
    tests map { case (name, func) => (name, func(submission)) }

}
