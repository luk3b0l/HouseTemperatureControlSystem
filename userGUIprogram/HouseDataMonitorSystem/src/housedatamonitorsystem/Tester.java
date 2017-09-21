package housedatamonitorsystem;


import java.io.*;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

/**
 * This is a testing class, to manually test Database Connection.
 * @author Lukasz Bol
 */
public class Tester 
{    
    public static void main(String[] args) throws FileNotFoundException, InterruptedException
    {
        System.out.println("\n===== DATABASE TEST start =====");
        System.out.println("\nCheck database connection.");
        DatabaseConnection conn = new DatabaseConnection();
        boolean connectionState = conn.isConnectionState();
        System.out.println("Connection state: " + connectionState);
        
        System.out.println("\nCheck heating state.");
        
        System.out.println("Heating state: " + conn.getHeatingState());

        System.out.println("\nChange heating state to ON");
        conn.setHeatingOn();         
        TimeUnit.SECONDS.sleep(5);  
        System.out.println("Heating state: " + conn.getHeatingState());
        
        System.out.println("\nChange heating state to OFF");
        conn.setHeatingOff();        
        TimeUnit.SECONDS.sleep(5);
        System.out.println("Heating state: " + conn.getHeatingState());
                
        Timestamp readingTime = conn.getReadingTime();
        System.out.println("Timestamp of the first row: " + readingTime);
        
        System.out.println("\nCheck sensor1(Room) and sensor2(Outdoor) data retrieving (5 readings from both sensors)");
        
        String sensor1Data = "";        
        String sensor2Data = "";
        
        for(int i = 0; i < 5l; i++)
        {
            sensor1Data = conn.getSensor1Data();
            sensor2Data = conn.getSensor2Data();
            System.out.println("Sensor1(Room) data: " + sensor1Data);
            System.out.println("Sensor2(Outdoor) data: " + sensor2Data);
            TimeUnit.SECONDS.sleep(10);
        }
        
        System.out.println("\nClose connection and check its state.");
        conn.closeConnection();
        boolean connectionState1 = conn.isConnectionState();
        System.out.println("Connection state: " + connectionState1);
        System.out.println("\n===== DATABASE TEST end =====");    
    }  
}
