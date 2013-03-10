package scalamin

import java.io.StringReader
import nu.validator.htmlparser.common.XmlViolationPolicy
import nu.validator.htmlparser.sax.HtmlParser
import org.xml.sax.InputSource
import xml.{NodeSeq, Text, Node}
import xml.parsing.NoBindingFactoryAdapter
import io.Source
import collection.mutable.ArrayBuffer

/**
 * Created with IntelliJ IDEA.
 * User: M
 * Date: 13/03/07
 * Time: 23:44
 * To change this template use File | Settings | File Templates.
 */
class Route {
  val TOKYO23KU_ROUTE_ID = "route_1"
  val KAWASAKI_ROUTE_ID = "route_2"
  val YOKOHAMA_ROUTE_ID = "route_3"
  val KAMAKURA_ROUTE_ID = "route_4"
  val FUJISAWA_ROUTE_ID = "route_5"
  val ZUSHI_ROUTE_ID = "route_6"
  val YOKOSUKA_ROUTE_ID = "route_7"
  val MIURASHI_ROUTE_ID = "route_8"
  val HAYAMAMACHI_ROUTE_ID = "route_9"



  def getRouteList():ArrayBuffer[String] = {
    val routeListUrl = "http://timetablenavi.keikyu-bus.co.jp/dia0315/timetable/web/route/"

    val src = Source.fromURL(routeListUrl, "Shift_JIS")

    val str = src.mkString

    val nodeList = toNode(str)

    val routeListNode:NodeSeq = (nodeList \\ "div").filter(_ \ "@class" contains Text("select_line")) \\ "p"

//    val areaRouteListNode = (routeListNode \\ "select").filter(_ \ "@id" contains("select_line"))

    val tokyoRouteListNode:NodeSeq = (routeListNode \\ "select").filter(_ \ "@id" contains Text(TOKYO23KU_ROUTE_ID)) \\ "option"

    val kawasakiRouteListNode = (routeListNode \\ "select").filter(_ \ "@id" contains(KAWASAKI_ROUTE_ID)) \\ "option"

    val yokohamaRouteListNode = (routeListNode \\ "select").filter(_ \ "@id" contains(YOKOHAMA_ROUTE_ID)) \\ "option"

    val kamakuraRouteListNode = (routeListNode \\ "select").filter(_ \ "@id" contains(KAMAKURA_ROUTE_ID)) \\ "option"

    val fujisawaRouteListNode = (routeListNode \\ "select").filter(_ \ "@id" contains(FUJISAWA_ROUTE_ID)) \\ "option"

    val zushiRouteListNode = (routeListNode \\ "select").filter(_ \ "@id" contains(ZUSHI_ROUTE_ID)) \\ "option"

    val yokosukaRouteListNode = (routeListNode \\ "select").filter(_ \ "@id" contains(YOKOSUKA_ROUTE_ID)) \\ "option"

    val miurashiRouteListNode = (routeListNode \\ "select").filter(_ \ "@id" contains(MIURASHI_ROUTE_ID)) \\ "option"

    val hayamamachiRouteListNode = (routeListNode \\ "select").filter(_ \ "@id" contains(HAYAMAMACHI_ROUTE_ID)) \\ "option"



    val tokyoRouteList = new ArrayBuffer[(String,String)]

    tokyoRouteListNode.foreach { item =>
      val route =  ((item \ "@value").text, item.text)
      if(route._1 != "0") tokyoRouteList += route
    }

    val dbAccess = new DBAccess
    dbAccess.insertRoute(tokyoRouteList)

    val webIdList = new ArrayBuffer[String]
    tokyoRouteList.foreach { set =>
      webIdList += set._1
    }
    webIdList
  }

  def getBusStopWebId(busStops:String):ArrayBuffer[String] = {
    val idList:Array[String] = busStops.split(",")
    val db = new DBAccess
    db.queryBusStopWebIdById(idList)
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
