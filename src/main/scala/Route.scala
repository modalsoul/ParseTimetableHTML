package scalamin

import java.io.StringReader
import nu.validator.htmlparser.common.XmlViolationPolicy
import nu.validator.htmlparser.sax.HtmlParser
import org.xml.sax.InputSource
import xml.{Text, Node}
import xml.parsing.NoBindingFactoryAdapter
import io.Source

/**
 * Created with IntelliJ IDEA.
 * User: M
 * Date: 13/03/07
 * Time: 23:44
 * To change this template use File | Settings | File Templates.
 */
class Route {
  val urlPostfix = "http://timetablenavi.keikyu-bus.co.jp/dia0315/timetable/web/any/"
  def getBusStopIDList(routeId:String) {
    val src = Source.fromURL(urlPostfix + routeId + "/", "Shift_JIS")
    val str = src.mkString

    val nodeList = toNode(str)
    val busStopNodeList = (nodeList \\ "a").filter(_ \ "@class" contains Text("post"))

    busStopNodeList.foreach { busStopNode =>
      val url = (busStopNode \ "@href").text
      val items = url.split("/")
      println(items(4))

    }
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
