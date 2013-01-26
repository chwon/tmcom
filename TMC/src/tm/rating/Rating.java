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

package tm.rating;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tm.datasource.RatingXmlIO;

public class Rating {

	private ReviewedEntity reviewedEntity = new ReviewedEntity();
	private QuantitativeReviewScheme quantitativeReviewScheme = new QuantitativeReviewScheme();
	private WebsiteInfo websiteInfo = new WebsiteInfo();
	private List<Review> reviews = new ArrayList<Review>();
	
	private Map<String, Review> idToRev = new HashMap<String, Review>();
	
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

	public ReviewedEntity getReviewedEntity() {
		return reviewedEntity;
	}

	public QuantitativeReviewScheme getQuantitativeReviewScheme() {
		return quantitativeReviewScheme;
	}

	public WebsiteInfo getWebsiteInfo() {
		return websiteInfo;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviewedEntity(ReviewedEntity reviewedEntity) {
		this.reviewedEntity = reviewedEntity;
	}

	public void setQuantitativeReviewScheme(
			QuantitativeReviewScheme quantitativeReviewScheme) {
		this.quantitativeReviewScheme = quantitativeReviewScheme;
	}

	public void setWebsiteInfo(WebsiteInfo websiteInfo) {
		this.websiteInfo = websiteInfo;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
	
	public void toXml(String filename) throws FileNotFoundException {
		RatingXmlIO.INSTANCE.writeRatingXml(this, filename);
	}
	
	public void toXml(OutputStream output) {
		RatingXmlIO.INSTANCE.writeRatingXml(this, output);
	}

}
