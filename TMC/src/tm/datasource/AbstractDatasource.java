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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import tm.rating.Rating;

public abstract class AbstractDatasource implements Datasource {

	protected Rating rating;

	protected String ratingBaseSiteFullUrlString;
	protected String ratingBaseSiteFile;
	protected String ratingBaseSitePrefix;

	protected String encoding = "UTF-8";

	protected abstract void fillHeaderData(String fileContent);

	protected abstract void traverseReviewData(String entryUrl)
			throws IOException;

	public ReturnCode loadData(String ref) {

		rating = new Rating();

		try {

			ratingBaseSiteFullUrlString = ref;

			URL url = new URL(ratingBaseSiteFullUrlString);
			ratingBaseSitePrefix = url.getProtocol() + "://"
					+ url.getAuthority();
			ratingBaseSiteFile = url.getFile();

			if (robotsDenied(ratingBaseSitePrefix)) {
				return ReturnCode.ROBOTS_TXT;
			}

			String webpageAsString = urlToString(ratingBaseSiteFullUrlString,
					encoding);
			fillHeaderData(webpageAsString);
			traverseReviewData(ref);

		} catch (IOException e) {

			return ReturnCode.CONNECTION_ERROR;

		}

		return ReturnCode.OK;

	}

	@Override
	public Rating getRating() {
		return rating;
	}

	public static String fileToString(String filename, String encoding) {

		InputStream is = ClassLoader.getSystemClassLoader()
				.getResourceAsStream(filename);
		Scanner s = new Scanner(is, encoding).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";

	}

	public static String fileToString(String filename) {

		return fileToString(filename, "UTF-8");

	}

	public static String urlToString(String urlString, String encoding)
			throws IOException {

		StringBuilder buf = new StringBuilder();

		try {

			URL url = new URL(urlString);
			URLConnection con = url.openConnection();

			Reader r = new InputStreamReader(con.getInputStream(), encoding);

			while (true) {
				int ch = r.read();
				if (ch < 0)
					break;
				buf.append((char) ch);
			}

		} catch (IOException e) {

			throw new IOException("URL " + urlString + " could not be opened.",
					e);

		}

		return buf.toString();

	}

	public static boolean urlExists(String urlString) {

		boolean result = false;

		try {

			URL url = new URL(urlString);
			HttpURLConnection httpConn = (HttpURLConnection) url
					.openConnection();
			httpConn.setRequestMethod("GET");
			// Mimic request from browser
			httpConn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:17.0) Gecko/20100101 Firefox/17.0");
			httpConn.connect();

			int responseCode = httpConn.getResponseCode();
			// Check for 2xx response
			if ((responseCode % 100) == 2) {
				result = true;
			}

		} catch (IOException e) {
			// Drop Exception
		}

		return result;
	}

	protected boolean robotsDenied(String domain) {

		boolean result = false;

		// check robots.txt here

		return result;
	}

}
