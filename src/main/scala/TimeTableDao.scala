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
class TimeTableDao {
  def insertTimeTable(timeItems:ArrayBuffer[(Int, Int, Int, String)]) = {
    var connection:Connection = null
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:sample.db")

      val statement:Statement = connection.createStatement()
      statement.setQueryTimeout(30)
      timeItems.foreach { item =>
        statement.executeUpdate("insert into time_table(bus_stop_id, route_id, type, starting_time) values('" + item._1 + "', '" + item._2 +"', '" + item._3 +"', '" + item._4 + "')")

      }
    } catch {
      case e:Exception =>
        e.printStackTrace()
        false
    }finally connection.close()
    true

  }


  def queryTimeTableAll():ArrayBuffer[(Int, Int, Int, String)] = {
    var connection:Connection = null
    var list:ArrayBuffer[(Int, Int, Int, String)] = new ArrayBuffer[(Int, Int, Int, String)]
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:sample.db")

      val statement:Statement = connection.createStatement()
      statement.setQueryTimeout(30)
      val res:ResultSet =statement.executeQuery("select * from time_table")
      while (res.next()) {
        val row = (res.getInt("bus_stop_id"), res.getInt("route_id"), res.getInt("type"), res.getString("starting_time"))
        list += row
      }
    } catch {
      case e:Exception =>
        e.printStackTrace()
    } finally connection.close()
    list
  }

  def queryTime(busStopId:Int, routeId:Int, weekType:Int):ArrayBuffer[String] = {
    var connection:Connection = null
    var list:ArrayBuffer[String] = new ArrayBuffer[String]()
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:sample.db")

      val statement:Statement = connection.createStatement()
      statement.setQueryTimeout(30)
      val res:ResultSet =statement.executeQuery("select starting_time from time_table where bus_stop_id = " + busStopId + " and route_id = " + routeId + " and type = " + weekType + " order by id")
      while (res.next()) {
//        list.addString(res.getString("starting_time"))
        val startingTime = res.getString("starting_time")
        list += startingTime
      }
    } catch {
      case e:Exception =>
        e.printStackTrace()
    } finally connection.close()
    list
  }
}
