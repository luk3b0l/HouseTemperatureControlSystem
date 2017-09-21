package housedatamonitorsystem;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * This class provides a GUI window of the House Data Monitor System.
 * Main functionality:
 * - make a connection with a database to send/retrieve sensors' and heating data
 * - switch ON/OFF heating manually
 * - SEMI AUTO option in which a user is able to set heating ON and OFF temperatures
 * - FULL AUTO option in which a user does not have to interfere with a system. The system automatically switches ON/OFF heating, base on the temperatures gathered from sensors. 
 * - visual representation of sensor data (sensor 1 - ROOM, sensor 2 - OUTDOOR) gathered in real time
 * - shows Room temperature in a field
 * - shows Outdoor temperature in a field
 * - shows Heating Status (ON/OFF)
 * - menu bar which allows to Exit the application as well as read more about the system and author under the About section
 * @author Lukasz Bol
 */
public class SensorChartGUI
{

    
    // Declare and create attributes outside constructor, as they will be accessed by several methods:
    private String title = "";
    private String message = "";
    private final String CELSIUS = " \u00b0" + "C";
    private float tempOn = 0.0f;
    private float tempOff = 0.0f;
    private float sensor1data;
    private float sensor2data;
    private boolean automaticControl = false;
    private JFrame window;
    private DatabaseConnection connection = null;
    
    // Create a drop-down box, connect button:
    private JButton connectionButton = new JButton("Connect");
    private JButton heatingOnButton = new JButton("Heating ON");
    private JButton heatingOffButton = new JButton("Heating OFF");
    private JButton setAutoHeatingTempButton = new JButton("Set Temperatures");
    private JButton automaticControlButton = new JButton("Switch to SEMI AUTO");
    private JButton fullAutoControlButton = new JButton("Switch to FULL AUTO");
    private JLabel heatingControl = new JLabel("Manual Heating Control: ");
    private JLabel roomTempInfo = new JLabel("Room temperature: ");
    private JLabel outdoorTempInfo = new JLabel("     Outdoor temperature: ");
    private JLabel heatingStatus = new JLabel("     Heating Status: ");
    private JLabel setHeatingLabel = new JLabel("          Set Heating ON/OFF: \n");
    private JLabel setHeatingOnLabel = new JLabel("Temp ON: \n");
    private JLabel setHeatingOffLabel = new JLabel("Temp OFF: \n");
    private JLabel blank = new JLabel("                        ");
    private JLabel blank2 = new JLabel("                        ");
    private JTextField roomTempField = new JTextField("", 5);
    private JTextField outdoorTempField = new JTextField("", 5);
    private JTextField heatingStatusField = new JTextField("", 5);
    private JTextField setHeatingOn = new JTextField("", 5);
    private JTextField setHeatingOff = new JTextField("", 5);
    
