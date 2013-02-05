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

public class ReviewedEntity {

	private String name = "";
	private String description = "";
	private String marketEntry = "";
	private String overallQuantiativeReview = "";

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getMarketEntry() {
		return marketEntry;
	}

	public String getOverallQuantiativeReview() {
		return overallQuantiativeReview;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setMarketEntry(String marketEntry) {
		this.marketEntry = marketEntry;
	}

	public void setOverallQuantiativeReview(String overallQuantiativeReview) {
		this.overallQuantiativeReview = overallQuantiativeReview;
	}

}
