
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

object Hello {
  def main(args:Array[String]) = {
    val str = FileUtils.readFileToString(new File("sample.xml"))
        
    val tableNode = (toNode(str) \\ "table").filter(_ \ "@class" contains Text("timetable"))
    
    val weekTypeNum = (tableNode \\ "tr" \\ "th").filter(_ \ "@class" contains Text("week")).size
    
    val hourList = (tableNode \\ "tr" \\ "th").filter(_ \ "@class" contains Text("hour")).text
    
    val trNodeList = (tableNode \\ "tr").filter(_ \\ "th" \ "@class" contains Text("hour"))
    
    val timeList = Array.ofDim[String](weekTypeNum, hourList.size, 10)
    
    trNodeList.foreach { rowNode =>
      var hour=(rowNode \\ "th").text
      val tdNodeList = rowNode \\ "td"
      
      //(rowNode \\ "td").foreach { tdNodeList =>
      for ( currentHour <- 0 to hourList.size-1) {
        for ( currentWeek <- 0 to weekTypeNum-1) {
          val minuteList = tdNodeList(currentWeek).text.replaceAll(" ", "").split("\n").filter(_ != "")
          for ( currentMinute <- 0 to minuteList.size-1) {
            timeList(currentWeek)(currentHour)(currentMinute) = hourList(currentHour) + ":" + minuteList(currentMinute)
          }
        //tdNodeList.foreach { tableData =>
        
        }
      }
    }
    
    timeList.foreach { time =>
      time.foreach { smalltime =>
    		smalltime.foreach { minutes =>
    		  println(minutes)
    		}
    	}
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
}