    public SensorChartGUI()
    {
        // Creating and configuring the window:
        window = new JFrame();
        Container contentPane = window.getContentPane();
        contentPane.setLayout(new BorderLayout());

        window.setTitle("House Data Monitor System");
        window.setLayout(new FlowLayout());   // Split up the window into regions, for better organisation of the window
        window.setSize(1400, 570);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Close the entire program when hovering on the CLOSE icon.

        makeMenuBar(window);
        
        JPanel northWestPanel = new JPanel();
        northWestPanel.add(connectionButton);                
        window.add(northWestPanel, BorderLayout.WEST);
        
        JPanel northCentrePanel = new JPanel();
        northCentrePanel.add(heatingControl);
        northCentrePanel.add(heatingOnButton);
        northCentrePanel.add(heatingOffButton);
        northCentrePanel.add(blank);        
        northCentrePanel.add(blank2);
        northCentrePanel.add(automaticControlButton);
        northCentrePanel.add(fullAutoControlButton);
        window.add(northCentrePanel, BorderLayout.CENTER);
        
        JPanel northEastPanel = new JPanel();
        northEastPanel.add(roomTempInfo);
        northEastPanel.add(roomTempField);
        northEastPanel.add(outdoorTempInfo);
        northEastPanel.add(outdoorTempField);
        northEastPanel.add(heatingStatus);
        northEastPanel.add(heatingStatusField);
        
        northEastPanel.add(setHeatingLabel);
        northEastPanel.add(setHeatingOnLabel);
        northEastPanel.add(setHeatingOn);
        northEastPanel.add(setHeatingOffLabel);
        northEastPanel.add(setHeatingOff);
        northEastPanel.add(setAutoHeatingTempButton);
        window.add(northEastPanel, BorderLayout.EAST);
        
        initialStateBeforeConnect(); // all buttons are disabled apart from 'Connect' button

        // Create the chart:
        XYSeries series1 = new XYSeries("Room temperature");
        XYSeries series2 = new XYSeries("Outdoor temperature");
        XYSeriesCollection sensorDataSet = new XYSeriesCollection();
        sensorDataSet.addSeries(series1); // ROOM temperature readings
        sensorDataSet.addSeries(series2); // OUTDOOR temperature readings
        
        /*
        * The below code is commented out as it includes first version of the chart, no longer used. It is to show the evolution of the software
        */
        //JFreeChart temperatureChart = ChartFactory.createXYLineChart("Temperature Sensors Readings", "Time (seconds)", "Temperature (Celsius)", sensorDataSet);
        //window.add(new ChartPanel(temperatureChart), BorderLayout.SOUTH);        
        
        // The below is a current version of the chart:
        //JFreeChart tempChart = ChartFactory.createTimeSeriesChart("Temperature Sensors Readings", "Time (hh:mm:ss)", "Temperature (Celsius)", sensorDataSet);

                
        // Displaying Temperature Sensor Data using STRATEGY PATTERN and JFreeChart Time Series Chart:
        SensorDataDisplayOnGUI displayTemperatureOnTimeSeriesChart = new TemperatureData();
        DisplayData dispDataOnTimeSeries = displayTemperatureOnTimeSeriesChart.displayingData;
        JFreeChart temperatureDisplayChart = dispDataOnTimeSeries.display("Temperature Sensors Readings - TIME SERIES CHART", sensorDataSet, null);
        window.add(new ChartPanel(temperatureDisplayChart), BorderLayout.SOUTH);
        
        //Displaying Temperature Sensor Data using STRATEGY PATTERN and JFreeChart Bar Chart:
        String sensor1name = "Room temperature";
        String sensor2name = "Outdoor temperature";
        String whatCompares = "Temperature Sensors";
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        SensorDataDisplayOnGUI displayTemperatureOnBarChart = new HumidityData(); //HumidityData() class has been used, as it has a Bar Chart inside
        DisplayData dispDataOnBar = displayTemperatureOnBarChart.displayingData;
        JFreeChart barChart = dispDataOnBar.display("Temperature Sensors Readings - BAR CHART", null, dataset);
        window.add(new ChartPanel(barChart), BorderLayout.SOUTH);

        connectionButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                if(connectionButton.getText().equals("Connect"))
                {
                    connection = new DatabaseConnection();
                    connection.setConnectionState(true);
                    connectionButton.setText("Disconnect");                    
                    setToManual(); // set Heating options to MANUAL as initial state after establishing connection
                    
//                  A thread for getting data from sensor 1 to present them in a graph:
                    Thread sensor1dataThread = new Thread()
                    {
                        @Override
                        public void run()
                        {
                            String sensor1Data = connection.getSensor1Data();
                            
                            while(!sensor1Data.equals(""))
                            {                                 
                                try 
                                {
//                                    Timestamp sensorReadingTime = connection.getReadingTime();
//                                    Timestamp tStamp = java.sql.Timestamp.valueOf(sensorReadingTime.toString());
//                                    long tStampConverted = tStamp.getTime();
                                    //long timestampLong = Long.parseLong(sensorReadingTime);
                                    Date currentDate = new Date();
                                    long nowMilliseconds = currentDate.getTime();
                                    System.out.println("Sensor 1[Room]: " + sensor1Data);
                                    setSensor1data(Float.parseFloat(sensor1Data));
                                    double sensor1number = Double.parseDouble(sensor1Data);
                                    roomTempField.setText(sensor1Data + CELSIUS);
                                    series1.add(nowMilliseconds, sensor1number);
                                    dataset.addValue(sensor1number, sensor1name, whatCompares);
                                    TimeUnit.SECONDS.sleep(1);
                                    window.repaint();
                                    sensor1Data = connection.getSensor1Data();
                                    //sensorReadingTime = connection.getReadingTime();
                                    
                                }
                                catch(Exception e)
                                {
                                    System.out.println("sensor1dataThread EXCEPTION: " + e);
                                }   
                            }
                        }
                    };        
//                  A thread for getting data from sensor 2 to present them in a graph:
                    Thread sensor2dataThread = new Thread()
                    {
                        @Override
                        public void run()
                        {                            
                            String sensor2Data = connection.getSensor2Data();
                            
                            while(!sensor2Data.equals(""))
                            {                        
                                try 
                                {
                                    /*
                                    * The below code is for reading timestamps from the database for further use on a chart.
                                    * Currently not used as this section is under development.
                                    */
                                    //Timestamp sensorReadingTime = connection.getReadingTime();
                                    //Timestamp tStamp = java.sql.Timestamp.valueOf(sensorReadingTime.toString());
                                    //long tStampConverted = tStamp.getTime();
                                    Date currentDate = new Date();
                                    long nowMilliseconds = currentDate.getTime();
                                    System.out.println("Sensor 2[Outdoor]: " + sensor2Data);
                                    setSensor2data(Float.parseFloat(sensor2Data));
                                    double sensor2number = Double.parseDouble(sensor2Data);
                                    outdoorTempField.setText(sensor2Data + CELSIUS);
                                    series2.add(nowMilliseconds, sensor2number);
                                    dataset.addValue(sensor2number, sensor2name, whatCompares);
                                    TimeUnit.SECONDS.sleep(1);
                                    window.repaint();                                    
                                    sensor2Data = connection.getSensor2Data();
                                    //sensorReadingTime = connection.getReadingTime();
                                }
                                catch(Exception e)
                                {
                                    System.out.println("sensor2dataThread EXCEPTION: " + e);
                                }                                
                            }
                        }
                    };
                    
                    // This is a thread for getting heating status from the database:
                    Thread getHeatingStatusThread = new Thread()
                    {
                        @Override
                        public void run()
                        {                            
                            boolean heatingState;
                            String heatingOnOff = "";
                            while(true)
                            {                        
                                try 
                                {
                                    if(connection.isConnectionState() == true && connectionButton.getText().equals("Disconnect"))
                                    {
                                        heatingState = connection.getHeatingState();
                                        if(heatingState == true)
                                        {
                                            heatingStatusField.setText("ON");
                                        }
                                        else if(heatingState == false)
                                        {
                                            heatingStatusField.setText("OFF");
                                        }
                                        TimeUnit.SECONDS.sleep(5);
                                    }
                                }
                                catch(Exception e)
                                {
                                    System.out.println("getHeatingStatusThread EXCEPTION: " + e);
                                }
                            }
                        }  
                    };
                    
                    // Starting all the threads:
                    sensor1dataThread.start();
                    sensor2dataThread.start();
                    getHeatingStatusThread.start();
                }
                else if (connectionButton.getText().equals("Disconnect"))
                { 
                    connectionButton.setText("Connect");
                    initialStateBeforeConnect();                    
                    series1.clear();
                    series2.clear();
                    dataset.clear();
                    connection.setHeatingOff(); 
                    connection.closeConnection();
                    connection.setConnectionState(false);
                }    
            }            
        });

        // Adding further Action Listeners:
        heatingOn heatOn = new heatingOn();
        heatingOnButton.addActionListener(heatOn);        
        heatingOff heatOff = new heatingOff();
        heatingOffButton.addActionListener(heatOff);
        
        setHeatingOnOffTemp setHeatOnOff = new setHeatingOnOffTemp();
        setAutoHeatingTempButton.addActionListener(setHeatOnOff);
        
        autoManualHeating autoManualHeat = new autoManualHeating();
        automaticControlButton.addActionListener(autoManualHeat);
        
        fullAutoControlHeating fullHeatControl = new fullAutoControlHeating();
        fullAutoControlButton.addActionListener(fullHeatControl);


        // Show the window:
        window.setVisible(true);
        window.setLocationRelativeTo(null); // this centres the window
    }
    
    /*
    * This is a method for creating a menu bar at the top of the application, with options
    * File -> Quit
    * About -> Info
    * @param - frame is of JFrame type
    */
    public void makeMenuBar(JFrame frame) 
    {
        JMenuBar menubar = new JMenuBar();
        frame.setJMenuBar(menubar);

        JMenu fileMenu = new JMenu("File");
        menubar.add(fileMenu);
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(new QuitHandler());
        fileMenu.add(quitItem);

        JMenu aboutMenu = new JMenu("About");
        menubar.add(aboutMenu);
        JMenuItem infoItem = new JMenuItem("Info");
        infoItem.addActionListener(new InfoHandler());
        aboutMenu.add(infoItem);
    }
    
    
    /*
    * The below are private inner classes for handling various events:
    */
    // MENU event handlers:    
    class QuitHandler implements ActionListener 
    {
        @Override
        public void actionPerformed(ActionEvent e) 
        { 
            try {
                if(connection.isConnectionState() == false)
                {
                    System.exit(0);
                }
                else
                {
                    title = "Warning Information";
                    message = "You have to disconnect from the databae first.";
                    JOptionPane.showMessageDialog(window, message, title, JOptionPane.WARNING_MESSAGE);
                }     
                
            } catch (Exception ex) 
            {
                System.out.println("QuitHandler EXCEPTION: " + ex);
            }
        }
    }
    
    class InfoHandler implements ActionListener 
    {
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            title = "About";
            message = "\n<html><font size='5'>HOUSE TEMPERATURE CONTROL SYSTEM</font></html>\n\n"
                    + "This system allows to read temperatures from two sensors connected to Raspberry Pi"
                    + "\n and turn on/off heating (simulated by an LED attached to Raspberry PI breadboard).\n"
                    + "\n\nAuthor: Lukasz Bol"
                    + "\nVersion of the user GUI program: 1.7"
                    + "\nApril 2017"
                    + "\nUniversity of Hertfordshire";
                    
            
            JOptionPane.showMessageDialog(window, message, title, JOptionPane.INFORMATION_MESSAGE);
        }
    }
    // ==============================================
    
    // 'Heating ON' button configuration:
    class heatingOn implements ActionListener
    {
        // This method is necessary for an ActionListener interface
        @Override
        public void actionPerformed(ActionEvent event)
        {
            if(connectionButton.getText().equals("Disconnect"))
            {
                connection.setHeatingOn();
                title = "Confirmation";
                message = "Heating switched ON";
                JOptionPane.showMessageDialog(window, message, title, JOptionPane.INFORMATION_MESSAGE);  
            }
        }
    }    

    // 'Heating OFF' button configuration:
    class heatingOff implements ActionListener
    {
        // This method is necessary for an ActionListener interface
        @Override
        public void actionPerformed(ActionEvent event)
        {
            if(connectionButton.getText().equals("Disconnect"))
            {
                connection.setHeatingOff();
                title = "Confirmation";
                message = "Heating switched OFF";
                JOptionPane.showMessageDialog(window, message, title, JOptionPane.INFORMATION_MESSAGE);  
            }
        }
    }
    
    // 'setAutomaticHeating' button configuration:
    class setHeatingOnOffTemp implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            float heatOnTemp = 0.0f;
            float heatOffTemp = 0.0f;
            String tmpTempOn = setHeatingOn.getText();
            String tmpTempOff = setHeatingOff.getText();
            title = "Warning Information";
            message = "ERRORS encountered:";
            boolean errors = false;
            
            if(tmpTempOn.isEmpty() || tmpTempOff.isEmpty())
            {
                errors = true;
                message += "\n- One or both of the fields are empty.";
            }
            // check if input data are numbers
            if(!tmpTempOn.isEmpty() || !tmpTempOff.isEmpty())
            {
               try
               {
                   heatOnTemp = Float.parseFloat(tmpTempOn);
                   heatOffTemp = Float.parseFloat(tmpTempOff);
               }
               catch(NumberFormatException numbFex)
               {
                    errors = true;
                    message += "\n- Please enter numbers only.";
               }
            }
            
            if(heatOnTemp < 15 || heatOnTemp > 35)
            {
                errors = true;
                message += "\n- Temperature ON must be greater  or equal to 15" + CELSIUS + " and less or equal to 35" + CELSIUS;
            }
            
            if(heatOffTemp > 35 || heatOffTemp < 15)
            {
                errors = true;
                message += "\n- Temperature OFF must be lower or equal to 35" + CELSIUS + " and greater or equal to 15" + CELSIUS;
            }            
            
            if(heatOnTemp > heatOffTemp)
            {
                errors = true;
                message += "\n- Temp ON must be less or equal to Temp OFF";
            }
            
            if(errors == true)
            {
                JOptionPane.showMessageDialog(window, message, title, JOptionPane.WARNING_MESSAGE);
            }
            else
            {
               heatOnTemp = Float.parseFloat(tmpTempOn);
               heatOffTemp = Float.parseFloat(tmpTempOff);
               setTempOn(heatOnTemp);
               setTempOff(heatOffTemp);
               title = "Confirmation Information";
               message = "Heating set!";
               JOptionPane.showMessageDialog(window, message, title, JOptionPane.INFORMATION_MESSAGE);
            }
        }        
    }

    // 'heatingOffButton' button configuration:
    class setHeatingOffTemp implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) 
        {
            String heatOffTemp = setHeatingOff.getText();
            String heatOnTemp = setHeatingOn.getText();
            JPanel panel = new JPanel();
            
            if(Float.parseFloat(heatOffTemp) < Float.parseFloat(heatOnTemp))
            {
                title = "Warning Information";
                message = "Temp OFF must be greater or equal to Temp ON";
                JOptionPane.showMessageDialog(panel, message, title, JOptionPane.WARNING_MESSAGE);
            }
            else if(!heatOffTemp.equals(""))
            {
                setTempOff(Float.parseFloat(heatOffTemp));
                float tOff = getTempOff();
                System.out.println("Temp OFF set: " + tOff);
            }
            else
            {
                title = "Warning Information";
                message = "Enter a temperature to set the heating OFF";
                JOptionPane.showMessageDialog(panel, message, title, JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    // Setting heating to SEMI AUTO or MANUAL
    class autoManualHeating implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            if(automaticControlButton.getText().equals("Switch to SEMI AUTO") && isAutomaticControl() == false)
            {
                automaticControlButton.setText("Switch off SEMI AUTO");
                setAutomaticControl(true);     
                heatingOnButton.setEnabled(false);
                heatingOffButton.setEnabled(false);
                setHeatingOnLabel.setEnabled(true);
                setHeatingOn.setEnabled(true);                
                setHeatingOffLabel.setEnabled(true);
                setHeatingOff.setEnabled(true);
                setAutoHeatingTempButton.setEnabled(true);

                Thread heatingAutoControlThread = new Thread()
                {
                    public void run()
                    {
                        while(true)
                        {
                            try
                            {
                                while(connection.isConnectionState() == true && connectionButton.getText().equals("Disconnect") && automaticControlButton.getText().equals("Switch off SEMI AUTO") && automaticControl == true)
                                {
                                    heatingAutoControl(connection, getSensor1data());                                
                                }
                            }
                            catch(Exception e)
                            {
                                System.out.println("heatingAutoControlThread EXCEPTION: " + e);
                            }
                            try {
                                TimeUnit.SECONDS.sleep(5);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(SensorChartGUI.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                };
                heatingAutoControlThread.start();
            }
            else if(automaticControlButton.getText().equals("Switch off SEMI AUTO") && isAutomaticControl() == true)
            {
                setToManual();
            }
        } 
    }
    // Setting heating ON/OFF automatically, based on the temperatures gathered from sensors
    class fullAutoControlHeating implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            if(fullAutoControlButton.getText().equals("Switch to FULL AUTO") && automaticControlButton.getText().equals("Switch off SEMI AUTO") && isAutomaticControl() == true)
            {
                    title = "Warning Information";
                    message = "First, you need to switch off SEMI AUTO heating control.";
                    JOptionPane.showMessageDialog(window, message, title, JOptionPane.WARNING_MESSAGE);      
            }
            else if(fullAutoControlButton.getText().equals("Switch to FULL AUTO") && isAutomaticControl() == false)
            {
                try {
                    connection.setHeatingOff(); // set initial state before 'fullAutoHeating' method taking action
                    setAutomaticControl(true);
                    fullAutoHeating(connection);
                    fullAutoControlButton.setText("Switch off FULL AUTO");
                    heatingOnButton.setEnabled(false);
                    heatingOffButton.setEnabled(false);
                    automaticControlButton.setEnabled(false);
                    setHeatingOnLabel.setEnabled(false);
                    setHeatingOn.setEnabled(false);
                    setHeatingOffLabel.setEnabled(false);
                    setHeatingOff.setEnabled(false);
                    setAutoHeatingTempButton.setEnabled(false);
                    
                    title = "Information";
                    message = "From now, the system will take full control over the heating. Enjoy!\n" +
                               "\nIf OUTDOOR temperature < 20 AND ROOM temperature <= 20 ==> heating ON" +
                               "\nIf OUTDOOR temperature < 20 AND ROOM temperature > 25 ==> heating OFF" +
                               "\nIf OUTDOOR temperature >= 20 AND ROOM temperature > 21 ==> heating OFF" +
                               "\nIf OUTDOOR temperature >= 20 AND ROOM temperature <= 20 ==> heating ON";
                    JOptionPane.showMessageDialog(window, message, title, JOptionPane.INFORMATION_MESSAGE);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SensorChartGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if(fullAutoControlButton.getText().equals("Switch off FULL AUTO") && isAutomaticControl() == true)
            {
                setToManual();
            }
        }
    }
    
    /*
    * This method is to control heating base on readings from sensor 1 (Room), and temperature ON and OFF set by a user
    * @param connection - a parameter of DatabaseConnection type to access inner methods
    * @param sensor1temp - a parameter of float type to pass the readings inside the method for further evaluation
    * @throws InterrputedException if the TimeUnit.SECONDS.sleep(5) is interrupted
    */
    public void heatingAutoControl(DatabaseConnection connection, float sensor1temp) throws InterruptedException
    {
        float tempOn = getTempOn();
        float tempOff = getTempOff();
        boolean heatingState = connection.getHeatingState();

        // If ROOM temperature (sensor1temp) is less or equal to set temperature(tempOn), then we switch ON the heating
        if(connection.isConnectionState() == true && connectionButton.getText().equals("Disconnect") && automaticControlButton.getText().equals("Switch off SEMI AUTO") && isAutomaticControl() == true)
        {
            if(tempOn >= sensor1temp)
            {
                connection.setHeatingOn();
            }

            // If ROOM temperature (sensor1temp) is greater or equal to set temperature(tempOff), then we switch OFF the heating
            if(sensor1temp >= tempOff)
            {
                connection.setHeatingOff();
            } 
        }
        TimeUnit.SECONDS.sleep(5);
    }
    
    
    /*
    * This method is to control the heating automatically, without user intereference. It takes connection as a parameter.
    * If Outdoor temperature is less than 20 and room temperature is less or equal to 20, heating will turn on
    * If Outdoor temperature is less than 20 and room temperature is greater than 25, heating will turn off
    * If Outdoor temperature is greater or equal to 20 and room temperature is greater than 21, heating will turn off
    * If Outdoor temperature is greater or equal to 20 and room temperature is less or equal to 20, heating will turn on
    * @param connection - is of DatabaseConnection type, used to access inner connection methods
    */
    public void fullAutoHeating(DatabaseConnection connection) throws InterruptedException
    {
        float outdoorTempOnThreshold = 20;
        float roomTempOnThreshold = 20;
        float roomTempOffThreshold = 25;

        Thread fullAutoHeatingThread = new Thread()
        {
            public void run()
            {
                try
                {
                    while(connection.isConnectionState() == true && connectionButton.getText().equals("Disconnect") && isAutomaticControl() == true)
                    {
                        float sensor1temp = getSensor1data();
                        float sensor2temp = getSensor2data();
                        
                        if(sensor2temp < outdoorTempOnThreshold && sensor1temp <= roomTempOnThreshold)
                        {
                            connection.setHeatingOn();
                        }
                        else if(sensor2temp < outdoorTempOnThreshold && sensor1temp > roomTempOffThreshold)
                        {
                            connection.setHeatingOff();  
                        }
                        else if(sensor2temp >= outdoorTempOnThreshold && sensor1temp > roomTempOnThreshold)
                        {
                            connection.setHeatingOff();
                        }
                        else if(sensor2temp >= outdoorTempOnThreshold && sensor1temp <= roomTempOnThreshold)
                        {
                            connection.setHeatingOff();
                        }
                            
                        TimeUnit.SECONDS.sleep(5);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(SensorChartGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        fullAutoHeatingThread.start();
    }
 
    /*
    * This method sets the look of the software to its initial state, i.e. Heating On, Heating OFF, Switch to SEMI AUTO, Switch to FULL AUTO options are available for use
    */
    public void setToManual() 
    {
        setAutomaticControl(false);     
        heatingOnButton.setEnabled(true);
        heatingOffButton.setEnabled(true);
        automaticControlButton.setEnabled(true);
        automaticControlButton.setText("Switch to SEMI AUTO");
        fullAutoControlButton.setEnabled(true);
        fullAutoControlButton.setText("Switch to FULL AUTO");        
        setHeatingOnLabel.setEnabled(false);
        setHeatingOn.setEnabled(false);
        setHeatingOn.setText("");
        setHeatingOffLabel.setEnabled(false);
        setHeatingOff.setEnabled(false);
        setHeatingOff.setText("");
        setAutoHeatingTempButton.setEnabled(false);
        roomTempField.setEnabled(true);
        outdoorTempField.setEnabled(true);
        heatingStatusField.setEnabled(true);
        connection.setHeatingOff();
        setTempOn(0.0f);
        setTempOff(0.0f);
    }
    
    /*
    * This method provides initial state of the software, just at the start of application
    */
    public void initialStateBeforeConnect()
    {
        setAutomaticControl(false);     
        heatingOnButton.setEnabled(false);
        heatingOffButton.setEnabled(false);        
        automaticControlButton.setEnabled(false);
        fullAutoControlButton.setEnabled(false);
        setHeatingOnLabel.setEnabled(false);
        setHeatingOn.setEnabled(false);
        setHeatingOffLabel.setEnabled(false);
        setHeatingOff.setEnabled(false);
        setAutoHeatingTempButton.setEnabled(false);   
        roomTempField.setEnabled(false);
        roomTempField.setText("");
        outdoorTempField.setEnabled(false);
        outdoorTempField.setText("");
        heatingStatusField.setEnabled(false);
        heatingStatusField.setText("");      
        automaticControlButton.setText("Switch to SEMI AUTO");
        fullAutoControlButton.setText("Switch to FULL AUTO");        
    }
    
    /*
    * This method gets a float representation of temperature ON set during the SEMI AUTO option
    * @return tempOn - returns float representation of set temperature ON
    */
    public float getTempOn() {
        return tempOn;
    }

    /*
    * This method sets temperature ON as a float
    * @param tempOn - a parameter of float type that passes the temperature from the field and sets the temperature ON in tempOn variable
    */
    public void setTempOn(float tempOn) {
        this.tempOn = tempOn;
    }

    /*
    * This method gets a float representation of temperature OFF set during the SEMI AUTO option
    * @return tempOff - returns float representation of set temperature OFF
    */
    public float getTempOff() {
        return tempOff;
    }

    /*
    * This method sets temperature OFF as a float
    * @param tempOff - a parameter of float type that passes the temperature from the field and sets the temperature OFF in tempOff variable
    */    
    public void setTempOff(float tempOff) {
        this.tempOff = tempOff;
    }

    /*
    * This method gets a float representation of temperature set for sensor 1(Room)
    * @return sensor1data - returns float representation of sensor 1(Room) data
    */
    public float getSensor1data() {
        return sensor1data;
    }

    /*
    * This method sets sensor 1(Room) data as a float
    * @param sensor1data - a parameter of float type that passes the temperature read from the sensor 1 (Room)
    */
    public void setSensor1data(float sensor1data) {
        this.sensor1data = sensor1data;
    }

    /*
    * This method gets a float representation of temperature set for sensor 2(Outdoor)
    * @return sensor2data - returns float representation of sensor 2(Outdoor) data
    */
    public float getSensor2data() {
        return sensor2data;
    }

    /*
    * This method sets sensor 2(Outdoor) data as a float
    * @param sensor2data - a parameter of float type that passes the temperature read from the sensor 2 (Outdoor)
    */
    public void setSensor2data(float sensor2data) {
        this.sensor2data = sensor2data;
    }
    
    /*
    * This method returns state of the automatic control, used in some methods for checking its state
    * @return automaticControl - returns boolean representation of automatic control state
    */
    public boolean isAutomaticControl() {
        return automaticControl;
    }

    /*
    * This method sets state of the automatic control, used in some methods later on to indicate SEMI or FULL automatic heating control
    * @param automaticControl - boolean type parameter that passes true/false to set automatic control state for further indication
    */
    public void setAutomaticControl(boolean automaticControl) {
        this.automaticControl = automaticControl;
    }
}