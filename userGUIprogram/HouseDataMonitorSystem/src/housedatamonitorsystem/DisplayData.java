package housedatamonitorsystem;

import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
/**
 * This interface is to provide display() method for displaying different types of charts. 
 * It helps to eliminate duplicated code.
 * The concept varies in this interface(as it uses two different classes - OnBarChart, OnTimeSeriesChart, to display data in a different way)
 * Classes that are going to implement this interface are going to allow classes OnBarChart and OnTimeSeriesChart, which is known as decoupling. 
 * @author Lukasz Bol
 */
public interface DisplayData
{
    /*
    * This method is to display sensor data in different types of chart:
    * @param chartTitle - title of the chart
    * @param sensorDataSet - data set of type XYSeriesCollection needed for some chart types
    * @param dataset - data set of type CategoryDataset needed for some chart types
    */
    JFreeChart display(String chartTitle, XYSeriesCollection sensorDataSet, CategoryDataset dataset);
}