/**
 * Created with IntelliJ IDEA.
 * User: M
 * Date: 13/03/07
 * Time: 21:46
 * To change this template use File | Settings | File Templates.
 */
package scalamin
import java.sql.{ResultSet, Statement, Connection, DriverManager}

class MappingRoute {

  def testSqlite {
    Class.forName("org.sqlite.JDBC");

    val connection:Connection = DriverManager.getConnection("jdbc:sqlite:sample.db")

    val statement:Statement = connection.createStatement()
    statement.setQueryTimeout(30)
    statement.executeUpdate("drop table if exists sample")
    statement.executeUpdate("create table sample (id integer, name string)")
    statement.executeUpdate("insert into sample values(1, 'hoge')" )
    val res:ResultSet = statement.executeQuery("select * from sample")

    while(res.next()) {
      println("name:" + res.getString("name"))
      println("id:" + res.getInt("id"))

    }
    connection.close()
  }
}
