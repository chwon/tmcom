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

package tmcservlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tm.datasource.AbstractDatasource;
import tm.datasource.Datasource;
import tm.evaluator.Evaluator;

/**
 * Servlet implementation class TMcom
 */
@WebServlet("/TMcom")
public class TMcom extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final String evaluatorsPropFile = "/resources/evaluators.properties";
	private final String websitesPropFile = "/resources/websites.properties";
	private final String pageFrameLocationsPropFile = "/resources/pageframelocations.properties";

	private final Properties evaluatorsProp;
	private final Properties websitesProp;
	private final Properties pageFrameLocationsProp;

	private final String datasourceClassPrefix = "tm.datasource.";
	private final String evaluatorClassPrefix = "tm.evaluator.";

	private String responsePageFrame = "";
	private final String responsePageFramePlaceholder = "PLACEHOLDER";

	private String responseErrorPageFrame = "";
	private final String responseErrorPageFramePlaceholder = "ERRORPLACEHOLDER";

	private Map<Class, Evaluator> evaluatorPool;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TMcom() {
		super();

		evaluatorPool = new HashMap<Class, Evaluator>();

		evaluatorsProp = new Properties();
		websitesProp = new Properties();
		pageFrameLocationsProp = new Properties();
		try {
			evaluatorsProp.load(this.getClass().getResourceAsStream(
					evaluatorsPropFile));
			websitesProp.load(this.getClass().getResourceAsStream(
					websitesPropFile));
			pageFrameLocationsProp.load(this.getClass().getResourceAsStream(
					pageFrameLocationsPropFile));
		} catch (IOException e) {
			System.out.println("Loading of properties file failed.");
			e.printStackTrace();
		}

		try {
			responsePageFrame = AbstractDatasource.urlToString(
					pageFrameLocationsProp.getProperty("result"), "UTF-8");
			responseErrorPageFrame = AbstractDatasource.urlToString(
					pageFrameLocationsProp.getProperty("error"), "UTF-8");
		} catch (IOException e) {
			System.out.println("Loading of page frames failed.");
			e.printStackTrace();
		}

	}


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String reviewpage = request.getParameter("REVIEWPAGE");
		
		if (! reviewpage.contains("://")) {
			reviewpage = "http://" + reviewpage;
		}
		
		String hostName = new URL(reviewpage).getHost();

		String evaluatorName = evaluatorsProp.getProperty(request
				.getParameter("EVALUATOR"));
		String datasourceName = websitesProp.getProperty(hostName);

		String result = "";

		String responseBody = "";
		
		Map<String, String[]> params = null;
		if (request.getParameter("PARAMAUTOSET") == null) {
			params = request.getParameterMap();
		}

		if (evaluatorName == null) {
			responseBody = generateErrorPageBody("The evaluator could not be found.");
		} else if (datasourceName == null) {
			responseBody = generateErrorPageBody("There is no data extractor assigned to " + reviewpage + ".");
		} else {
			
			BigDecimal startTime = new BigDecimal(System.currentTimeMillis());
			
			responseBody = dispatchRequest(evaluatorName, datasourceName,
					reviewpage, params);
			
			BigDecimal elapsedTime = (new BigDecimal(System.currentTimeMillis())).subtract(startTime);
			String timeString = (elapsedTime.divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_UP)).toPlainString();
			responseBody = responseBody.replace(Evaluator.placeholderDuration, timeString + "s");
			
		}

		result = responsePageFrame.replace(responsePageFramePlaceholder,
				responseBody);

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(result);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	

	private String generateErrorPageBody(String errorMsg) {

		return "Error: " + errorMsg;

	}
	

	private String dispatchRequest(String evaluatorName, String datasourceName,
			String reviewSite, Map<String, String[]> evalParameters) {

		String responseBody = "The processing of your request failed.";

		try {

			Class evaluatorClass = Class.forName(evaluatorClassPrefix
					+ evaluatorName);
			Class datasourceClass = Class.forName(datasourceClassPrefix
					+ datasourceName);

			Datasource ds = (Datasource) datasourceClass.newInstance();
			if (ds.loadData(reviewSite) != Datasource.ReturnCode.OK) {
				return generateErrorPageBody("Loading of website data failed!");
			}

			Evaluator eval;

			if (evaluatorPool.get(evaluatorClass) != null) {
				eval = evaluatorPool.get(evaluatorClass);
			} else {
				eval = (Evaluator) evaluatorClass.newInstance();
				evaluatorPool.put(evaluatorClass, eval);
			}
			
			if (evalParameters != null) {
				eval.setParameters(evalParameters);
			} else {
				eval.determineParameters();
			}

			if (eval.loadData(ds) != Evaluator.ReturnCode.OK) {
				return generateErrorPageBody("Evaluation failed!");
			}

			responseBody = eval.generateEvaluationHtmlSnippet();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return responseBody;

	}

}
