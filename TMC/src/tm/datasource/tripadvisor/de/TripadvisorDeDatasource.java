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

package tm.datasource.tripadvisor.de;

import java.text.SimpleDateFormat;
import java.util.Locale;

import tm.datasource.tripadvisor.com.TripadvisorComDatasource;

public class TripadvisorDeDatasource extends TripadvisorComDatasource {

	public TripadvisorDeDatasource() {
		
		super();
		
		websiteInfoName = "Trip Advisor";
		websiteInfoBaseUrl = "www.tripadvisor.de";
		
		inFormat = new SimpleDateFormat("dd. MMMMM yyyy", Locale.GERMANY);
		
		dateSearchPatternStart = "<span class=\"ratingDate\">Bewertet am ";
		dateSearchPatternEnd = "</span";
		quantitySearchPatternStart = "<img class=\"sprite-ratings\" src=\"http://c1.tacdn.com/img2/x.gif\" alt=\"";
		quantitySearchPatternEnd = " von 5 Sternen\"";
		patternLinkPrefix = "http://" + websiteInfoBaseUrl;
	}
	
}