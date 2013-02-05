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
import java.util.ArrayList;
import java.util.List;

import tm.datasource.RatingXmlIO;

public class RatingSheet {
	
	private List<Rating> ratings = new ArrayList<Rating>();

	public List<Rating> getRatings() {
		return ratings;
	}
	
	public void toXml(String filename) throws FileNotFoundException {
		RatingXmlIO.INSTANCE.writeRatingXml(this, filename);
	}
	
	public void toXml(OutputStream output) {
		RatingXmlIO.INSTANCE.writeRatingXml(this, output);
	}
	
}
