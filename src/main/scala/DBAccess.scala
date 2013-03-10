/**
 * Created with IntelliJ IDEA.
 * User: M
 * Date: 13/03/07
 * Time: 21:46
 * To change this template use File | Settings | File Templates.
 */
package scalamin
import java.sql.{ResultSet, Statement, Connection, DriverManager}
import collection.mutable.ArrayBuffer
import java.util

class DBAccess {
  Class.forName("org.sqlite.JDBC");


  def initDB {
    var connection:Connection = null

    try {
      connection = DriverManager.getConnection("jdbc:sqlite:sample.db")

      val statement:Statement = connection.createStatement()
      statement.setQueryTimeout(30)
      statement.executeUpdate("drop table if exists android_metadata")
      statement.executeUpdate("create table android_metadata (locale text default 'en_US')")
      statement.executeUpdate("drop table if exists history")
      statement.executeUpdate("create table history (id integer primary key, route_id integer not null, bus_stop_id integer not null, id_string string unique, create_date string not null, update_date string not null)")
      statement.executeUpdate("drop table if exists area")
      statement.executeUpdate("create table area (id integer, name string)")
      statement.executeUpdate("insert into area values(0, '東京23区')")
      statement.executeUpdate("insert into area values(1, '川崎市')")
      statement.executeUpdate("insert into area values(2, '横浜市')")
      statement.executeUpdate("insert into area values(3, '鎌倉市')")
      statement.executeUpdate("insert into area values(4, '藤沢市')")
      statement.executeUpdate("insert into area values(5, '逗子市')")
      statement.executeUpdate("insert into area values(6, '横須賀市')")
      statement.executeUpdate("insert into area values(7, '三浦市')")
      statement.executeUpdate("insert into area values(8, '葉山町')")
    } catch {
      case e:Exception =>
        e.printStackTrace()
    } finally connection.close()
  }

  def makeRouteTable() = {
    var connection:Connection = null

    try {
      connection = DriverManager.getConnection("jdbc:sqlite:sample.db")

      val statement:Statement = connection.createStatement()
      statement.setQueryTimeout(30)
      statement.executeUpdate("drop table if exists route")
      statement.executeUpdate("create table route (id integer primary key AUTOINCREMENT, web_id string not null unique, route_name string, terminal text, starting text, bus_stops)")
    } catch {
      case e:Exception =>
        e.printStackTrace()
    } finally connection.close()
  }
  def makeBusstopTable() = {
    var connection:Connection = null

    try {
      connection = DriverManager.getConnection("jdbc:sqlite:sample.db")

      val statement:Statement = connection.createStatement()
      statement.setQueryTimeout(30)
      statement.executeUpdate("drop table if exists bus_stop")
      statement.executeUpdate("create table bus_stop (id integer primary key AUTOINCREMENT, web_id string not null unique, bus_stop_name string, location string)")
    } catch {
      case e:Exception =>
        e.printStackTrace()
    } finally connection.close()
  }
  def makeTimeTable() = {
    var connection:Connection = null

    try {
      connection = DriverManager.getConnection("jdbc:sqlite:sample.db")

      val statement:Statement = connection.createStatement()
      statement.setQueryTimeout(30)
      statement.executeUpdate("drop table if exists time_table")
      statement.executeUpdate("create table time_table (id integer primary key AUTOINCREMENT, bus_stop_id integer, route_id integer, type integer, starting_time string)")
    } catch {
      case e:Exception =>
        e.printStackTrace()
    } finally connection.close()
  }

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

  def insertBusStop(busStopItems:ArrayBuffer[(String,String)]) = {
    var connection:Connection = null
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:sample.db")

      val statement:Statement = connection.createStatement()
      statement.setQueryTimeout(30)
      busStopItems.foreach { item =>
        statement.executeUpdate("insert into bus_stop(web_id, bus_stop_name) values('" + item._1 + "', '" + item._2 +"')")
      }
    } catch {
      case e:Exception =>
        e.printStackTrace()
        false
    }finally connection.close()
    true

  }


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
    var result:Int = -1
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:sample.db")

      val statement:Statement = connection.createStatement()
      statement.setQueryTimeout(30)
      val str = "select count(*) as id_num from bus_stop where web_id = '" + webId + "'"
      println(str)
      val res:ResultSet =statement.executeQuery(str)

      while (res.next()) {
        result = res.getInt("id_num")


      }
      println("hoge:"+result)
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


}
