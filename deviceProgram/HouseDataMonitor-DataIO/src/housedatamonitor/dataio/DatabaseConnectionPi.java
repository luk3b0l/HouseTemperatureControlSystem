
package housedatamonitor.dataio;

/**
 *
 * This class is to establish connection with the database, retrieve sensors' data and turn ON/OFF a LED on Raspberry Pi.
 * It also allows to get/set heating state, and close resources(i.e. Statement object and ResultSet)
 * 
 * This class uses external library called The Pi4J for software-hardware communication with Raspberry Pi, i.e.:
 * - retrieving data from sensors
 * - switching ON/OFF the LED
 * - giving some information on the system network(i.e. hostname, ip addresses, name server)
 * @author Lukasz Bol
 */

import com.pi4j.system.NetworkInfo;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.lang.*;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnectionPi 
{
    // Database URL and credentials:
    private static final String DB_URL = "jdbc:mysql://lukaszbol-finalproject2017.cptdw52vxos4.eu-west-2.rds.amazonaws.com:3306/monitordatabase";
    private static final String USER = "lukasz_bol";
    private static final String PASS = "T22jan17!";
    
    private Connection DBconnection = null;
    private Statement statementObject = null;
    private ResultSet result = null;
    private boolean connectionState = false;
    
    public DatabaseConnectionPi()
    {
        try
        {
            DBconnection = DriverManager.getConnection(DB_URL, USER, PASS);
            setConnectionState(true);
            System.out.println("Connection established successfully");
            
            // Additional information about the system NETWORK:
            System.out.println("HOSTNAME: " + NetworkInfo.getHostname());
            for(String ipAddress : NetworkInfo.getIPAddresses())
                System.out.println("IP ADDRESSES: " + ipAddress);
            for(String nameserver : NetworkInfo.getNameservers())
                System.out.println("NAMESERVER: " + nameserver);
        }
        catch(Exception e)
        {
            // Handle errors for Class.forName
            e.printStackTrace();
        }
        
    }
    /*
    * This method gets heating state from the database, using Statement object and executing a SQL query
    */
    public int getHeatingState()
    {
         int heatingState = -1;
        
        try
        {
            // Create a Statement object to execute any queries:
            statementObject = DBconnection.createStatement();
            
            // Execute a query and store the result:
            result = statementObject.executeQuery("SELECT heating_on FROM heating_status WHERE switch_num = 1;");
            
            while(result.next())
            {
                heatingState = result.getInt("heating_on");
                return heatingState;
            }
        }
        catch(SQLException sqle)
        {
            // Handling errors for JDBC:
            System.out.println("SQL exception occured: ");
            sqle.printStackTrace();
        }
//        finally
//        {
//            // call a method to close resources:
//            closeResources(statementObject, result);           
//        }
        return heatingState;
    }
    
    /*
    * This method sends data gathered from sensor 1 (sensorID, value, date, time, and timestamp) to a database.
    * Timestamp is currently not used in the user GUI application, but may be used for data analysis. 
    * Also, it will be used for displaying data in a chart as well as may be a sort of detection element for Raspberry Pi to indicate that id does not work.
    * @param sensorID - String representation of sensor id
    * @param value - float representation of temperature
    * @param date - String representation of date
    * @param time - String representation of time
    */
    public void sendSensor1Data(String sensorID, float value, String date, String time)
    {
        try {
            Date now = new Date();
            Timestamp tStamp = new Timestamp(now.getTime());
            //String sqlStatement = "INSERT into sensor1_data VALUES(null, ?,?,?,?);";
            String sqlStatement = "UPDATE sensor1_data set sensor_id = ?, value = ?, date = ?, time = ?, timestamp = now() where measurement_id = 1;";
            PreparedStatement prepStatement = DBconnection.prepareStatement(sqlStatement);
            prepStatement.setString(1, sensorID);
            prepStatement.setFloat(2, value);
            prepStatement.setString(3, date);
            prepStatement.setString(4, time);
            
            // Execute the statement:
            prepStatement.execute();
            
            String sqlStatementArchive = "INSERT into sensor1_data_archive(sensor_id, value, date, time, timestamp) VALUES (?,?,?,?,?);";
            PreparedStatement prepStatementArchive = DBconnection.prepareStatement(sqlStatementArchive);
            prepStatementArchive.setString(1, sensorID);
            prepStatementArchive.setFloat(2, value);
            prepStatementArchive.setString(3, date);
            prepStatementArchive.setString(4, time);
            prepStatementArchive.setTimestamp(5, tStamp);
            // Execute the statement:
            prepStatementArchive.executeUpdate();         
            
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnectionPi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
    * This method sends data gathered from sensor 2 (sensorID, value, date, time, and timestamp) to a database.
    * Timestamp is currently used as in sendSensor2Data method
    * @param sensorID - String representation of sensor id
    * @param value - float representation of temperature
    * @param date - String representation of date
    * @param time - String representation of time
    */    
    public void sendSensor2Data(String sensorID, float value, String date, String time)
    {
        try {
            Date now = new Date();
            Timestamp tStamp = new Timestamp(now.getTime());
            //String sqlStatement = "INSERT into sensor2_data VALUES(null, ?,?,?,?);";
            String sqlStatement = "UPDATE sensor2_data set sensor_id = ?, value = ?, date = ?, time = ?, timestamp = now() where measurement_id = 1;";
            PreparedStatement prepStatement = DBconnection.prepareStatement(sqlStatement);
            prepStatement.setString(1, sensorID);
            prepStatement.setFloat(2, value);
            prepStatement.setString(3, date);
            prepStatement.setString(4, time);
            
            // Execute the statement:
            prepStatement.execute();
            
            String sqlStatementArchive = "INSERT into sensor2_data_archive(sensor_id, value, date, time, timestamp) VALUES (?,?,?,?,?);";
            PreparedStatement prepStatementArchive = DBconnection.prepareStatement(sqlStatementArchive);
            prepStatementArchive.setString(1, sensorID);
            prepStatementArchive.setFloat(2, value);
            prepStatementArchive.setString(3, date);
            prepStatementArchive.setString(4, time);
            prepStatementArchive.setTimestamp(5, tStamp);
            // Execute the statement:
            prepStatementArchive.executeUpdate(); 
            
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnectionPi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    /*
    * This method is the same as in the user GUI software of this system. It closes resources, i.e. Statement and ResultSet
    * The method is still under development, and was commented out from try-catch blocks as it was causing some errors on ResultSet
    *
    * @param statementObject - parameter of Statement type
    * @param result - parameter of ResultSet type
    */
    public void closeResources(Statement statementObject, ResultSet result)
    {    
                // finally block to close resources:            
        try
        {
            if(statementObject != null)
            {
                statementObject.close();
                //System.out.println("statementObject.close() SUCCESSFUL");
            }
        }
        catch(SQLException sqle)
        {
            // Handling errors for JDBC:
            System.out.println("SQL exception occured: ");
            sqle.printStackTrace();
        }
        try
        {
            if(result != null)
            {
                result.close();
                //System.out.print("result.close() SUCCESSFUL");
            }
        }
        catch(SQLException sqle)
        {
            // Handling errors for JDBC:
            System.out.println("SQL exception occured: ");
            sqle.printStackTrace();
        }
        
    }
    /*
    * This method is to represent actual connection state, by returning boolean representation
    * @return connectionState is returned (true/false)
    */
    public boolean isConnectionState() {
        return connectionState;
    }

    /*
    * The method to set connection state after establishing or closing connection(true or false respectively)
    * @param connectionState - boolean type parameter to set the connections state
    */
    public void setConnectionState(boolean connectionState) {
        this.connectionState = connectionState;
    }
}  

