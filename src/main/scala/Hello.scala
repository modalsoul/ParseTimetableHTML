package scalamin

import java.io.StringReader
import scala.xml.Text
import scala.xml.Node
import scala.xml.parsing.NoBindingFactoryAdapter
import scala.collection.mutable.ArrayBuffer
import collection.mutable.{ArrayBuffer, ListBuffer}
import nu.validator.htmlparser.sax.HtmlParser
import nu.validator.htmlparser.common.XmlViolationPolicy
import org.xml.sax.InputSource
import org.apache.commons.io.FileUtils
import java.io.File
import io.Source


object Hello {
//  val hoge = new MappingRoute
//  hoge.testSqlite

  val hoge = new Route
  hoge.getBusStopIDList("1020021011")

  var routeName = ""

  def main(args:Array[String]) = {
    /*
//    val src = Source.fromURL("http://timetablenavi.keikyu-bus.co.jp/dia/timetable/web/any/1020021011/", "Shift_JIS")
    val src = Source.fromURL("http://timetablenavi.keikyu-bus.co.jp/dia/timetable/web/any/1020021012/", "Shift_JIS")

    val str = src.mkString

    val host = "http://timetablenavi.keikyu-bus.co.jp"

    val nodeList = toNode(str)

    this.routeName = ((nodeList \\ "h3").text.replaceAll(" ", "").split("\n"))(1).split("「")(1).split("」")(0)

    val busStopNameList =  (nodeList \\ "table").filter(_ \ "@class" contains Text("stoplist")) \\ "th"

    val busStopNodeList = (nodeList \\ "a").filter(_ \ "@class" contains Text("post"))

    val timeTableURL = new ArrayBuffer[String]

    busStopNodeList.foreach { bus =>
      timeTableURL += host + (bus \ "@href").text
    }

    for(urlNum <- 0 to timeTableURL.size-1) {
      parseTimetable((urlNum+1) + busStopNameList(urlNum).text, timeTableURL(urlNum))
    }
    */
    print("hello")
  }
  def parseTimetable(busStopName:String, url:String) = {
    //    val str = FileUtils.readFileToString(new File("sample.xml"))
    val src = Source.fromURL(url, "Shift_JIS")

    val str = src.mkString

    val tableNode = (toNode(str) \\ "table").filter(_ \ "@class" contains Text("timetable"))

    val weekTypeNum = (tableNode \\ "tr" \\ "th").filter(_ \ "@class" contains Text("week")).size

    val trNodeList = (tableNode \\ "tr").filter(_ \\ "th" \ "@class" contains Text("hour"))

    val timeList = new ArrayBuffer[ArrayBuffer[String]]()

    trNodeList.foreach { rowNode =>
      val tdNodeList = rowNode \\ "td"

      for (week <- 0 to weekTypeNum-1) {
        timeList += ArrayBuffer[String]()
        val times = tdNodeList(week).text.replaceAll(" ", "").split("\n").filter(_ != "")
        for (num <- 0 to times.size-1) {
          timeList(week) += (rowNode \ "th").filter(_ \ "@class" contains Text("hour")).text + ":" +times(num)
        }
      }
    }
    for( week <- 0 to weekTypeNum-1) {
      writeToFile(busStopName , week, timeList(week))
    }
    println("Hello Scala!")
  }

  def toNode(str: String): Node = {
    val hp = new HtmlParser
    hp.setNamePolicy(XmlViolationPolicy.ALLOW)

    val saxer = new NoBindingFactoryAdapter
    hp.setContentHandler(saxer)
    hp.parse(new InputSource(new StringReader(str)))

    saxer.rootElem
  }

  def writeToFile(name:String, week:Int, list:ArrayBuffer[String]) = {
    val destination = "./output/"
    val newFile = new File(destination + this.routeName)
    newFile.mkdir()

    import java.io.{ FileOutputStream=>FileStream, OutputStreamWriter=>StreamWriter }

    val encode = "UTF-8"
    val append = true

    val fileOutPutStream = new FileStream(destination + this.routeName + "/" + name, append)
    val writer = new StreamWriter( fileOutPutStream, encode )

    list.foreach { time =>
      writer.write(week + "," + time + "\n")
    }
    writer.close()
  }
}
