package housedatamonitorsystem;

/**
 * This class extends SensorDataDisplayOnGUI class and it makes HumidityData class being able to display data on Bar Chart
 * This class is currently used for displaying Temperature sensors data only.
 * @author Lukasz Bol
 */
public class HumidityData extends SensorDataDisplayOnGUI
{
    public HumidityData()
    {
        displayingData = new OnBarChart(); // POLYMORPHISM used
    }
}
