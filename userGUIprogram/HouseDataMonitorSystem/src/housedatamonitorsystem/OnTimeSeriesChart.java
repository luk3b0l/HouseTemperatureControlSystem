package housedatamonitorsystem;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * This class implements DisplayData interface.
 * It overrides 'display' method and creates a Time Series Chart for further implementation in SensorChartGUI class
 * @author Lukasz Bol
 */
public class OnTimeSeriesChart implements DisplayData
{
    @Override
    public JFreeChart display(String chartTitle, XYSeriesCollection sensorDataSet, CategoryDataset dataset) 
    {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(chartTitle, "Time (hh:mm:ss)", "Temperature (Celsius)", sensorDataSet);        
        return chart;
    }
}