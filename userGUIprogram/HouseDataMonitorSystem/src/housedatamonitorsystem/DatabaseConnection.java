package housedatamonitorsystem;

/**
 *
 * This class is to provide a database connection for the House Data Monitor System.
 * Functionality:
 * - connection with the MySQL database on the Amazon Relational Database Service (Amazon RD)
 * - retrieving temperature readings from the database
 * - retrieving heating state (0 - heating OFF, 1 - heating ON)
 * - sending heating state (0 - heating OFF, 1 - heating ON)
 * - closing connection
 * - closing resources (Statement, ResultSet)
 * - checking connection state
 * - setting connection state (true/false)
 * 
 * @author Lukasz Bol
 */

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class DatabaseConnection 
{
    // Database URL and credentials:
    private static final String DB_URL = "jdbc:mysql://lukaszbol-finalproject2017.cptdw52vxos4.eu-west-2.rds.amazonaws.com:3306/monitordatabase";
    private static final String USER = "lukasz_bol";
    private static final String PASS = "T22jan17!";   
    
    private Connection DBconnection = null;
    private Statement statementObject = null;
    private Statement statementObject2 = null;   
    private ResultSet result = null;
    private ResultSet result2 = null;
    private boolean connectionState = false;

    /*
    * This constructor helps to initialise a database connection, using variables: DB_URL - database url, USER - user login, PASS - password
    */
    public DatabaseConnection()
    {
        try
        {
            // Establish a connection:
            DBconnection = DriverManager.getConnection(DB_URL, USER, PASS);
            setConnectionState(true);
            System.out.println("Connection established successfully");
        }
        catch (Exception e)
        {
            //Handle errors for Class.forName
            e.printStackTrace();
        }
    }
    
    /*
    * This method gets heating state from the database, using Statement object and executing a SQL query
    * @return Returns boolean representation of heating state (true/false)
    */
    public boolean getHeatingState()
    {
        boolean heatingState = true;
          
        try
        {
            if(isConnectionState() == true)
            {
                // Create a Statement object to execute any queries:
                statementObject = DBconnection.createStatement();

                // Execute a query and store the result:
                result = statementObject.executeQuery("SELECT heating_on FROM heating_status WHERE switch_num = 1;");

                // Go through a loop to get the result of each row of the table and add to the list:
                while(result.next())
                {
                    heatingState = result.getBoolean("heating_on");
                    return heatingState;
                }    
            }
            else
            {
                System.out.println("NO CONNECTION");
            }
        }
        catch(SQLException sqle)
        {
            // Handling errors for JDBC:
            System.out.println("getHeatingState EXCEPTION[sqle]: " + sqle);
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
    * This method gets sensor 1 (Room) temperature data from the database, by executing a SQL query
    * @return Returns String representation of a temperature stored in sensor1_data table, that is filled in with data coming from sensor 1(Room) on the Raspberry Pi
    */
    public String getSensor1Data()
    {
        String sensor1Data = "";
        
        try
        {
            if(isConnectionState() == true)
            {
                // Create a Statement object to execute any queries:
                statementObject = DBconnection.createStatement();

                // Execute a query and store the result:
                result = statementObject.executeQuery("SELECT value FROM sensor1_data WHERE measurement_id = 1;");

                // Get a result of a 'value' column:
                while(result.next())
                {
                    sensor1Data = result.getString("value");
                }        
            }
        }
        catch(SQLException sqle)
        {
            // Handling errors for JDBC:
            System.out.println("SQL exception occured: " + sqle);
        }
//        finally
//        {
//            // call a method to close resources:
//            closeResources(statementObject, result);  
//        }       
        return sensor1Data;
    }
    
    /*
    * This method gets sensor 2 (Outdoor) temperature data from the database, by executing a SQL query
    * @return Returns String representation of a temperature stored in sensor2_data table, that is filled in with data coming from sensor 2(Outdoor) on the Raspberry Pi
    */
    public String getSensor2Data()
    {
        String sensor2Data = "";
        
        try
        {
            if(isConnectionState() == true)
            {
                // Create a Statement object to execute any queries:
                statementObject2 = DBconnection.createStatement();

                // Execute a query and store the result:
                result2 = statementObject2.executeQuery("SELECT value FROM sensor2_data WHERE measurement_id = 1;");

                // Get a result of a 'value' column:
                while(result2.next())
                {
                    sensor2Data = result2.getString("value");
                }        
            }
        }
        catch(SQLException sqle)
        {
            // Handling errors for JDBC:
            System.out.println("SQL exception occured: " + sqle);
        }
//        finally
//        {
//            // call a method to close resources:
//            closeResources(statementObject2, result2);  
//        }       
        return sensor2Data;
    }
    
    /*
    * Sets heating_on to 1 (i.e. heating switched ON) in a database, to be evaluated further by the Raspberry Pi to switch the LED on.
    */
    public void setHeatingOn()
    {       
        try
        {
            if(isConnectionState() == true)
            {
            // Create a Statement object to execute any queries:
            statementObject = DBconnection.createStatement();
            
            // Execute a query:
            statementObject.executeUpdate("UPDATE heating_status SET heating_on = 1 WHERE switch_num = 1;");
            }
            else
            {
                System.out.println("NO CONNECTION!");
            }
        }
        catch(SQLException sqle)
        {
            //Handling errors for JDBC:
            System.out.println("SQL exception occured: ");
            sqle.printStackTrace();            
        }
//        finally
//        {
//            // finally block to close resources:
//            try
//            {
//                if(statementObject != null)
//                    statementObject.close();
//            }
//            catch(SQLException sqle)
//            {
//                //Handling errors for JDBC:
//                System.out.println("SQL exception occured: ");
//                sqle.printStackTrace();            
//            }
//        }   
    }
    
    /*
    * Sets heating_on to 0 (i.e. heating switched OFF) in a database, to be evaluated further by the Raspberry Pi to switch the LED off.
    */
    public void setHeatingOff()
    {        
        try
        {
            if(isConnectionState() == true)
            {
                // Create a Statement object to execute any queries:
                statementObject = DBconnection.createStatement();
                
                // Execute a query:
                statementObject.executeUpdate("UPDATE heating_status SET heating_on = 0 WHERE switch_num = 1;");      
            }
            else
            {
                System.out.println("NO CONNECTION!");
            }
        } 
        catch(SQLException sqle)
        {
            //Handling errors for JDBC:
            System.out.println("SQL exception occured: ");
            sqle.printStackTrace();            
        }
//        finally
//        {
//            // finally block to close resources:
//            try
//            {
//                if(statementObject != null)
//                    statementObject.close();
//            }
//            catch(SQLException sqle)
//            {
//                //Handling errors for JDBC:
//                System.out.println("SQL exception occured: ");
//                sqle.printStackTrace();            
//            }
//        }          
    }

    /*
    * @return Returns Timestamp representation of time in which sensor 1 data (Room temperature) was read.
    * This method is under development. It has been initially tested but due to conflicts occured on the chart when presenting the data, 
    * it was not used further as it was unsafe (i.e. could crash the program).
    */
    public Timestamp getReadingTime()
    {
        Timestamp timestamp = null;

        try
        {
            // Create a Statement object to execute any queries:
            statementObject = DBconnection.createStatement();
            
            // Execute a query and store the result:
            result = statementObject.executeQuery("SELECT timestamp FROM sensor1_data WHERE measurement_id = 1;");
            
            // Go through a loop to get the result of each row of the table and return:
            while(result.next())
            {
                timestamp = result.getTimestamp("timestamp");
            }        
        }
        catch(SQLException sqle)
        {
            // Handling errors for JDBC:
            System.out.println("SQL exception occured: " + sqle);
        }
//        finally
//        {
//            // call a method to close resources:
//            closeResources(statementObject, result);  
//        }       
        return timestamp;
    }
    
    /*
    * This method closes database connection.
    */
    public void closeConnection()
    {
        try
        {
            if(DBconnection != null)
            {
                DBconnection.close();
                setConnectionState(false);
                System.out.println("Connection terminated");
            }
        }
        catch(SQLException sqle)
        {
            //Handling errors for JDBC:
            System.out.println("SQL exception occured: ");
            sqle.printStackTrace();            
        }
    }
    
    /*
    * This method closes resources. It has been developed for the 'finally' try-catch blocks to reduce code duplication.
    * The main functionality of it is to close statamentObject and ResultSet.
    * This method is still under development, therefore commented out in try-catch blocks, until implemented and successfully tested.
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
            //Handling errors for JDBC:
            System.out.println("SQL exception occured: ");
            sqle.printStackTrace();            
        }
        try
        {
            if(result != null)
            {
                result.close();
                //System.out.println("result.close() SUCCESSFUL");
            }
        }
        catch(SQLException sqle)
        {
            //Handling errors for JDBC:
            System.out.println("SQL exception occured: ");
            sqle.printStackTrace();            
        }        
    }

    /*
    * This method returns boolean representation of connection state (true/false)
    * @return connectionState boolean type of connection state
    */
    public boolean isConnectionState() {
        return connectionState;
    }

    /*
    * This method sets connection state (true/false), to be used later on in a program
    * @param connectionState - boolean type parameter to set the connection state
    */
    public void setConnectionState(boolean connectionState) {
        this.connectionState = connectionState;
    } 
}