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

package tm.evaluator;

import java.util.Map;

import tm.datasource.Datasource;
import tm.rating.Rating;

public abstract class AbstractEvaluator implements Evaluator {

	protected Rating rating;

	public AbstractEvaluator() {
		super();
	}

	public AbstractEvaluator(Datasource datasource) {
		super();
		rating = datasource.getRating();
		doEvaluation();
	}
	
	public AbstractEvaluator(Datasource datasource, Map<String, String[]> params) {
		super();
		rating = datasource.getRating();
		setParameters(params);
		doEvaluation();
	}
	
	public ReturnCode loadData(Datasource datasource) {
		rating = datasource.getRating();
		return doEvaluation();
	}
	
	protected abstract ReturnCode doEvaluation();

	public Rating getRating() {
		return rating;
	}

}
