package com.cse.its.its;

import android.content.Context;
import android.graphics.Color;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

/**
 * Created by ajalan on 16/9/16.
 */
public class LineGraph {
    private XYSeries xSeries, ySeries;
    XYMultipleSeriesDataset dataset;
    XYSeriesRenderer xRenderer, yRenderer;
    XYMultipleSeriesRenderer multiRenderer;
    private GraphicalView mChart;

    /**
     * Constructor called.
     */
    LineGraph() {
        dataset = new XYMultipleSeriesDataset();

        xSeries = new XYSeries("X");
        //ySeries = new XYSeries("Y");

        // Add new XY series to the list
        dataset.addSeries(xSeries);
        //dataset.addSeries(ySeries);

        // Renderer for the X series
        xRenderer = new XYSeriesRenderer();
        xRenderer.setColor(Color.RED);
        xRenderer.setPointStyle(PointStyle.CIRCLE);
        xRenderer.setFillPoints(true);
        xRenderer.setLineWidth(1);
        xRenderer.setDisplayChartValues(false);
        xRenderer.setLineWidth(3f);

        // Renderer for the Y series
        /*yRenderer = new XYSeriesRenderer();
        yRenderer.setColor(Color.GREEN);
        yRenderer.setPointStyle(PointStyle.CIRCLE);
        yRenderer.setFillPoints(true);
        yRenderer.setLineWidth(1);
        yRenderer.setDisplayChartValues(false);
        yRenderer.setLineWidth(3f);*/

        // Renders the display and the orientation of the chart
        multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setXLabels(0);
        multiRenderer.setLabelsColor(Color.RED);
        multiRenderer.setChartTitle("t vs (x,y)");
        multiRenderer.setXTitle("Sensor Data");
        multiRenderer.setYTitle("Intensity Value");
        multiRenderer.setZoomButtonsVisible(true);
        //multiRenderer.setScale(0.25f);
        for(int i=-100;i<100;i++){
            multiRenderer.addYTextLabel(i,(i*2)+"");
        }
        multiRenderer.addSeriesRenderer(xRenderer);
        //multiRenderer.addSeriesRenderer(yRenderer);
        multiRenderer.setInScroll(true);
        multiRenderer.setZoomEnabled(true);
    }

    /**
     * Get the graph based on the chart rendered
     * @param context
     * @return GraphicalView
     */
    public GraphicalView getGraph(Context context){

        mChart = ChartFactory.getLineChartView(context, dataset, multiRenderer);
        return mChart;
    }

    public void addPoints(LightData point) {
        if (point.getTimestamp() >= 10000) {
            multiRenderer.setXAxisMax(point.getTimestamp());
            multiRenderer.setXAxisMin(point.getTimestamp()-10000);
        }
        else {
            multiRenderer.setXAxisMax(9999);
            multiRenderer.setXAxisMin(0);
        }
        xSeries.add(point.getTimestamp(), point.getX());
        //ySeries.add(point.getTimestamp(), point.getY());
    }

    public void addPreviousPoint(long time){
        int position = xSeries.getItemCount();
        xSeries.add(time,xSeries.getY(position-1));
        //ySeries.add(time,ySeries.getY(position-1));
    }
}
