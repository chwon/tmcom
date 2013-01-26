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

import java.util.UUID;

public class Review {

	private String id = UUID.randomUUID().toString();
	private String title = "";
	private String timestamp = "";
	private String quantitativeReview = "";
	private String reviewText = "";
	private String reviewUrl = "";
	private ReviewerInfo reviewerInfo = new ReviewerInfo();
	private ConnectionProperties connectionProperties = new ConnectionProperties();

	public String getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public String getQuantitativeReview() {
		return quantitativeReview;
	}

	public String getReviewText() {
		return reviewText;
	}

	public String getReviewUrl() {
		return reviewUrl;
	}

	public ReviewerInfo getReviewerInfo() {
		return reviewerInfo;
	}

	public ConnectionProperties getConnectionProperties() {
		return connectionProperties;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public void setQuantitativeReview(String quantitativeReview) {
		this.quantitativeReview = quantitativeReview;
	}

	public void setReviewText(String reviewText) {
		this.reviewText = reviewText;
	}

	public void setReviewUrl(String reviewUrl) {
		this.reviewUrl = reviewUrl;
	}

	public void setReviewerInfo(ReviewerInfo reviewerInfo) {
		this.reviewerInfo = reviewerInfo;
	}

	public void setConnectionProperties(
			ConnectionProperties connectionProperties) {
		this.connectionProperties = connectionProperties;
	}

}
