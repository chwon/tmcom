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

package tm.evaluator.TimeCluster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import tm.rating.Review;

public class ReviewCluster extends HashMap<Review, Integer> {

	private static final long serialVersionUID = 8666133989639143168L;
	
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<Integer> getSortedValues() {
		List<Integer> valList = new ArrayList<Integer>(this.values());
		Collections.sort(valList);
		return valList;
	}
	
	public String toString() {
		return "Name: " + name + "; Size: " + size();
	}

}
