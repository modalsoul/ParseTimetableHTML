package scalamin

import collection.mutable.ArrayBuffer
import java.sql.{ResultSet, Statement, DriverManager, Connection}

/**
 * Created with IntelliJ IDEA.
 * User: M
 * Date: 13/03/11
 * Time: 22:51
 * To change this template use File | Settings | File Templates.
 */
class TimeSummaryDao {
  def insertTimeSummary(item:(Int, Int, Int, Int, Int)) = {
    var connection:Connection = null
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:sample.db")

      val statement:Statement = connection.createStatement()
      statement.setQueryTimeout(30)
//      summaryItems.foreach { item =>
        statement.executeUpdate("insert into time_summary(bus_stop_id, route_id, type, hour, position) values('" + item._1 + "', '" + item._2 +"', '" + item._3 +"', '" + item._4 + "','" + item._5 + "')")

//      }
    } catch {
      case e:Exception =>
        e.printStackTrace()
        false
    }finally connection.close()
    true

  }


  def queryTimeSummaryAll():ArrayBuffer[(Int, Int, Int, Int)] = {
    var connection:Connection = null
    var list:ArrayBuffer[(Int, Int, Int, Int)] = new ArrayBuffer[(Int, Int, Int, Int)]
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:sample.db")

      val statement:Statement = connection.createStatement()
      statement.setQueryTimeout(30)
      val res:ResultSet =statement.executeQuery("select * from time_summary")
      while (res.next()) {
        val row = (res.getInt("bus_stop_id"), res.getInt("route_id"), res.getInt("type"), res.getInt("position"))
        list += row
      }
    } catch {
      case e:Exception =>
        e.printStackTrace()
    } finally connection.close()
    list
  }

}
