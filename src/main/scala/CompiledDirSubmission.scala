import java.net.{URL, URLClassLoader}
import java.nio.file.Path
import javax.tools.ToolProvider

class CompiledDirSubmission(path: Path) extends Submission {
  override def apply(test: String): Option[Seq[Any] => Any] = {
    val loader = new URLClassLoader(Array[URL](path/*.resolve(test + ".class")*/.toFile.toURI.toURL))
    try {
      Option(loader.loadClass(test))
        .flatMap(_.getMethods.find(_.getName == "apply"))
        .map(method => (args: Seq[Any]) => method.invoke(null, args.map(_.asInstanceOf[AnyRef]): _*))
    } catch {
      case e: Exception =>
        println("class file not found: " + test + ".class")
        None
    }
  }
}
