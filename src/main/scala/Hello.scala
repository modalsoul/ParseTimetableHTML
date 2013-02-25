
package scalamin

import java.io.StringReader
import scala.xml.Text
import scala.xml.Node
import scala.xml.parsing.NoBindingFactoryAdapter
import scala.collection.mutable.ListBuffer
import nu.validator.htmlparser.sax.HtmlParser
import nu.validator.htmlparser.common.XmlViolationPolicy
import org.xml.sax.InputSource
import org.apache.commons.io.FileUtils
import java.io.File
import io.Source

object Hello {
  def main(args:Array[String]) = {
//    val str = FileUtils.readFileToString(new File("sample.xml"))

    val src = Source.fromURL("http://timetablenavi.keikyu-bus.co.jp/dia/timetable/web/51174/1020021012/08/", "Shift_JIS")
    val str = src.mkString
    val tableNode = (toNode(str) \\ "table").filter(_ \ "@class" contains Text("timetable"))
    
    val weekTypeNum = (tableNode \\ "tr" \\ "th").filter(_ \ "@class" contains Text("week")).size

    val hourNodeList = (tableNode \\ "tr" \\ "th").filter(_ \ "@class" contains Text("hour"))

    
    val trNodeList = (tableNode \\ "tr").filter(_ \\ "th" \ "@class" contains Text("hour"))
    
    val timeList = Array.ofDim[String](weekTypeNum, hourNodeList.size, 10)
    
    trNodeList.foreach { rowNode =>
      val tdNodeList = rowNode \\ "td"

      for ( currentWeek <- 0 to weekTypeNum-1) {
        val minuteList = tdNodeList(currentWeek).text.replaceAll(" ", "").split("\n").filter(_ != "")
        print(currentWeek)
        minuteList.foreach { minute =>
        	print("," + (rowNode \\ "th").text + ":" + minute)
        }
        println()
          /*
          for ( currentMinute <- 0 to minuteList.size-1) {
            timeList(currentWeek)(currentHour)(currentMinute) = hourNodeList(currentHour).text + ":" + minuteList(currentMinute)
          }*/
        //tdNodeList.foreach { tableData =>
        
        }
    }
    /*
    timeList.foreach { time =>
    	time.foreach { smalltime =>
    		smalltime.foreach { minutes =>
    		  println(minutes.filter(_ != null))
    		  
    		}
    	}
    }*/
    
    
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
}
