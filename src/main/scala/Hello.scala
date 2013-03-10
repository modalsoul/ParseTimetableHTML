package scalamin

import java.io.{DataOutput, StringReader, File}
import scala.xml.Text
import scala.xml.Node
import scala.xml.parsing.NoBindingFactoryAdapter
import scala.collection.mutable.ArrayBuffer
import collection.mutable.{ArrayBuffer, ListBuffer}
import nu.validator.htmlparser.sax.HtmlParser
import nu.validator.htmlparser.common.XmlViolationPolicy
import org.xml.sax.InputSource
import org.apache.commons.io.FileUtils
import io.Source


object Hello {
//  val hoge = new DBAccess
//  hoge.testSqlite

//  val hoge = new Route
//  hoge.getBusStopIDList("1020021011")



  def main(args:Array[String]) = {
    val db = new DBAccess
    db.initDB
    db.makeRouteTable
    db.makeBusstopTable
    db.makeTimeTable
    val route = new Route
//    val webIdList = new ArrayBuffer[String]
    route.getRouteList.foreach { webId =>
      val busStop = new BusStop(webId)
      busStop.getBusStopList
    }

//    val busStop = new BusStop("1020021011")
//    busStop.getBusStopList
//    val busStop2 = new BusStop("1020021012")
//    busStop2.getBusStopList

//    val timeTable = new TimeTable
//    timeTable.getTimetable("1020021011", "51081")

//    val list:ArrayBuffer[(Int, Int, Int, String)] = new ArrayBuffer[(Int, Int, Int, String)]



  }

}
