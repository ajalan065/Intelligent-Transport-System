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
 * Created by ajalan on 24/9/16.
 */
public class AccelLineGraph {
    private XYSeries xSeries, ySeries, zSeries;
    XYMultipleSeriesDataset dataset;
    XYSeriesRenderer xRenderer, yRenderer, zRenderer;
    XYMultipleSeriesRenderer multiRenderer;
    private GraphicalView mChart;

    AccelLineGraph(){
        dataset = new XYMultipleSeriesDataset();

        xSeries = new XYSeries("X");
        ySeries = new XYSeries("Y");
        zSeries = new XYSeries("Z");

        dataset.addSeries(xSeries);
        dataset.addSeries(ySeries);
        dataset.addSeries(zSeries);

        xRenderer = new XYSeriesRenderer();
        xRenderer.setColor(Color.RED);
        xRenderer.setPointStyle(PointStyle.CIRCLE);
        xRenderer.setFillPoints(true);
        xRenderer.setLineWidth(1);
        xRenderer.setDisplayChartValues(false);
        xRenderer.setLineWidth(3f);

        yRenderer = new XYSeriesRenderer();
        yRenderer.setColor(Color.GREEN);
        yRenderer.setPointStyle(PointStyle.CIRCLE);
        yRenderer.setFillPoints(true);
        yRenderer.setLineWidth(1);
        yRenderer.setDisplayChartValues(false);
        yRenderer.setLineWidth(3f);

        zRenderer = new XYSeriesRenderer();
        zRenderer.setColor(Color.BLUE);
        zRenderer.setPointStyle(PointStyle.CIRCLE);
        zRenderer.setFillPoints(true);
        zRenderer.setLineWidth(1);
        zRenderer.setDisplayChartValues(false);
        zRenderer.setLineWidth(3f);

        multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setXLabels(0);
        multiRenderer.setLabelsColor(Color.RED);
        multiRenderer.setChartTitle("t vs (x,y,z)");
        multiRenderer.setXTitle("Sensor Data");
        multiRenderer.setYTitle("Values of Acceleration");
        multiRenderer.setZoomButtonsVisible(true);
        //multiRenderer.setScale(0.25f);
        for(int i=-100;i<100;i++){
            multiRenderer.addYTextLabel(i,(i*2)+"");
        }
        multiRenderer.addSeriesRenderer(xRenderer);
        multiRenderer.addSeriesRenderer(yRenderer);
        multiRenderer.addSeriesRenderer(zRenderer);

        multiRenderer.setInScroll(true);
        multiRenderer.setZoomEnabled(true);
    }
    public void addPoints(AccelData point){
        if(point.getTimestamp()>=10000){
            multiRenderer.setXAxisMax(point.getTimestamp());
            multiRenderer.setXAxisMin(point.getTimestamp()-10000);
        }else{
            multiRenderer.setXAxisMax(9999);
            multiRenderer.setXAxisMin(0);
        }
        xSeries.add(point.getTimestamp(),point.getX());
        ySeries.add(point.getTimestamp(),point.getY());
        zSeries.add(point.getTimestamp(),point.getZ());
    }

    public void addPreviousPoint(long time){
        int position = xSeries.getItemCount();
        xSeries.add(time,xSeries.getY(position-1));
        ySeries.add(time,ySeries.getY(position-1));
        zSeries.add(time,zSeries.getY(position-1));
    }
    public GraphicalView getGraph(Context context){

        mChart = ChartFactory.getLineChartView(context, dataset, multiRenderer);
        return mChart;
    }
    public void scrollToEnd(boolean state){
        multiRenderer.setInScroll(state);
    }
}
