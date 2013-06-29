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

  val TOKYO23KU_AREA_ID = 0


  def getRouteList():ArrayBuffer[String] = {
    val routeListUrl = "http://timetablenavi.keikyu-bus.co.jp/dia/timetable/web/route/"

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



    val tokyoRouteList = new ArrayBuffer[(String,String,Int)]

    tokyoRouteListNode.foreach { item =>
      val route =  ((item \ "@value").text, item.text, TOKYO23KU_AREA_ID)
      // 下記路線は、時刻表ページに2路線分以上記載のため例外的に取り込まない
      if(route._1 != "0" && route._1 != "1070121011" && route._1 != "1060107012" && route._1 != "1060107011" &&
          route._1 != "1100204011" && route._1 != "8620106011" && route._1 != "8540091011" && route._1 != "8540091012" &&
          route._1 != "8540096011" && route._1 != "8540096012" && route._1 != "1080141011" && route._1 != "1050152011" &&
          route._1 != "1050151011" && route._1 != "1130241011" && route._1 != "1100207011" && route._1 != "1050168011" &&
          route._1 != "1050158011" && route._1 != "1120223011" && route._1 != "1120227012" && route._1 != "1120224011" &&
          route._1 != "1120228012" && route._1 != "1050153011" && route._1 != "1140261011" && route._1 != "1140261012" &&
          route._1 != "1050169011" && route._1 != "8510304011" && route._1 != "8510304012" && route._1 != "8510306012" &&
          route._1 != "8510306011" && route._1 != "8510305012" && route._1 != "1040061011" && route._1 != "8520318012" &&
          route._1 != "8520318011" && route._1 != "8530061012" && route._1 != "8530061011" && route._1 != "1160291011" &&
          route._1 != "8550155011" && route._1 != "8510303011") tokyoRouteList += route
    }

    val routeDao = new RouteDao
    routeDao.insertRoute(tokyoRouteList)

    val webIdList = new ArrayBuffer[String]
    tokyoRouteList.foreach { set =>
      webIdList += set._1
    }
    webIdList
  }

  def getBusStopWebId(busStops:String):ArrayBuffer[String] = {
    val idList:Array[String] = busStops.split(",")
    val busStopDao = new BusStopDao
    busStopDao.queryBusStopWebIdById(idList)
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
