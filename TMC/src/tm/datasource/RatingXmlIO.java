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

package tm.datasource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.NoSuchElementException;

import tm.rating.ConnectionProperties;
import tm.rating.QuantitativeReviewScheme;
import tm.rating.Rating;
import tm.rating.RatingSheet;
import tm.rating.Review;
import tm.rating.ReviewedEntity;
import tm.rating.ReviewerInfo;
import tm.rating.WebsiteInfo;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public enum RatingXmlIO {

	INSTANCE;

	private XStream xstream;

	private RatingXmlIO() {

		xstream = new XStream(new StaxDriver());

		xstream.alias("RatingSheet", RatingSheet.class);
		xstream.alias("Rating", Rating.class);
		xstream.alias("Review", Review.class);

		xstream.addImplicitCollection(RatingSheet.class, "ratings");

		xstream.aliasField("ReviewedEntity", Rating.class, "reviewedEntity");
		xstream.aliasField("QuantitativeReviewScheme", Rating.class,
				"quantitativeReviewScheme");
		xstream.aliasField("WebsiteInfo", Rating.class, "websiteInfo");
		xstream.addImplicitCollection(Rating.class, "reviews");

		xstream.aliasField("Name", ReviewedEntity.class, "name");
		xstream.aliasField("Description", ReviewedEntity.class, "description");
		xstream.aliasField("MarketEntry", ReviewedEntity.class, "marketEntry");
		xstream.aliasField("OverallQuantiativeReview", ReviewedEntity.class,
				"overallQuantiativeReview");

		xstream.aliasField("UnitDescription", QuantitativeReviewScheme.class,
				"unitDescription");
		xstream.aliasField("MinValue", QuantitativeReviewScheme.class,
				"minValue");
		xstream.aliasField("MaxValue", QuantitativeReviewScheme.class,
				"maxValue");
		xstream.aliasField("Step", QuantitativeReviewScheme.class, "step");
		xstream.aliasField("ReviewSchemeOrder", QuantitativeReviewScheme.class,
				"reviewSchemeOrder");

		xstream.aliasField("Name", WebsiteInfo.class, "name");
		xstream.aliasField("BaseUrl", WebsiteInfo.class, "baseUrl");
		xstream.aliasField("Organization", WebsiteInfo.class, "organization");
		xstream.aliasField("Country", WebsiteInfo.class, "country");
		xstream.aliasField("Region", WebsiteInfo.class, "region");
		xstream.aliasField("City", WebsiteInfo.class, "city");

		xstream.useAttributeFor(Review.class, "id");
		xstream.aliasField("id", Review.class, "id");
		xstream.aliasField("Title", Review.class, "title");
		xstream.aliasField("Timestamp", Review.class, "timestamp");
		xstream.aliasField("QuantitativeReview", Review.class,
				"quantitativeReview");
		xstream.aliasField("ReviewText", Review.class, "reviewText");
		xstream.aliasField("ReviewUrl", Review.class, "reviewUrl");
		xstream.aliasField("ReviewerInfo", Review.class, "reviewerInfo");
		xstream.aliasField("ConnectionProperties", Review.class,
				"connectionProperties");

		xstream.aliasField("Username", ReviewerInfo.class, "username");
		xstream.aliasField("FirstName", ReviewerInfo.class, "firstName");
		xstream.aliasField("MiddleName", ReviewerInfo.class, "middleName");
		xstream.aliasField("LastName", ReviewerInfo.class, "lastName");
		xstream.aliasField("Country", ReviewerInfo.class, "country");
		xstream.aliasField("Region", ReviewerInfo.class, "region");
		xstream.aliasField("City", ReviewerInfo.class, "city");

		xstream.aliasField("HostName", ConnectionProperties.class, "hostName");
		xstream.aliasField("IpAddress", ConnectionProperties.class, "ipAddress");
		xstream.aliasField("ISPName", ConnectionProperties.class, "ispName");
		xstream.aliasField("Country", ConnectionProperties.class, "country");
		xstream.aliasField("Region", ConnectionProperties.class, "region");
		xstream.aliasField("City", ConnectionProperties.class, "city");

	}

	public Rating readRatingXml(String filename) throws FileNotFoundException {

		FileInputStream inStream = new FileInputStream(filename);

		return readRatingXml(inStream);

	}

	public Rating readRatingXml(InputStream input) {

		RatingSheet ratingSheet;

		ratingSheet = (RatingSheet) xstream.fromXML(input);

		if (ratingSheet.getRatings().isEmpty()) {
			throw new NoSuchElementException("No Rating in RatingXML file.");
		}

		return ratingSheet.getRatings().get(0);

	}

	public void writeRatingXml(Rating rating, String filename) throws FileNotFoundException {
		
		RatingSheet ratingSheet = new RatingSheet();
		ratingSheet.getRatings().add(rating);
		
		writeRatingXml(ratingSheet, filename);

	}

	public void writeRatingXml(RatingSheet ratingSheet, String filename) throws FileNotFoundException {
		
			FileOutputStream out = new FileOutputStream(filename);
			writeRatingXml(ratingSheet, out);

	}

	public void writeRatingXml(Rating rating, OutputStream output) {

		RatingSheet ratingSheet = new RatingSheet();
		ratingSheet.getRatings().add(rating);

		writeRatingXml(ratingSheet, output);

	}

	public void writeRatingXml(RatingSheet ratingSheet, OutputStream output) {

		xstream.toXML(ratingSheet, output);

	}

}
