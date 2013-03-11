package scalamin

import io.Source
import java.io.StringReader
import nu.validator.htmlparser.common.XmlViolationPolicy
import nu.validator.htmlparser.sax.HtmlParser
import org.xml.sax.InputSource
import xml.parsing.NoBindingFactoryAdapter
import xml.{NodeSeq, Node, Text}
import collection.mutable.ArrayBuffer

/**
 * Created with IntelliJ IDEA.
 * User: M
 * Date: 13/03/09
 * Time: 12:12
 * To change this template use File | Settings | File Templates.
 */
class BusStop(routeId:String) {
  val urlPostfix = "http://timetablenavi.keikyu-bus.co.jp/dia0315/timetable/web/any/"

  def getBusStopList = {
    val src = Source.fromURL(urlPostfix + routeId + "/", "Shift_JIS")
    val str = src.mkString

    val nodeList = toNode(str)
    val busStopIdNodeList = (nodeList \\ "a").filter(_ \ "@class" contains Text("post"))
    val busStopNameNodeList = (nodeList \\ "table").filter(_ \ "@class" contains Text("stoplist")) \\ "th"

    val busStopIdList = getBusStopIdList(busStopIdNodeList)
    val busStopNameList = getBUsStopNameList(busStopNameNodeList)

    val busStopIdNameList = new ArrayBuffer[(String, String)]
    for (num <- 0 to busStopIdList.size-1) {
      val idName = (busStopIdList(num), busStopNameList(num))
      busStopIdNameList += idName
    }

    val busStopDao:BusStopDao = new BusStopDao
    var insertBusStopList = new ArrayBuffer[(String, String)]
    busStopIdNameList.foreach { busStop =>
      if( busStopDao.queryBusStopWebIdExistance(busStop._1) == 0) {
        insertBusStopList += busStop
      }
    }
    busStopDao.insertBusStop(insertBusStopList)

    var busStops:String = ""
    busStopIdList.foreach { web_id =>
      val busStopWebId = busStopDao.queryBusStopIdByWebId(web_id)
      busStops += busStopWebId + ","

      val time = new TimeTable
      time.getTimetable(routeId, web_id)
    }
    busStops = busStops.init
    val values = (routeId, busStopNameList.head, busStopNameList.last, busStops)
    val routeDb = new RouteDao
    routeDb.updateRoute(values)

  }
  def getBusStopIdList(nodeList:NodeSeq):ArrayBuffer[String] = {
    val busStopIdList = new ArrayBuffer[String]
    nodeList.foreach { idNode =>
      val url = (idNode \ "@href").text
      val items = url.split("/")
//      println("hoge:"+ items(4)+"/"+items(6))
      busStopIdList += items(4)+"/"+items(6)
    }
    busStopIdList
  }

  def getBUsStopNameList(nodeList:NodeSeq):ArrayBuffer[String] = {
    val busStopNameList = new ArrayBuffer[String]

    nodeList.foreach { nameNode =>
      busStopNameList += nameNode.text
    }
    busStopNameList
  }

  def toNode(str: String): Node = {
    val hp = new HtmlParser
    hp.setNamePolicy(XmlViolationPolicy.ALLOW)

    val saxer = new NoBindingFactoryAdapter
    hp.setContentHandler(saxer)
    hp.parse(new InputSource(new StringReader(str)))

    saxer.rootElem
  }
}
