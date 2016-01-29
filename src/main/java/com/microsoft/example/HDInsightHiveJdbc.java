package com.microsoft.example;

import java.sql.*;

public class HDInsightHiveJdbc {
  public static void main(String[] args) throws SQLException {

    if(args.length!=3) {
      System.err.println("Invalid number of arguments!");
      System.err.println("Usage:");
      System.err.println("HDInsightHiveJdbc <clustername> <clusteradmin> <adminpassword>");
      System.exit(1);
    }
    //Assume that the arguments are in correct order
    String clusterName = args[0];
    String clusterAdmin = args[1];
    String clusterPassword = args[2];

    //Variables to hold statements, connection, and results
    Connection conn=null;
    Statement stmt = null;
    Statement stmt2 = null;
    ResultSet res1 = null;
    ResultSet res2 = null;

    try
    {
      //Load the HiveServer2 JDBC driver
      Class.forName("org.apache.hive.jdbc.HiveDriver");

      //Create the connection string
      // Note that HDInsight always uses the external port 443 for SSL secure
      //  connections, and will direct it to the hiveserver2 from there
      //  to the internal port 10001 that Hive is listening on.
      String connectionQuery = String.format(
        "jdbc:hive2://%s.azurehdinsight.net:443/default;ssl=true?hive.server2.transport.mode=http;hive.server2.thrift.http.path=/hive2",
        clusterName);

      //Get the connection using the cluster admin user and password
      conn = DriverManager.getConnection(connectionQuery,clusterAdmin,clusterPassword);
      stmt = conn.createStatement();

      //Will be reused for qeuries and results
      String sql =null;

      //Drop the 'hivesampletablederived' table, if it exists
      // from a previous run
      String tableName = "hivesampletablederived";
      sql =  "drop table if exists " + tableName;
      stmt.execute(sql);

      //Create the 'hivesampletablederived':
      // Store it at /hivesampletablederived on the default cluster storage
      // It is space delimited and stored in text FORMAT
      String tableLocation = "wasb:///hivesampletablederived";
      sql =   "CREATE EXTERNAL TABLE hivesampletablederived(querytime string, market string, deviceplatform string, devicemodel string, state string, country string)" +
              "ROW FORMAT DELIMITED FIELDS TERMINATED BY ' '" +
              "STORED AS TEXTFILE LOCATION '" + tableLocation + "'";
      stmt.execute(sql);

      //Retrieve and display a description of the table
      sql = "describe " + tableName;
      System.out.println("\nGetting a description of the table:");
      res1 = stmt.executeQuery(sql);
      while (res1.next()) {
          System.out.println(res1.getString(1) + "\t" + res1.getString(2));
      }

      //Insert data into the new table
      sql = "INSERT INTO TABLE hivesampletablederived " +
      "SELECT querytime, market, deviceplatform, devicemodel, state, country from hivesampletable LIMIT 10";
      System.out.println("\nInserting data into the table.");
      stmt.execute(sql);

      //Retrieve data from the table
      sql = "SELECT querytime, market, deviceplatform, devicemodel, state, country from " + tableName + " LIMIT 3";
      stmt2 = conn.createStatement();
      System.out.println("\nRetrieving inserted data:");

      res2 = stmt2.executeQuery(sql);

      while (res2.next()) {
        System.out.println( res2.getString(1) + "\t" + res2.getString(2) + "\t" + res2.getString(3) + "\t" + res2.getString(4) + "\t" + res2.getString(5) + "\t" + res2.getString(6));
      }
      System.out.println("\nHive queries completed successfully!");
    }

    //Catch exceptions
    catch (SQLException e )
    {
      e.getMessage();
      e.printStackTrace();
      System.exit(1);
    }
    catch(Exception ex)
    {
      ex.getMessage();
      ex.printStackTrace();
      System.exit(1);
    }
    //Close connections
    finally {
      if (res1!=null) res1.close();
      if (res2!=null) res2.close();
      if (stmt!=null) stmt.close();
      if (stmt2!=null) stmt2.close();
    }
  }
}
