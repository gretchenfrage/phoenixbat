import java.lang.reflect.InvocationTargetException
import java.net.{URL, URLClassLoader}
import java.nio.file.Path
import javax.tools.ToolProvider

class CompiledDirSubmission(path: Path) extends Submission {

  override def apply(test: String, args: Seq[Any], acceptance: Any => Boolean): TestResult = {
    val argString = new StringBuilder
    argString.append("[")
    for ((arg, i) <- args.zipWithIndex) {
      argString.append(arg)
      if (i < args.size - 1)
        argString.append(", ")
    }
    argString.append("]")

    if (path.resolve(test + ".class").toFile.exists()) {
      val loader = new URLClassLoader(Array[URL](path.toFile.toURI.toURL))
      try {
        val clazz = loader.loadClass(test)
        clazz.getMethods.find(_.getName == "apply") match {
          case Some(method) =>
            try {
              val result = method.invoke(null, args.map(_.asInstanceOf[AnyRef]): _*)

              if (acceptance(result)) Passed(argString.toString(), result.toString)
              else IncorrectResult(argString.toString(), result.toString)
            } catch {
              case ite: InvocationTargetException => TargetException(argString.toString, ite.getTargetException)
            }
          case None => MethodNotFound
        }
      } catch {
        case loadFail: Throwable => ClassLoadFail(loadFail)
      }
    } else FileNotFound
  }
}
