package scalamin

import collection.mutable.ArrayBuffer
import java.sql.{ResultSet, Statement, DriverManager, Connection}

/**
 * Created with IntelliJ IDEA.
 * User: M
 * Date: 13/03/11
 * Time: 22:49
 * To change this template use File | Settings | File Templates.
 */
class RouteDao {

  def insertRoute(routeItems:ArrayBuffer[(String, String)]) = {
    var connection:Connection = null
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:sample.db")

      val statement:Statement = connection.createStatement()
      statement.setQueryTimeout(30)
      routeItems.foreach { item =>
        statement.executeUpdate("insert into route(web_id, route_name) values('" + item._1 + "', '" + item._2 +"')")
      }
    } catch {
      case e:Exception =>
        e.printStackTrace()
        false
    }finally connection.close()
    true
  }
  def updateRoute(item:(String, String, String, String)) = {
    var connection:Connection = null
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:sample.db")

      val statement:Statement = connection.createStatement()
      statement.setQueryTimeout(30)
      statement.executeUpdate("update route set starting = '" + item._2  + "', terminal = '" + item._3 + "', bus_stops = '" + item._4 + "' where web_id = '" + item._1 + "'")

    } catch {
      case e:Exception =>
        e.printStackTrace()
        false
    }finally connection.close()
    true
  }

  def queryRouteAll() = {
    var connection:Connection = null

    try {
      connection = DriverManager.getConnection("jdbc:sqlite:sample.db")

      val statement:Statement = connection.createStatement()
      statement.setQueryTimeout(30)
      val res:ResultSet =statement.executeQuery("select * from route")
      while (res.next()) {
        println(res.getInt("id") + "," + res.getString("web_id") + "," + res.getString("route_name"))
      }
    } catch {
      case e:Exception =>
        e.printStackTrace()
    } finally connection.close()
  }
  def queryRouteByWebId(web_id:String):(Int, String, String, String, String, String) = {
    var connection:Connection = null
    var route:(Int, String, String, String, String, String) = (-1, "", "", "", "", "")
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:sample.db")

      val statement:Statement = connection.createStatement()
      statement.setQueryTimeout(30)
      val res:ResultSet =statement.executeQuery("select * from route where web_id = '" + web_id + "'")
      while (res.next()) {
        route = (res.getInt("id"), res.getString("web_id"), res.getString("route_name"), res.getString("starting"), res.getString("terminal"), res.getString("bus_stops"))
      }
    } catch {
      case e:Exception =>
        e.printStackTrace()
    } finally connection.close()
    route
  }

}
