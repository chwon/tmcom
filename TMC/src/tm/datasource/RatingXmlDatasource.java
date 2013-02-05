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

package tm.datasource;

import java.io.FileNotFoundException;
import java.io.InputStream;

import tm.rating.Rating;


public class RatingXmlDatasource implements Datasource {
	
	protected Rating rating;
	
	public RatingXmlDatasource(InputStream input) {
		
		rating = RatingXmlIO.INSTANCE.readRatingXml(input);
		
	}

	@Override
	public ReturnCode loadData(String ref) {
		
		try {
			
			rating = RatingXmlIO.INSTANCE.readRatingXml(ref);
			
		} catch (FileNotFoundException e) {
			
			return ReturnCode.FILE_NOT_FOUND;
					
		}
		
		return ReturnCode.OK;
		
	}

	@Override
	public Rating getRating() {
		return rating;
	}
	

}
