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

import java.io.InputStream;
import java.util.List;

import de.lmu.ifi.dbs.elki.datasource.FileBasedDatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.InputStreamDatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.filter.ObjectFilter;
import de.lmu.ifi.dbs.elki.datasource.parser.Parser;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.WrongParameterValueException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.ObjectParameter;


public class ExternalInputStreamDatabaseConnection extends FileBasedDatabaseConnection {
	
	public ExternalInputStreamDatabaseConnection(List<ObjectFilter> filters,
			Parser parser, InputStream in) {
		super(filters, parser, in);
	}
	
	
	public static final OptionID INPUT_ID = OptionID.getOrCreateOptionID("instream", "The name of the input stream to be parsed.");
	

	/**
	 * Parameterization class.
	 */
	public static class Parameterizer extends
			InputStreamDatabaseConnection.Parameterizer {
		protected InputStream inputStream;

		@Override
		protected void makeOptions(Parameterization config) {
			// Add the input file first, for usability reasons.
			final ObjectParameter<InputStream> inputParam = new ObjectParameter<InputStream>(INPUT_ID,
					InputStream.class, InputStream.class);

			if (config.grab(inputParam)) {
				try {
					inputStream = (InputStream) inputParam.getGivenValue();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					config.reportError(new WrongParameterValueException(
							inputParam, inputParam.getValue().toString(), e));
					inputStream = null;
				}
			}

			super.makeOptions(config);
		}

		@Override
		protected FileBasedDatabaseConnection makeInstance() {
			return new FileBasedDatabaseConnection(filters, parser, inputStream);
		}

	}
	
	
	
}
