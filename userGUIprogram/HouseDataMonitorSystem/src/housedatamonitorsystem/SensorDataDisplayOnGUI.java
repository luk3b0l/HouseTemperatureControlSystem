package housedatamonitorsystem;

import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * This class is to use actual DisplayData interface and its classes to check and set chart types. 
 * This class is still under development.
 * @author Lukasz Bol
 */
public class SensorDataDisplayOnGUI
{
    public DisplayData displayingData;    // COMPOSITION used - instead of inheriting the ability through inheritance, the class is composed with
                                          // object of DisplayData type, with the built-in ability
    
    private String chartTitle;
    private  XYSeriesCollection sensorDataSet;
    private CategoryDataset dataset;
    
    /*
    * This method is to display data on a chart, to check chart type. However, it is still under development.
    */
    public JFreeChart tryToDisplayData()
    {
        return displayingData.display(chartTitle, sensorDataSet, dataset); // the return statement under development
    }
    
    /*
    * This method uses COMPOSITION to change the displayingData of Displaydata type to change its abilities at runtime, i.e. allows to change the type of Chart dynamically, if needed.
    * @param newDisplayType - DisplayData parameter type to allow changing chart type to a different one, dynamically,
    * to be either of type OnBarChart or OnTimeSeriesChart.
    */
    public void setDisplayingAbility(DisplayData newDisplayType)
    {
        displayingData = newDisplayType;
    }
}
