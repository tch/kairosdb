/*
 * Copyright 2013 Proofpoint Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kairosdb.anomalyDetection;


import org.kairosdb.core.DataPoint;

import java.util.Date;
import java.util.LinkedList;

/**
 Created with IntelliJ IDEA.
 User: bhawkins
 Date: 9/6/13
 Time: 1:17 PM
 To change this template use File | Settings | File Templates.
 */
public class MedianStreamAnomalyDetector implements AnomalyAlgorithm
{
	private static final int TREE_SIZE = 150;
	private static final int LAST_VALUE_SIZE = 2;
	private static final double FACTOR = 2.5;

	private AnalyzerTreeMap<AnalyzerDataPoint, String> m_tree;
	private LinkedList<Double> m_lastValues;

	public MedianStreamAnomalyDetector()
	{
		m_tree = new AnalyzerTreeMap<AnalyzerDataPoint, String>(TREE_SIZE);
		m_lastValues = new LinkedList<Double>();
	}

	@Override
	public double isAnomaly(String metricName, DataPoint dataPoint)
	{
		if (!metricName.equals("my_test"))
			return 0;

		/*boolean anomaly = false;

		if (m_tree.size() == TREE_SIZE)
		{
			double low = m_tree.firstKey().getValue();
			double med = m_tree.getRootKey().getValue();
			double hi = m_tree.lastKey().getValue();

			double range = Math.max(med - low, hi - med);
			range *= FACTOR;

			if (Math.abs(med - dataPoint.getDoubleValue()) > range)
			{
				anomaly = true;
			}
		}

		if (!anomaly)
			m_tree.put(new AnalyzerDataPoint(dataPoint.getTimestamp(), dataPoint.getDoubleValue()), null);*/

		double ret = 0.0;

		m_tree.put(new AnalyzerDataPoint(dataPoint.getTimestamp(), dataPoint.getDoubleValue()), null);

		double newValue = m_tree.lastKey().getValue() - m_tree.firstKey().getValue();
		m_lastValues.addFirst(newValue);

		if (m_tree.size() == TREE_SIZE)
		{
			double lastValue = m_lastValues.getLast();
			if ((newValue > lastValue) && ((newValue - lastValue) > (lastValue * 0.33)))
			{
				ret = ((newValue - lastValue) / lastValue);
				System.out.println("Anomaly found at "+ new Date(dataPoint.getTimestamp()) + " " + ret);
			}
		}

		if (m_lastValues.size() > LAST_VALUE_SIZE)
			m_lastValues.removeLast();

		return ret;
	}
}