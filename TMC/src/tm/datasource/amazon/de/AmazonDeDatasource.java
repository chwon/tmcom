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

package tm.datasource.amazon.de;

import java.text.SimpleDateFormat;
import java.util.Locale;

import tm.datasource.amazon.com.AmazonComDatasource;

public class AmazonDeDatasource extends AmazonComDatasource {

	public AmazonDeDatasource() {
		
		super();

		websiteInfoName = "Amazon.de";
		websiteInfoBaseUrl = "www.amazon.de";
		
		titleSearchPatternStart = "<span id=\"btAsinTitle\">";
		titleSearchPatternEnd = "</span>";
		
		reviewpageSearchPatternStart = "<span>Alle Rezensionen anzeigen</span></span>&nbsp;</a></span></span>(<a href=\"";
		reviewpageSearchPatternEnd = "\" >";
		
		reviewPageOffsetString = "<table id=\"productReviews\"";
		
		nextPageSearchPatternStart = "<a href=\"";
		nextPageSearchPatternEnd = "\" >Weiter &rsaquo;</a>";
		
		inFormat = new SimpleDateFormat("dd. MMMMM yyyy", Locale.GERMANY);
		
		dateSearchPatternStart = "</b>, <nobr>";
		dateSearchPatternEnd = "</nobr></span>";
		quantitySearchPatternStart = " von 5 Sternen\" ><span>";
		quantitySearchPatternEnd = " von 5 Sternen</span></span>";
		
	}
	
}
