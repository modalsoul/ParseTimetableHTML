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
      statement.executeUpdate("create table bus_stop (id integer primary key AUTOINCREMENT, web_id string not null , bus_stop_name string, location string)")
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




}
