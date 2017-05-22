---
services: hdinsight
platforms: java
author: blackmist
---

# hdinsight-java-hive-jdbc

An example of how to use the JDBC to issue Hive queries from a Java client application.

The code in this project creates a new Hive table (external table,) and populates it with data from a sample table that is provided with the HDInsight cluster. Then it returns data from that table. Pretty basic.

##How to use JDBC with HDInsight

JDBC connections to an HDInsight cluster on Azure are made over port 443, and the traffic is secured using SSL. The public gateway that the clusters sit behind redirects the traffic to the port that Hive is actually listening on. So a typical connection string would like like the following:

    jdbc:hive2://CLUSTERNAME.azurehdinsight.net:443/default;transportMode=http;ssl=true;httpPath=/hive2

When establishing the connection, you have to specify the HDInsight cluster admin name and password. These authenticate the request to the gateway. For example:

    DriverManager.getConnection(connectionString,clusterAdmin,clusterPassword);

Once the connection is established, it's just sending queries and waiting on Hive to respond:

    sql = "SELECT querytime, market, deviceplatform, devicemodel, state, country from " + tableName + " LIMIT 3";
    stmt2 = conn.createStatement();
    System.out.println("\nRetrieving inserted data:");

    res2 = stmt2.executeQuery(sql);

    while (res2.next()) {
      System.out.println( res2.getString(1) + "\t" + res2.getString(2) + "\t" + res2.getString(3) + "\t" + res2.getString(4) + "\t" + res2.getString(5) + "\t" + res2.getString(6));
    }

##To run this example

1. Install Java version 7 or higher. Oracle, OpenJDBC, etc.; I'm not sure it really matters. I built/tested with Oracle Java 7 and 8. The Hadoop cluster I tested with is Azure HDInsight 3.5

2. Install [Maven](http://maven.apache.org/).

3. Get an HDInsight cluster. This can be either a [Linux-based](https://azure.microsoft.com/en-us/documentation/articles/hdinsight-hadoop-linux-tutorial-get-started/) or [Windows-based](https://azure.microsoft.com/en-us/documentation/articles/hdinsight-hadoop-tutorial-get-started-windows/) cluster.

4. Clone/fork the repository locally and change directories into it.

5. Build and run the project using the following Maven command:

        mvn compile exec:java -Dexec.args="clustername clusteradmin clusterpassword"

    - Replace __clustername__ with the name of your HDInsight cluster
    - Replace __clusteradmin__ with the admin account name for your cluster
    - Replace __clusterpassword__ with the password for the admin account

    Special note: If you are using PowerShell, you have to wrap the `-D` parameters in quotes. So it would be as follows:

        mvn compile exec:java "-Dexec.args=""clustername clusteradmin clusterpassword"""

This will return the following:

    Getting a description of the table:
    querytime       string
    market  string
    deviceplatform  string
    devicemodel     string
    state   string
    country string

    Inserting data into the table.

    Retrieving inserted data:
    01:37:19        en-US   Android Droid   X       Colorado
    16:43:41        en-US   Android Droid   X       Utah
    16:44:21        en-US   Android Droid   X       Utah

    Hive queries completed successfully!

## Project code of conduct

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.