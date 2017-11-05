import java.awt._
import java.io._
import java.net.{URI, URISyntaxException, URL}
import java.nio.file.{Path, Paths}
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.concurrent.Worker
import javafx.concurrent.Worker.State
import javafx.embed.swing.JFXPanel
import javafx.event.EventTarget
import javax.swing.GroupLayout.Alignment
import javax.swing._
import javafx.scene.web.WebEngine
import javafx.scene.Scene
import javafx.scene.web.WebView
import javafx.scene.text.Font

import netscape.javascript.JSObject

import scala.collection.mutable.ArrayBuffer

class PhoenixBat {

  val frame = new JFrame("PhoenixBat")
  val panel = new JFXPanel
  var stateListener: Worker.State => Unit = (_: Worker.State) => ()
  val engine: WebEngine = {
    val queue = new java.util.concurrent.LinkedBlockingQueue[WebEngine]
    Platform.runLater(() => {
      val view = new WebView
      panel.setScene(new Scene(view))
      queue.add(view.getEngine)
      view.getEngine.getLoadWorker.stateProperty().addListener((_, _, s) => stateListener(s))
      println("engine initialized")
    })
    queue.take()
  }
  engine.setOnError(error => {
    System.err.println(error)
  })
  frame.setContentPane(panel)
  frame.setSize(800, 700)
  frame.setLocationRelativeTo(null)
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  frame.setVisible(true)
  val dontGC = new ArrayBuffer[AnyRef]



  val appdir = AppDirs.dataDir("phoenixbat")
  appdir.toFile.mkdirs()
  println("appdir = " + appdir)
  val properties = PersistentStringMap(appdir.resolve("settings.properties"))
  enterPage()



  def loadPage(url: String): Unit = {
    val ris = classOf[PhoenixBat].getResourceAsStream(url)
    val br = new BufferedReader(new InputStreamReader(ris))
    val builder = new StringBuilder
    try {
      var line = br.readLine()
      while (line != null) {
        builder.append(line)
        builder.append('\n')
        line = br.readLine()
      }
    } catch {
      case e: IOException =>
    }
    val html = builder.toString()

    Platform.runLater(() => {
      engine.loadContent(html)

      frame.revalidate()
      frame.repaint()
    })
  }

  def enterPage(): Unit = Platform.runLater(() => {
    dontGC.clear()
    stateListener = neu => {
      if (neu == Worker.State.SUCCEEDED) {
        val app = new Object {
          def defaultProblemSuite(): String = Option(properties.get("suite")).getOrElse("")

          def defaultWorkingDir(): String = Option(properties.get("workspace")).getOrElse("")

          def selectProblemSuite(): String = {
            val chooser = new JFileChooser()
            chooser.showOpenDialog(frame) match {
              case JFileChooser.APPROVE_OPTION =>
                val result = chooser.getSelectedFile.toString
                properties.put("suite", result)
                result
              case _ => ""
            }
          }

          def selectWorkingDir(): String = {
            val chooser = new JFileChooser()
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
            chooser.setAcceptAllFileFilterUsed(false)
            chooser.showOpenDialog(frame) match {
              case JFileChooser.APPROVE_OPTION =>
                val result = chooser.getSelectedFile.toString
                properties.put("workspace", result)
                result
              case _ => ""
            }
          }

          def enter(suite: String, workspace: String): Unit = {
            println("entering " + (suite, workspace))
            /*
            val suiteURI = {
              val file = new File(suite)
              if (file.exists()) Some(file.toURI)
              else try Some(new URL(suite).toURI)
              catch {
                case e: URISyntaxException => None
              }
            }
            */
            val suiteJar = {
              val file = new File(suite)
              if (file.exists()) Some(file)
              else try Some(Downloader.download(file.toURI.toURL, "suite"))
              catch {
                case e: Exception =>
                  System.err.println("exception in downloading suite jar")
                  e.printStackTrace()
                  None
              }
            }
            val workspacePath = {
              val file = new File(workspace)
              if (file.exists()) Some(file.toPath)
              else None
            }
            (suiteJar, workspacePath) match {
              case (Some(s), Some(w)) => enterApp(s, w)
              case _ => println("failed")
            }
          }
        }
        dontGC += app
        engine.executeScript("window").asInstanceOf[JSObject].setMember("app", app)
        engine.executeScript("onload()")
      }
    }
    loadPage("enter.html")
  })

  def enterApp(suite: File, workspace: Path): Unit = Platform.runLater(() => {
    val tester = new JarTester(suite)
    println("tests = " + tester)
    val submission = new CompiledDirSubmission(workspace)
    println("submission = " + submission)
    dontGC.clear()

    stateListener = neu => {
      if (neu == Worker.State.SUCCEEDED) {
        val app = new Object {
          def suiteName(): String = tester.name

          def print(str: String): Unit = {
            println(str)
          }

          def exit(): Unit = {
            println("exiting")
            enterPage()
          }

          def test(): Unit = {
            try {
              def escapify(str: String): String = str.replace("\"", "\\\"")

              engine.executeScript("clearresults()")

              for (result <- tester(submission)) {
                def addProblemResult(name: String, text: String, color: String): Unit =
                  engine.executeScript("addresult('" + name + "', '" + text + "', '" + color + "')")

                if (result.passed) addProblemResult(result.name, "Success", "green")
                else addProblemResult(result.name, "Fail", "red")

                def addTestResult(input: String, output: String, status: String): Unit =
                  engine.executeScript("addtestresult('" + input + "', '" + output + "', '" + status + "')")
                result.tests.foreach {
                  case Passed(in, out) => addTestResult(in, out, "correct")
                  case IncorrectResult(in, out) => addTestResult(in, out, "incorrect")
                  case TargetException(in, t) => addTestResult(in, t.toString, "exception")
                  case FileNotFound => addTestResult("N/A", "N/A", "file not found")
                  case MethodNotFound => addTestResult("N/A", "N/A", "method not found")
                  case ClassLoadFail(t) => addTestResult("N/A", t.toString, "class load fail")
                }
              }

            } catch {
              case e: Exception => e.printStackTrace()
            }
          }
        }
        dontGC += app
        engine.executeScript("window").asInstanceOf[JSObject].setMember("app", app)
        engine.executeScript("onload()")
      }
    }
    loadPage("main.html")
  })

}

object PhoenixBat extends App {
  new PhoenixBat
}