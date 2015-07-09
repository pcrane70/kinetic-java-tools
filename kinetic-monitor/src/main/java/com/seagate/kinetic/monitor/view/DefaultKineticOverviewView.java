/**
 * 
 * Copyright (C) 2014 Seagate Technology.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package com.seagate.kinetic.monitor.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.StandardGradientPaintTransformer;

public class DefaultKineticOverviewView extends AbstractKineticStatView {
	private static final long serialVersionUID = -2822683433579140662L;
	protected DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
	protected GroupedStackedBarRenderer groupedstackedbarrenderer = new GroupedStackedBarRenderer();
	protected KeyToGroupMap keytogroupmap = new KeyToGroupMap("G1");
	protected String unitName;

	public DefaultKineticOverviewView(String title, String unitName) {
		super(title);
		this.unitName = unitName;
		JPanel jpanel = new ChartPanel(createChart(defaultcategorydataset));
		jpanel.setPreferredSize(new Dimension(590, 350));
		setContentPane(jpanel);
	}

	public synchronized void updateDataSet(String node, double put, double get,
			double delete) {
		String ipPlusPort = node.substring(0, node.indexOf("("));
		defaultcategorydataset.addValue(get, "Get", ipPlusPort);
		defaultcategorydataset.addValue(put, "Put", ipPlusPort);
		defaultcategorydataset.addValue(delete, "Delete", ipPlusPort);
	}

	private JFreeChart createChart(CategoryDataset categorydataset) {
		JFreeChart jfreechart = ChartFactory.createStackedBarChart("", "",
				"Total " + unitName, categorydataset,
				PlotOrientation.HORIZONTAL, true, true, false);
		keytogroupmap = new KeyToGroupMap("G1");
		keytogroupmap.mapKeyToGroup("Get", "G1");
		keytogroupmap.mapKeyToGroup("Put", "G1");
		keytogroupmap.mapKeyToGroup("Delete", "G1");
		groupedstackedbarrenderer.setSeriesToGroupMap(keytogroupmap);
		groupedstackedbarrenderer.setItemMargin(0.10000000000000001D);
		groupedstackedbarrenderer.setDrawBarOutline(false);

		GradientPaint gradientpaint = new GradientPaint(0.0F, 0.0F, new Color(
				34, 34, 255), 0.0F, 0.0F, new Color(136, 136, 255));
		groupedstackedbarrenderer.setSeriesPaint(0, gradientpaint);
		groupedstackedbarrenderer.setSeriesPaint(4, gradientpaint);
		groupedstackedbarrenderer.setSeriesPaint(8, gradientpaint);
		GradientPaint gradientpaint1 = new GradientPaint(0.0F, 0.0F, new Color(
				34, 255, 34), 0.0F, 0.0F, new Color(136, 255, 136));
		groupedstackedbarrenderer.setSeriesPaint(1, gradientpaint1);
		groupedstackedbarrenderer.setSeriesPaint(5, gradientpaint1);
		groupedstackedbarrenderer.setSeriesPaint(9, gradientpaint1);
		GradientPaint gradientpaint2 = new GradientPaint(0.0F, 0.0F, new Color(
				255, 34, 34), 0.0F, 0.0F, new Color(255, 136, 136));
		groupedstackedbarrenderer.setSeriesPaint(2, gradientpaint2);
		groupedstackedbarrenderer.setSeriesPaint(6, gradientpaint2);
		groupedstackedbarrenderer.setSeriesPaint(10, gradientpaint2);
		GradientPaint gradientpaint3 = new GradientPaint(0.0F, 0.0F, new Color(
				255, 255, 34), 0.0F, 0.0F, new Color(255, 255, 136));
		groupedstackedbarrenderer.setSeriesPaint(3, gradientpaint3);
		groupedstackedbarrenderer.setSeriesPaint(7, gradientpaint3);
		groupedstackedbarrenderer.setSeriesPaint(11, gradientpaint3);
		groupedstackedbarrenderer
				.setGradientPaintTransformer(new StandardGradientPaintTransformer(
						GradientPaintTransformType.HORIZONTAL));

		return jfreechart;
	}
}
