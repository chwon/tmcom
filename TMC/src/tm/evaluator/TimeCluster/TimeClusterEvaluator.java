/*
 This file is part of TMcom.

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package tm.evaluator.TimeCluster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import tm.datasource.Datasource;
import tm.evaluator.AbstractEvaluator;
import tm.rating.Review;

public class TimeClusterEvaluator extends AbstractEvaluator {

	private String earliestTimestamp;
	private String latestTimestamp;

	private final int epsilonDefault = 7 * (60 * 60 * 24); // first number specifies days
	private final int minptsDefault = 3;
	
	private int epsilon = epsilonDefault;
	private int minpts = minptsDefault;
	
	private final String paramEpsilonInDays = "EPSILONINDAYS";
	private final String paramMinpts = "MINPTS";

	Collection<ReviewCluster> clusters;

	private final String htmlSnippetTemplateFilePath = "/resources/TimeClusterHtmlSnippetTemplate.html";
	private final String placeholderReviewedEntity = "REVIEWEDENTITY";
	private final String placeholderReviewPageUrl = "REVIEWPAGEURL";
	private final String placeholderClusterData = "CLUSTERDATA";
	private final String placeholderTotalReviews = "TOTALREVIEWS";
	private final String placeholderFirstTs = "FIRSTTS";
	private final String placeholderLastTs = "LASTTS";

	public TimeClusterEvaluator(Datasource datasource) {
		super(datasource);
	}

	public TimeClusterEvaluator() {
		super();
	}

	@Override
	protected ReturnCode doEvaluation() {

		ElkiDBScanAdapter dbscan = new ElkiDBScanAdapter(rating);
		earliestTimestamp = dbscan.getEarliestTimestamp();
		latestTimestamp = dbscan.getLatestTimestamp();

		clusters = dbscan.run(epsilon, minpts);

		return ReturnCode.OK;

	}

	@Override
	public void setParameters(Map<String, String[]> params) {
		if (params == null) return;
		
		System.out.println("HERE");
		
		String[] pEpsArray = params.get(paramEpsilonInDays);
		if (pEpsArray != null && pEpsArray.length > 0) {
			Integer pEpsInt = stringToInt(pEpsArray[0]);
			if (pEpsInt != null && pEpsInt.intValue() > 0) {
				epsilon = pEpsInt.intValue() * (60 * 60 * 24);
			}
		}
		
		String[] pMinptsArray = params.get(paramMinpts);
		if (pMinptsArray != null && pMinptsArray.length > 0) {
			Integer pMinptsInt = stringToInt(pMinptsArray[0]);
			if (pMinptsInt != null && pMinptsInt.intValue() > 0) {
				minpts = pMinptsInt.intValue();
			}
		}
	}

	private Integer stringToInt(String intStr) {
		int res;
		try {
			res = Integer.valueOf(intStr);
		} catch (NumberFormatException e) {
			return null;
		}
		return res;
	}

	public void setEpsilon(int epsilon) {
		this.epsilon = epsilon;
	}

	public void setMinpts(int minpts) {
		this.minpts = minpts;
	}

	@Override
	public String generateEvaluationHtmlPage()
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateEvaluationHtmlSnippet()
			throws UnsupportedOperationException {

		String htmlSnippetTemplate = fileToString(htmlSnippetTemplateFilePath);
		
		StringBuffer clusterData = new StringBuffer();
		int clusterNo = 1;
		for (ReviewCluster c : clusters) {
			if (!c.getName().equals("Noise")) {
				List<Integer> valueList = c.getSortedValues();

				BigDecimal avgQRating = new BigDecimal(0);
				for (Review r : c.keySet()) {
					BigDecimal qRating = new BigDecimal(
							r.getQuantitativeReview());
					avgQRating = avgQRating.add(qRating);
				}
				avgQRating = avgQRating.divide(new BigDecimal(c.size()), 1,
						BigDecimal.ROUND_HALF_DOWN);

				clusterData.append("{name: \"Cluster " + clusterNo + "\", ");
				clusterData.append("first: " + valueList.get(0).toString()
						+ ", ");
				clusterData.append("last: "
						+ valueList.get(c.size() - 1).toString() + ", ");
				clusterData.append("size: " + new Integer(c.size()).toString()
						+ ", ");
				clusterData.append("averagerating: "
						+ avgQRating.toPlainString() + "},\n");

				clusterNo++;
			}
		}

		String totalReviewsString = new Integer(rating.getReviews().size())
				.toString();

		return htmlSnippetTemplate
				.replace(placeholderReviewedEntity, rating.getReviewedEntity().getName())
				.replace(placeholderReviewPageUrl, rating.getWebsiteInfo().getBaseUrl())
				.replace(placeholderClusterData, clusterData.toString())
				.replace(placeholderTotalReviews, totalReviewsString)
				.replace(placeholderFirstTs, earliestTimestamp)
				.replace(placeholderLastTs, latestTimestamp);

	}

	@Override
	public String generateEvaluationNumericalValue()
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateEvaluationStringValue()
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

//	public void printClusters() {
//
//		System.out.println(clusters.size()
//				+ " clusters found (including noise).");
//
//		for (ReviewCluster c : clusters) {
//			System.out.println(c);
//			List<Integer> sortedVals = c.getSortedValues();
//			for (Integer i : sortedVals) {
//				Date date = new Date(i.longValue() * 1000L);
//				System.out.println("\t" + date.toGMTString());
//			}
//		}
//
//	}
	
	private String fileToString(String filename) {

		try {

			InputStream in = this.getClass().getResourceAsStream(filename);
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(in));
			StringBuilder stringBuilder = new StringBuilder();
			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}

			bufferedReader.close();
			return stringBuilder.toString();

		} catch (IOException e) {

			throw new RuntimeException("File " + filename
					+ " could not be opened.", e);

		}

	}
	

}
