package housedatamonitor.dataio;

import com.pi4j.component.temperature.TemperatureSensor;
import com.pi4j.component.temperature.impl.TmpDS18B20DeviceType;
import com.pi4j.io.w1.W1Device;
import com.pi4j.io.w1.W1Master;
import java.io.*;
import java.util.ArrayList;
import java.lang.Math;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This is a main class of a device House Data Monitor System application, for data input/output
 * Functionality:
 * - establish connection with a database
 * - get readings from sensors: sensor 1 (Room), sensor 2 (Outdoor)
 * - sends sensor data to a database, including date, time, and timestamp(created in a DatabaseConnectionPi class) 
 * - turns LED on/off based on the retrieved heating status data from the database
 * - 
 * @author Lukasz Bol
 */
public class HouseDataMonitorDataIO implements Serializable
{

    /**
     * This is a main method that controls the program: sends temperatures to the server, sets LED on/off
     * @param args the command line arguments
     */
    
    public static void main(String[] args) throws FileNotFoundException, InterruptedException
    {
        String sensor1 = null;
        String sensor2 = null;
        String sensor1ID = "28-03168c10ddff";
        String sensor2ID = "28-03168c3c82ff";
        String sensor1data;
        String sensor2data;
        float sensor1converted;
        float sensor2converted;
        
        Statement statement = null;        
        ResultSet result = null;
        
        String sensor1sql = "";
        String sensor2sql = "";
        
        
        DatabaseConnectionPi connection = new DatabaseConnectionPi();
        HeatingController LEDcontroller = new HeatingController();
        LEDcontroller.setHeatingInitialStatus();            
        
        while(true)
        {
            W1Master sensorsMaster = new W1Master();
            List<W1Device> sensorsList = sensorsMaster.getDevices(TmpDS18B20DeviceType.FAMILY_CODE);
            for(W1Device sensor : sensorsList)
            {
                //System.out.println("SENSOR ID: " + sensor.getId());
                //float sensortemp = (float)((TemperatureSensor)sensor).getTemperature();
                
                //System.out.println("SENSOR TEMP: " + sensortemp);
                String s = sensor.getId();
                String formattedSensorString = s.substring(0, s.length()-1); //removing last character from the sensor.getId() returned value
                if(formattedSensorString.equals("28-03168c10ddff"))
                {
                    float sensor1temp = (float)((TemperatureSensor)sensor).getTemperature();
                    connection.sendSensor1Data(sensor.getId(), sensor1temp, getDate(), getTime());
                    System.out.println("Sensor 1(Room): " + sensor1temp);
                }
                
                else if(formattedSensorString.equals("28-03168c3c82ff"))
                {
                    float sensor2temp = (float)((TemperatureSensor)sensor).getTemperature();                                
                    connection.sendSensor2Data(sensor.getId(), sensor2temp, getDate(), getTime());
                    System.out.println("Sensor 2(Outdoor): " + sensor2temp);
                }
            }
            if(connection.getHeatingState() == 1)
            {
                LEDcontroller.setHeatingOn();
            }
            else if (connection.getHeatingState() == 0)
            {
                LEDcontroller.setHeatingOff();
            }
            TimeUnit.SECONDS.sleep(1);
        }
    }
    /*
    * This method returns date in a format of year-month-day
    * @return dateFormat.format(date) - String representation of date, formatted
    */
    public static String getDate()
    {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }
    /*
    * This method returns time in a format of hours-minutes-seconds
    * @return timeFormat.format(time) - String representation of time, formatted
    */
    public static String getTime()
    {
        Date time = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        return timeFormat.format(time);
    }      
}