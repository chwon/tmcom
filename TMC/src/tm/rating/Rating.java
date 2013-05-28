/*
 This file is part of TrustMeasure.
 
 Copyright 2013 Claus Wonnemann

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

package tm.rating;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tm.datasource.RatingXmlIO;

/**
 * The <code>Rating</code> class represents a set of individual reviews for
 * an entity.
 * 
 * @author chwon
 *
 */

public class Rating {

	private ReviewedEntity reviewedEntity = new ReviewedEntity();
	private QuantitativeReviewScheme quantitativeReviewScheme = new QuantitativeReviewScheme();
	private WebsiteInfo websiteInfo = new WebsiteInfo();
	private List<Review> reviews = new ArrayList<Review>();
	
	private Map<String, Review> idToRev = new HashMap<String, Review>();

	public static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss");

	/**
	 * Returns the <code>Review</code> instance that corresponds to <code>id</code>. 
	 * 
	 * @return
	 */
	public Review reviewForId(String id) {
		if (idToRev.size() != reviews.size()) {
			refreshMap();
		}
		return idToRev.get(id);
	}
	
	private void refreshMap() {
		idToRev.clear();
		for (Review rev : reviews) {
			idToRev.put(rev.getId(), rev);
		}
	}

	/**
	 * Returns the <code>ReviewedEntity</code> instance that is associated to the rating.
	 * 
	 * @return
	 */
	public ReviewedEntity getReviewedEntity() {
		return reviewedEntity;
	}
	
	/**
	 * Returns the <code>QuantitativeReviewScheme</code> instance that is associated to the rating.
	 * 
	 * @return
	 */
	public QuantitativeReviewScheme getQuantitativeReviewScheme() {
		return quantitativeReviewScheme;
	}

	/**
	 * Returns the <code>WebsiteInfo</code> instance that is associated to the rating.
	 * 
	 * @return
	 */
	public WebsiteInfo getWebsiteInfo() {
		return websiteInfo;
	}
	
	/**
	 * Returns the list of <code>Review</code> instances associated to the rating.
	 * 
	 * @return
	 */
	public List<Review> getReviews() {
		return reviews;
	}

	/**
	 * Sets the <code>ReviewedEntity</code> instance that is associated to the rating.
	 * 
	 * @param reviewedEntity
	 */
	public void setReviewedEntity(ReviewedEntity reviewedEntity) {
		this.reviewedEntity = reviewedEntity;
	}

	/**
	 * Sets the <code>QuantitativeReviewScheme</code> instance that is associated to the rating.
	 * 
	 * @param quantitativeReviewScheme
	 */
	public void setQuantitativeReviewScheme(
			QuantitativeReviewScheme quantitativeReviewScheme) {
		this.quantitativeReviewScheme = quantitativeReviewScheme;
	}

	/**
	 * Sets the <code>WebsiteInfo</code> instance that is associated to the rating.
	 * 
	 * @param websiteInfo
	 */
	public void setWebsiteInfo(WebsiteInfo websiteInfo) {
		this.websiteInfo = websiteInfo;
	}

	/**
	 * Sets the list of <code>Review</code> instances that is associated to the rating.
	 * 
	 * @param reviews
	 */
	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
	
	/**
	 * Creates an XML representation for the rating and writes it to the specified file.
	 * 
	 * @param filename
	 * @throws FileNotFoundException
	 */
	public void toXml(String filename) throws FileNotFoundException {
		RatingXmlIO.INSTANCE.writeRatingXml(this, filename);
	}
	
	/**
	 * Creates an XML representation for the rating and writes it to the specified output stream.
	 * 
	 * @param output
	 */
	public void toXml(OutputStream output) {
		RatingXmlIO.INSTANCE.writeRatingXml(this, output);
	}
	
	/**
	 * Sorts the rating's list of reviews according to their timestamps in ascending order.
	 */
	public void sortReviewsByDate() {
		Collections.sort(reviews, Review.getTimeComparator());
	}

}
