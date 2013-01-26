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

package tm.datasource.tripadvisor.com;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import tm.datasource.AbstractDatasource;
import tm.rating.Review;

public class TripadvisorComDatasource extends AbstractDatasource {

	private enum SOURCE {
		LOAD_FROM_FILE, LOAD_FROM_URL
	};

	private SOURCE source = SOURCE.LOAD_FROM_URL;

	private String ratingBasefileFullPath;
	private String ratingBasefileDirectory;
	private String ratingBasefileName;

	SimpleDateFormat inFormat = new SimpleDateFormat("MMMMM dd, yyyy",
			Locale.US);
	SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	public TripadvisorComDatasource() {
		super();
	}

	@Override
	public ReturnCode loadData(String ref) {
		
		if (source == SOURCE.LOAD_FROM_FILE) {
			
			ratingBasefileFullPath = ref;

			File file = new File(ratingBasefileFullPath);
			ratingBasefileDirectory = file.getParent() + File.separator;
			ratingBasefileName = file.getName();

			String fileAsString = fileToString(ratingBasefileFullPath, encoding);
			fillHeaderData(fileAsString);
			fillReviewDataFromFile();
			
			return ReturnCode.OK;
			
		}
		
		if (source == SOURCE.LOAD_FROM_URL) {
			
			return super.loadData(ref);
			
		}
		
		return ReturnCode.ERROR;
		
	}

	protected void traverseReviewData(String ref) throws IOException {

		String nextPageSearchPatternStart = "<a href=\"";
		String nextPageSearchPatternEnd = "\" class=\"guiArw sprite-pageNext";

		String currentWebpage = ref;

		while (!currentWebpage.equals("")) {
			
			System.out.println("Processing " + currentWebpage);

			String webpageAsString = urlToString(currentWebpage, encoding);
			fillReviewData(webpageAsString);

			int nextPageAddressEndIndex = webpageAsString
					.indexOf(nextPageSearchPatternEnd);

			if (nextPageAddressEndIndex != -1) {

				int nextPageAddressStartIndex = webpageAsString.lastIndexOf(
						nextPageSearchPatternStart, nextPageAddressEndIndex)
						+ nextPageSearchPatternStart.length();
				String nextPagePath = webpageAsString.substring(
						nextPageAddressStartIndex, nextPageAddressEndIndex);
				currentWebpage = ratingBaseSitePrefix + nextPagePath;

			} else {

				currentWebpage = "";

			}

		}

	}

	private void fillReviewDataFromFile() {

		int counterPosition = ratingBasefileName.indexOf("-Reviews-") + 9;
		String currentFilename = ratingBasefileFullPath;

		int counter = 0;
		do {

			String fileAsString = fileToString(currentFilename, encoding);
			fillReviewData(fileAsString);

			counter++;
			currentFilename = ratingBasefileDirectory
					+ ratingBasefileName.substring(0, counterPosition) + "or"
					+ counter + "0-"
					+ ratingBasefileName.substring(counterPosition);

		} while (new File(currentFilename).exists());

	}

	protected void fillHeaderData(String fileContent) {

		String reviewedEntityName = "";
		int startIndex = fileContent.indexOf("<title>") + 7;
		int endIndex = fileContent.indexOf("</title>");
		if (startIndex < endIndex) {
			reviewedEntityName = fileContent.substring(startIndex, endIndex);
		}
		rating.getReviewedEntity().setName(reviewedEntityName);

		rating.getQuantitativeReviewScheme().setUnitDescription("Star");
		rating.getQuantitativeReviewScheme().setReviewSchemeOrder("ascending");
		rating.getQuantitativeReviewScheme().setMinValue("1.0");
		rating.getQuantitativeReviewScheme().setMaxValue("5.0");
		rating.getQuantitativeReviewScheme().setStep("1.0");

		rating.getWebsiteInfo().setName("Trip Advisor");
		rating.getWebsiteInfo().setBaseUrl("www.tripadvisor.com");

	}

	protected void fillReviewData(String fileContent) {

		String dateSearchPatternStart = "<span class=\"ratingDate\">Reviewed ";
		String dateSearchPatternEnd = "</span";
		String quantitySearchPatternStart = "<img class=\"sprite-ratings\" src=\"http://c1.tacdn.com/img2/x.gif\" alt=\"";
		String quantitySearchPatternEnd = " of 5 stars\"";

		int dateSearchSpaceOffset = 0;
		int quantitySearchSpaceOffset = 0;

		int dateStartIndex = fileContent.indexOf(dateSearchPatternStart,
				dateSearchSpaceOffset);
		int quantityStartIndex = fileContent.indexOf(
				quantitySearchPatternStart, quantitySearchSpaceOffset);

		while (dateStartIndex != -1) {

			dateStartIndex += dateSearchPatternStart.length();
			quantityStartIndex += quantitySearchPatternStart.length();

			int dateEndIndex = fileContent.indexOf(dateSearchPatternEnd,
					dateStartIndex);

			String rawDateString = fileContent.substring(dateStartIndex,
					dateEndIndex);

			rawDateString.trim();
			Date date;
			try {
				date = inFormat.parse(rawDateString);
			} catch (ParseException e) {
				System.out.println(rawDateString);
				throw new RuntimeException("Error parsing input data.", e);
			}

			int quantityEndIndex = fileContent.indexOf(
					quantitySearchPatternEnd, quantityStartIndex);
			String quantityString = fileContent.substring(quantityStartIndex,
					quantityEndIndex);
			quantityString.trim();

			Review review = new Review();
			review.setTimestamp(outFormat.format(date));
			review.setQuantitativeReview(quantityString);
			rating.getReviews().add(review);

			dateSearchSpaceOffset = dateEndIndex + 6;
			dateStartIndex = fileContent.indexOf(dateSearchPatternStart,
					dateSearchSpaceOffset);
			quantitySearchSpaceOffset = dateSearchSpaceOffset;
			quantityStartIndex = fileContent.indexOf(
					quantitySearchPatternStart, quantitySearchSpaceOffset);

		}
	}

}
