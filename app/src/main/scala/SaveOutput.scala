import java.io.{FileOutputStream, OutputStream, PrintStream}
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object SaveOutput {

  def activate(path: Path): Unit = {
    path.toFile.mkdirs()
    val name = DateTimeFormatter.ofPattern("yyy.MM.dd.HH.mm.ss").format(LocalDateTime.now())
    val file = path.resolve(name).toFile
    val fout = new FileOutputStream(file)

    val oldStdOut = System.out
    val oldStdErr = System.err

    System.setOut(new PrintStream(new OutputStream {
      override def write(b: Int): Unit = {
        fout.write(b)
        fout.flush()
        oldStdOut.write(b)
      }
    }))
    System.setErr(new PrintStream(new OutputStream {
      override def write(b: Int): Unit = {
        fout.write(b)
        fout.flush()
        oldStdOut.write(b)
      }
    }))

  }

}
