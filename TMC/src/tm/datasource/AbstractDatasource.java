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
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import tm.rating.Rating;
import tm.rating.Review;

public abstract class AbstractDatasource implements Datasource {

	protected String propertiesFile = "/resources/datasource.properties";
	
	protected Rating rating;

	protected String ratingBaseSiteOriginalRef;
	
	protected String ratingBaseSiteFile;
	protected String ratingBaseSitePrefix;
	protected String ratingBaseSiteNormalizedUrl;
	
	protected String patternLinkZoneStart;
	protected String patternLinkZoneEnd;
	protected String patternLinkStart;
	protected String patternLinkEnd;
	protected String patternLinkPrefix;

	protected String encoding = "UTF-8";
	
	protected int MAX_THREADS_DEFAULT = 5;
	protected int MAX_THREADS = 5;
	
	public AbstractDatasource() {
		Properties prop = new Properties();
		try {
			prop.load(this.getClass().getResourceAsStream(
					propertiesFile));
			String maxThreadsString = prop.getProperty("MAX_THREADS");
			MAX_THREADS = Integer.parseInt(maxThreadsString);
		} catch (IOException e) {
			// do nothing
		}
	}

	public ReturnCode loadData(String ref) {
		
		long startTime = System.currentTimeMillis();

		rating = new Rating();

		try {
			
			ratingBaseSiteOriginalRef = ref;

			URL url = new URL(ref);
			ratingBaseSitePrefix = url.getProtocol() + "://"
					+ url.getAuthority();
			ratingBaseSiteFile = url.getFile();
			ratingBaseSiteNormalizedUrl = normalizeUrl(ref);

			if (robotsDenied(ratingBaseSitePrefix)) {
				return ReturnCode.ROBOTS_TXT;
			}

			String webpageAsString = urlToString(ratingBaseSiteNormalizedUrl,
					encoding);
			fillHeaderData(webpageAsString);
			traverseReviewData(generateReviewUrls(ratingBaseSiteNormalizedUrl));

		} catch (IOException e) {

			return ReturnCode.CONNECTION_ERROR;

		}
		
		long elapsed = System.currentTimeMillis() - startTime;
		System.out.println("Loading time: " + (elapsed / 1000));

		return ReturnCode.OK;

	}
	
	protected abstract void fillHeaderData(String fileContent);
	
	protected abstract List<Review> fillReviewData(String fileContent);
	
	protected abstract Set<String> generateReviewUrls(String entryPageUrl) throws IOException;
	
	protected void traverseReviewData(Set<String> reviewUrls) throws IOException {
		
		final Object lock = new Object();

		Set<String> urlsToProcess = new HashSet<String>();
		Set<String> urlsProcessed = new HashSet<String>();
		Set<PageLoader> workers = new HashSet<PageLoader>();

		urlsToProcess.addAll(reviewUrls);

		while (!(urlsToProcess.isEmpty() && workers.isEmpty())) {

			int threadCnt = workers.size();
			
			// Start new workers
			Iterator<String> urlIter = urlsToProcess.iterator();
			while (urlIter.hasNext() && (threadCnt < MAX_THREADS)) {
				String url = urlIter.next();
				PageLoader worker = new PageLoader(url, false, lock);
				worker.start();
				workers.add(worker);
				threadCnt++;
				urlsProcessed.add(url);
				urlIter.remove();
			}
			
			// Sleep until a worker has finished
			try {
				synchronized (lock) {
					lock.wait(500);
				}
			} catch (InterruptedException e) {
				// do nothing
			}

			// Process workers' results
			Iterator<PageLoader> wIter = workers.iterator();
			while (wIter.hasNext()) {
				PageLoader worker = wIter.next();
				if (!worker.isAlive()) {
					rating.getReviews().addAll(worker.getReviews());
					for (String r : worker.getReferrers()) {
						if (!urlsProcessed.contains(r)) {
							urlsToProcess.add(r);
						}
					}
					wIter.remove();
				}
			}

		}
	}

	@Override
	public Rating getRating() {
		return rating;
	}
	
	public static String normalizeUrl(String urlString) throws MalformedURLException {
		URL url = new URL(urlString);
		return url.getProtocol() + "://" + url.getAuthority() + url.getFile();
	}
	
	protected String sanitizeUrl(String urlString) {
		return urlString;
	}

	public static String urlToString(String urlString, String encoding)
			throws IOException {

		StringBuilder buf = new StringBuilder();

		try {

			URL url = new URL(urlString);
			URLConnection con = url.openConnection();
			con.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:17.0) Gecko/20100101 Firefox/17.0");

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
	
	
	protected class PageLoader extends Thread {

		private String url;
		private Set<String> refs;
		private List<Review> revs;
		
		private Object listener;
		private boolean extractReferrers;

		public PageLoader(String url, boolean extractReferrers, Object listener) {
			super();
			this.url = url;
			this.extractReferrers = extractReferrers;
			this.listener = listener;
			refs = new HashSet<String>();
		}
		
		public PageLoader(String url, boolean extractReferrers) {
			this(url, extractReferrers, null);
		}

		public void run() {

			System.out.println("Processing " + url);

			String webpageAsString = "";

			try {
				webpageAsString = urlToString(url, encoding);
			} catch (IOException e) {
				e.printStackTrace();
			}

			revs = fillReviewData(webpageAsString);
			
			if (extractReferrers) {
				extractRefs(webpageAsString);
			}
			
			if (listener != null) {
				synchronized (listener) {
					listener.notifyAll();
				}
			}

		}
		
		private void extractRefs(String fileContent) {

			int linkZoneStartIndex = fileContent.indexOf(patternLinkZoneStart)
					+ patternLinkZoneStart.length();
			int linkZoneEndIndex = fileContent.indexOf(patternLinkZoneEnd,
					linkZoneStartIndex);

			if ((linkZoneStartIndex != -1) && (linkZoneEndIndex != -1)) {

				String linkZone = fileContent.substring(linkZoneStartIndex,
						linkZoneEndIndex);

				int nextReferrerStartIndex = linkZone.indexOf(patternLinkStart,
						0);

				while (nextReferrerStartIndex != -1) {
					nextReferrerStartIndex += patternLinkStart.length();

					int nextReferrerEndIndex = linkZone.indexOf(patternLinkEnd,
							nextReferrerStartIndex);

					String nextReferrer = linkZone.substring(
							nextReferrerStartIndex, nextReferrerEndIndex);
					String refUrl = "";
					try {
						refUrl = sanitizeUrl(normalizeUrl(patternLinkPrefix
								+ nextReferrer));
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
					refs.add(refUrl);

					nextReferrerStartIndex = linkZone.indexOf(patternLinkStart,
							nextReferrerEndIndex + patternLinkEnd.length());
				}

			}
		}

		public Set<String> getReferrers() {
			return refs;
		}

		public List<Review> getReviews() {
			return revs;
		}

	}

}
