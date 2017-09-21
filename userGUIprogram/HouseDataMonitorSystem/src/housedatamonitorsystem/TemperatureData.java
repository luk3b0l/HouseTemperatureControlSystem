package housedatamonitorsystem;

/**
 * This class extends SensorDataDisplayOnGUI class and it makes TemperatureData class being able to display data on Time Series Chart
 * @author Lukasz Bol
 */
public class TemperatureData extends SensorDataDisplayOnGUI
{
    public TemperatureData()
    {
        displayingData = new OnTimeSeriesChart(); // POLYMORPHISM used
    }

}