/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package housedatamonitor.dataio;

import com.pi4j.component.temperature.TemperatureSensor;
import com.pi4j.component.temperature.impl.TmpDS18B20DeviceType;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.w1.W1Device;
import com.pi4j.io.w1.W1Master;
import static housedatamonitor.dataio.HouseDataMonitorDataIO.getDate;
import static housedatamonitor.dataio.HouseDataMonitorDataIO.getTime;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * This class provides testing of the HouseDataMonitor-DataIO software functionality.
 * @author Lukasz Bol
 */
public class Tester 
{
    public static void main(String[] args) throws InterruptedException, FileNotFoundException
    {
        // DatabaseConnectionPi class testing:
        System.out.println("\n===== DatabaseConnectionPi class test start =====");
        System.out.println("\nCheck database connection.");
        DatabaseConnectionPi connection = new DatabaseConnectionPi();
        boolean connectionState = connection.isConnectionState();
        System.out.println("Connection state:" + connectionState);        
        
        System.out.println("\nCheck heating state.");
        int heatingState = connection.getHeatingState();
        System.out.println("Heating state: " + heatingState);
        System.out.println("\n===== DatabaseConnectionPi class test end =====");

        // HouseDataMonitorDataIO class testing (including manual testing of sensors' data retrieval and sending to the database):
        System.out.println("\n===== HouseDataMonitorDataIO class test start =====");
        System.out.println("\nGet date.");
        String date = HouseDataMonitorDataIO.getDate();
        System.out.println("Date: " + date);
        
        System.out.println("\nGet time.");
        String time = HouseDataMonitorDataIO.getTime();
        System.out.println("Time: " + time);
        
        System.out.println("\nManually testing sensor 1(Room) and sensor 2(Outdoor) sensors identification and data retrieval.");
        W1Master sensorsMaster = new W1Master();
        List<W1Device> sensorsList = sensorsMaster.getDevices(TmpDS18B20DeviceType.FAMILY_CODE);
        for(W1Device sensor : sensorsList)
        {
            float sensortemp = (float)((TemperatureSensor)sensor).getTemperature();
            System.out.println("Sensor ID: " + sensor.getId() + " Sensor temp: " + sensortemp + "\n");          
        }
        
        System.out.println("\nTesting sensor 1(Room) and sensor 2(Outdoor) data sending to the remote database.");
        
        for(W1Device sensor : sensorsList)
        {
            String s = sensor.getId();
            String formattedSensorString = s.substring(0, s.length()-1); //removing last character from the sensor.getId() returned value
            if(formattedSensorString.equals("28-03168c10ddff"))
            {
                float sensor1temp = (float)((TemperatureSensor)sensor).getTemperature();
                System.out.println("\nSensor 1(Room) data to be sent:" + 
                                   "\nSensor ID:" + sensor.getId() +
                                   "\nTemperature: " + sensor1temp +
                                   "\nDate: " + getDate() +
                                   "\nTime: " + getTime());
                connection.sendSensor1Data(sensor.getId(), sensor1temp, getDate(), getTime());
            }
            else if(formattedSensorString.equals("28-03168c3c82ff"))
            {
                float sensor2temp = (float)((TemperatureSensor)sensor).getTemperature();         
                System.out.println("\nSensor 2(Outdoor) data to be sent:" + 
                                    "\nSensor ID:" + sensor.getId() +
                                    "\nTemperature: " + sensor2temp +
                                    "\nDate: " + getDate() +
                                    "\nTime: " + getTime());
                connection.sendSensor2Data(sensor.getId(), sensor2temp, getDate(), getTime());
            }
        }
        System.out.println("\n===== HouseDataMonitorDataIO class test end =====");
        
        // HeatingController class testing:
        System.out.println("\n===== HeatingController class test start =====");
        HeatingController hController = new HeatingController();
        
        System.out.println("\nSet heating initial status(LED switched off).");
        hController.setHeatingInitialStatus();
        GpioPinDigitalOutput heatingLED = hController.getHeatingLED();
        System.out.println("LED status: " + heatingLED.getState());
        
        System.out.println("\nSet heating ON - LED status changed from LOW to HIGH and LED switched on");
        hController.setHeatingOn();
        System.out.println("LED status: " + heatingLED.getState());
        
        System.out.println("\nSet heating OFF - LED status changed from HIGH to LOW and LED switched off");
        hController.setHeatingOff();
        System.out.println("LED status: " + heatingLED.getState());
        System.out.println("\n===== HeatingController class test end =====");          
    }
}