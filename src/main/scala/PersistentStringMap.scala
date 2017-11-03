import java.io.{File, PrintStream}
import java.nio.file.Path
import java.util.Scanner

import scala.collection.{JavaConverters, mutable}

class PersistentStringMap private[this](file: File) extends java.util.HashMap[String, String] {

  def this(path: Path) = {
    this(path.toFile)
    if (!file.exists) file.createNewFile()
    val scanner = new Scanner(file)
    while (scanner.hasNextLine) {
      val line = scanner.nextLine().split("\\=")
      put(line(0).trim, line(1).trim)
    }
  }

  private def save(): Unit = {
    val out = new PrintStream(file)
    for (pair <- JavaConverters.collectionAsScalaIterable(this.entrySet())) {
      val (k, v) = (pair.getKey, pair.getValue)
      out.println(k + " = " + v)
    }
    out.close()
  }

  override def clear(): Unit = this.synchronized {
    try super.clear()
    finally save()
  }

  override def put(key: String, value: String): String = this.synchronized  {
    try super.put(key, value)
    finally save()
  }

  override def remove(key: scala.Any): String = this.synchronized  {
    try super.remove(key)
    finally save()
  }
}

object PersistentStringMap {
  private val mutex = new Object
  private val map = new mutable.WeakHashMap[Path, PersistentStringMap]

  def apply(path: Path): java.util.Map[String, String] = mutex.synchronized {
    map.get(path) match {
      case Some(m) => m
      case None =>
        val m = new PersistentStringMap(path)
        map.put(path, m)
        m
    }
  }
}