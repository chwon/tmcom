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

public class QuantitativeReviewScheme {

	private String unitDescription = "";
	private String minValue = "";
	private String maxValue = "";
	private String step = "";
	private String reviewSchemeOrder = "";

	public String getUnitDescription() {
		return unitDescription;
	}

	public String getMinValue() {
		return minValue;
	}

	public String getMaxValue() {
		return maxValue;
	}

	public String getStep() {
		return step;
	}

	public String getReviewSchemeOrder() {
		return reviewSchemeOrder;
	}

	public void setUnitDescription(String unitDescription) {
		this.unitDescription = unitDescription;
	}

	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public void setReviewSchemeOrder(String reviewSchemeOrder) {
		this.reviewSchemeOrder = reviewSchemeOrder;
	}

}
