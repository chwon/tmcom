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

package tm.datasource.amazon.com;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import tm.datasource.AbstractDatasource;
import tm.rating.Rating;
import tm.rating.Review;

public class AmazonComDatasource extends AbstractDatasource {

	protected String websiteInfoName = "Amazon.com";
	protected String websiteInfoBaseUrl = "www.amazon.com";

	protected String titleSearchPatternStart = "<meta name=\"title\" content=\"";
	protected String titleSearchPatternEnd = "\" />";

	protected String reviewpageSearchPatternStart = "<span>See all reviews</span></span>&nbsp;</a></span></span>(<a href=\"";
	protected String reviewpageSearchPatternEnd = "\" >";
	
	protected String lastReviewPagePatternStart = "&hellip; <a href=\"";
	protected String lastReviewPagePatternEnd = "\" >";
	
	protected String pageNumberPatternStart = "&pageNumber=";
	protected String pageNumberPatternEnd = "&";

	protected String reviewPageOffsetString = "<table id=\"productReviews\"";

	protected String nextPageSearchPatternStart = "<a href=\"";
	protected String nextPageSearchPatternEnd = "\" >Next &rsaquo;</a>";

	protected String dateSearchPatternStart = "</b>, <nobr>";
	protected String dateSearchPatternEnd = "</nobr></span>";
	protected String quantitySearchPatternStart = " out of 5 stars\" ><span>";
	protected String quantitySearchPatternEnd = " out of 5 stars</span></span>";

	protected SimpleDateFormat inFormat = new SimpleDateFormat(
			"MMMMM dd, yyyy", Locale.US);
	protected SimpleDateFormat outFormat = Rating.dateFormat;

	public AmazonComDatasource() {
		super();

		encoding = "ISO-8859-1";

		patternLinkZoneStart = "<span class=\"paging\">";
		patternLinkZoneEnd = "&rsaquo;";
		patternLinkStart = "<a href=\"";
		patternLinkEnd = "\" >";
		patternLinkPrefix = "";
	}

	@Override
	protected void fillHeaderData(String fileContent) {

		String reviewedEntityName = "";
		int startIndex = fileContent.indexOf(titleSearchPatternStart)
				+ titleSearchPatternStart.length();
		int endIndex = fileContent.indexOf(titleSearchPatternEnd, startIndex);

		if (startIndex < endIndex) {
			reviewedEntityName = fileContent.substring(startIndex, endIndex);
		}
		rating.getReviewedEntity().setName(reviewedEntityName);

		rating.getQuantitativeReviewScheme().setUnitDescription("Star");
		rating.getQuantitativeReviewScheme().setReviewSchemeOrder("ascending");
		rating.getQuantitativeReviewScheme().setMinValue("1.0");
		rating.getQuantitativeReviewScheme().setMaxValue("5.0");
		rating.getQuantitativeReviewScheme().setStep("1.0");

		rating.getWebsiteInfo().setName(websiteInfoName);
		rating.getWebsiteInfo().setBaseUrl(websiteInfoBaseUrl);

	}
	
	protected Set<String> generateReviewUrls(String entryPageUrl) throws IOException {
		
		// Search for URL of first review page
		String mainPageAsString = urlToString(entryPageUrl, encoding);
		int firstReviewpageUrlStartIndex = mainPageAsString
				.indexOf(reviewpageSearchPatternStart)
				+ reviewpageSearchPatternStart.length();
		int firstReviewpageUrlEndIndex = mainPageAsString.indexOf(
				reviewpageSearchPatternEnd, firstReviewpageUrlStartIndex);
		String firstReviewPageUrl = mainPageAsString.substring(
				firstReviewpageUrlStartIndex, firstReviewpageUrlEndIndex);
		
		

		Set<String> urlSet = new HashSet<String>();
		String firstPageUrl = firstReviewPageUrl;
		urlSet.add(firstPageUrl);

		String firstPageContent = urlToString(firstPageUrl,	encoding);
		
		int lastReviewPageStartIndex = firstPageContent.indexOf(lastReviewPagePatternStart);
		if (lastReviewPageStartIndex != -1) {
			lastReviewPageStartIndex += lastReviewPagePatternStart.length();;
			
			int lastReviewPageEndIndex = firstPageContent.indexOf(lastReviewPagePatternEnd, lastReviewPageStartIndex);
			String lastReviewPageUrl = firstPageContent.substring(lastReviewPageStartIndex, lastReviewPageEndIndex);
			
			int pageNumberIndexStart = lastReviewPageUrl.indexOf(pageNumberPatternStart) + pageNumberPatternStart.length();
			int pageNumberIndexEnd = lastReviewPageUrl.indexOf(pageNumberPatternEnd, pageNumberIndexStart);
			
			String lastPageNumberString = lastReviewPageUrl.substring(pageNumberIndexStart, pageNumberIndexEnd);
			String urlBeforePageNumber = lastReviewPageUrl.substring(0, pageNumberIndexStart);
			String urlAfterPageNumber = lastReviewPageUrl.substring(pageNumberIndexEnd);
			
			int lastPageNumber = Integer.parseInt(lastPageNumberString);
			
			for (int i = lastPageNumber; i >= 2; i--) {
				String urlToAdd = urlBeforePageNumber + i + urlAfterPageNumber;
				urlSet.add(urlToAdd);
			}
		}
			
		return urlSet;
	}

	protected List<Review> fillReviewData(String fileContent) {

		List<Review> revs = new ArrayList<Review>();

		int generalOffset = fileContent.indexOf(reviewPageOffsetString);

		int dateSearchSpaceOffset = generalOffset;
		int quantitySearchSpaceOffset = generalOffset;

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
			revs.add(review);

			dateSearchSpaceOffset = dateEndIndex
					+ dateSearchPatternEnd.length();
			dateStartIndex = fileContent.indexOf(dateSearchPatternStart,
					dateSearchSpaceOffset);
			quantitySearchSpaceOffset = quantityEndIndex
					+ quantitySearchPatternEnd.length();
			quantityStartIndex = fileContent.indexOf(
					quantitySearchPatternStart, quantitySearchSpaceOffset);

		}

		return revs;

	}
	
	protected String sanitizeUrl(String urlString) {
		return urlString.replace("_link_next_", "_link_")
						.replace("_link_prev_", "_link_")
						.replace("&showViewpoints=1", "&showViewpoints=0")
						.replace("dp_top_cm_cr_acr_txt", "cm_cr_pr_top_link_1");
	}

}
