package scalamin

import collection.mutable.ArrayBuffer
import io.Source
import java.io.{File, StringReader}
import nu.validator.htmlparser.common.XmlViolationPolicy
import nu.validator.htmlparser.sax.HtmlParser
import org.xml.sax.InputSource
import xml.parsing.NoBindingFactoryAdapter
import xml.{Node, Text}

/**
 * Created with IntelliJ IDEA.
 * User: M
 * Date: 13/03/08
 * Time: 21:40
 * To change this template use File | Settings | File Templates.
 */
class TimeTable {
  var routeName = ""
  val urlPrefix = "http://timetablenavi.keikyu-bus.co.jp/dia/timetable/web/"

  def getTimetable(routeWeb:String, busStopWeb:String) = {
    println(routeWeb + "/" + busStopWeb)
    val routeDao = new RouteDao
    val routeId = routeDao.queryRouteByWebId(routeWeb)._1
    val busStopDao = new BusStopDao
    val busStopId = busStopDao.queryBusStopIdByWebId(busStopWeb)
    val busStopWebUrl = busStopWeb.split("/")

    val timeTableUrl = urlPrefix + busStopWebUrl(0) + "/" + routeWeb + "/" + busStopWebUrl(1) + "/"

    val src  = Source.fromURL(timeTableUrl, "Shift-JIS")
    val str = src.mkString

    val tableNode = (toNode(str) \\ "table").filter(_ \ "@class" contains Text("timetable"))

    val weekTypeNum = (tableNode \\ "tr" \\ "th").filter(_ \ "@class" contains Text("week")).size
    println(weekTypeNum)

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
    val insertTimeList = new ArrayBuffer[(Int, Int, Int, String)]
    for( week <- 0 to weekTypeNum-1) {
      //      writeToFile(busStopName , week, timeList(week))
      timeList(week).foreach { time =>
        val typeTime = (busStopId, routeId, week, time)
        insertTimeList += typeTime
      }
    }
    val timeTableDao = new TimeTableDao

    timeTableDao.insertTimeTable(insertTimeList)

  }
  def toNode(str: String): Node = {
    val hp = new HtmlParser
    hp.setNamePolicy(XmlViolationPolicy.ALLOW)

    val saxer = new NoBindingFactoryAdapter
    hp.setContentHandler(saxer)
    hp.parse(new InputSource(new StringReader(str)))

    saxer.rootElem
  }
  /*
  def parseTimetable(busStopName:String, url:String):ArrayBuffer[(Int, String)] = {
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
    val typeTimeList = new ArrayBuffer[(Int, String)]
    for( week <- 0 to weekTypeNum-1) {
//      writeToFile(busStopName , week, timeList(week))
      timeList(week).foreach { time =>
        val typeTime = (week, time)
        typeTimeList += typeTime

      }
    }
    typeTimeList
  }
  def oldtimetable = {
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

    val db = new DBAccess
    val route_id = db.queryRouteByWebId(routeWeb)
    val busStop_id = db.queryBusStopIdByWebId(busStopWeb)
    for(urlNum <- 0 to timeTableURL.size-1) {
      val timeTableList = new ArrayBuffer[(Int, Int, Int, String)]
      parseTimetable((urlNum+1) + busStopNameList(urlNum).text, timeTableURL(urlNum)).foreach { typeTime =>
      val row = (route_id, busStop_id, typeTime._1, typeTime._2)

      }
    }
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
  }            */
}
