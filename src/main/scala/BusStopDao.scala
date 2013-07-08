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
class BusStopDao {

  def insertBusStop(busStopItems:ArrayBuffer[(String,String)]) = {
    var connection:Connection = null
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:sample.db")

      val statement:Statement = connection.createStatement()
      statement.setQueryTimeout(30)
      var result = 0
      busStopItems.foreach { item =>
        result = statement.executeUpdate("insert into bus_stop(route_id, web_id, bus_stop_name) values('-1', '" + item._1 + "', '" + item._2 +"')")
      }
      println("insert result is :" + result)
    } catch {
      case e:Exception =>
        e.printStackTrace()
        false
    }finally connection.close()
    true

  }



  def queryBusStopAll() = {
    var connection:Connection = null

    try {
      connection = DriverManager.getConnection("jdbc:sqlite:sample.db")

      val statement:Statement = connection.createStatement()
      statement.setQueryTimeout(30)
      val res:ResultSet =statement.executeQuery("select * from bus_stop")
      while (res.next()) {
        println(res.getInt("id") + "," + res.getString("web_id") + "," + res.getString("bus_stop_name"))
      }
    } catch {
      case e:Exception =>
        e.printStackTrace()
    } finally connection.close()
  }
  def queryBusStopIdByWebId(web_id:String):Int = {
    var connection:Connection = null
    var id:Int = -1
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:sample.db")

      val statement:Statement = connection.createStatement()
      statement.setQueryTimeout(30)
      val res:ResultSet =statement.executeQuery("select id from bus_stop where web_id = '" + web_id + "'")
      while (res.next()) {
        id = res.getInt("id")
      }
    } catch {
      case e:Exception =>
        e.printStackTrace()
    } finally connection.close()
    id
  }
  def queryBusStopWebIdExistance(webId:String):Int = {
    var connection:Connection = null
    var result:Int = 0
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:sample.db")

      val statement:Statement = connection.createStatement()
      statement.setQueryTimeout(30)
      val str = "select count(*) as id_num from bus_stop where web_id = '" + webId + "'"

      val res:ResultSet =statement.executeQuery(str)

      while (res.next()) {
        result = res.getInt("id_num")
      }
    } catch {
      case e:Exception =>
        e.printStackTrace()
    } finally connection.close()
    result
  }
  def queryBusStopWebIdById(busStopId:Array[String]):ArrayBuffer[String] = {
    var connection:Connection = null
    var webIdList:ArrayBuffer[String] = new ArrayBuffer[String]
    var idList = busStopId.head
    for (num <- 1 to busStopId.size-1) {
      idList += " or " + busStopId.indexOf(num)
    }

    try {
      connection = DriverManager.getConnection("jdbc:sqlite:sample.db")

      val statement:Statement = connection.createStatement()
      statement.setQueryTimeout(30)
      val res:ResultSet =statement.executeQuery("select web_id from bus_stop where id = '" + idList + "'")
      while (res.next()) {
        webIdList += res.getString("web_id")
      }
    } catch {
      case e:Exception =>
        e.printStackTrace()
    } finally connection.close()
    webIdList
  }

  def updateBusStopRouteIdByWebId(webId:String, routeId:Integer) = {
    var connection:Connection = null
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:sample.db")

      val statement:Statement = connection.createStatement()
      statement.setQueryTimeout(30)
      statement.executeUpdate("update bus_stop set route_id = '" + routeId + "' where web_id = '" + webId + "'")

    } catch {
      case e:Exception =>
        e.printStackTrace()
        false
    }finally connection.close()
    true
  }

}
