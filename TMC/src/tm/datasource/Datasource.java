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

import tm.rating.Rating;

public interface Datasource {
	
	public enum ReturnCode {
		OK,
		CONNECTION_ERROR,
		MALFORMED_URL,
		ROBOTS_TXT,
		FILE_NOT_FOUND,
		ERROR
	}

	public ReturnCode loadData(String ref);
	
	public Rating getRating();
	
}
