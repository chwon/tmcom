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

package tm.evaluator;

import java.util.Map;

import tm.datasource.Datasource;

public interface Evaluator {
	
	public enum ReturnCode {
		OK,
		ERROR
	}
	
	public static String placeholderDuration = "ELAPSEDTIME";
	
	public ReturnCode loadData(Datasource datasource);
	
	public void setParameters(Map<String, String[]> params);
	
	public void determineParameters();
	
	public String generateEvaluationHtmlPage() throws UnsupportedOperationException;
	
	public String generateEvaluationHtmlSnippet() throws UnsupportedOperationException;
	
	public String generateEvaluationNumericalValue() throws UnsupportedOperationException;
	
	public String generateEvaluationStringValue() throws UnsupportedOperationException;

}
