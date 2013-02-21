
package scalamin

import java.io.StringReader
import scala.xml.Text
import scala.xml.Node
import scala.xml.parsing.NoBindingFactoryAdapter
import nu.validator.htmlparser.sax.HtmlParser
import nu.validator.htmlparser.common.XmlViolationPolicy
import org.xml.sax.InputSource
import org.apache.commons.io.FileUtils
import java.io.File

object Hello {
  def main(args:Array[String]) = {
    val str = FileUtils.readFileToString(new File("sample.xml"))
        
    val tableNode = (toNode(str) \\ "table").filter(_ \ "@class" contains Text("timetable"))
    
    val trNodes = (tableNode \\ "tr").filter(_ \\ "th" \ "@class" contains Text("hour"))
    
    trNodes.foreach { rowNode =>
      var hour=(rowNode \\ "th").text + ", "
      (rowNode \\ "td").foreach { tdNodes =>
        tdNodes.foreach { tableData =>
          println(hour + tableData.text.trim.replaceAll(" ", "").replaceAll("\n", ", "))
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

