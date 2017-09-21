package housedatamonitorsystem;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * This class implements DisplayData interface.
 * It overrides 'display' method and creates a Bar Chart for further implementation in SensorChartGUI class
 * @author Lukasz Bol
 */
public class OnBarChart implements DisplayData
{
    @Override
    public JFreeChart display(String chartTitle, XYSeriesCollection sensorDataSet, CategoryDataset dataset) 
    {
        JFreeChart chart = ChartFactory.createBarChart(chartTitle, "Temperature Sensors", "Temperature (Celsius)", dataset);
        return chart;
    }
}
