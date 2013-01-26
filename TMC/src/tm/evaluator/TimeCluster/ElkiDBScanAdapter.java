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

package tm.evaluator.TimeCluster;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import tm.rating.Rating;
import tm.rating.Review;

import de.lmu.ifi.dbs.elki.algorithm.clustering.DBSCAN;
import de.lmu.ifi.dbs.elki.data.Cluster;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.DoubleVector;
import de.lmu.ifi.dbs.elki.data.IntegerVector;
import de.lmu.ifi.dbs.elki.data.model.Model;
import de.lmu.ifi.dbs.elki.data.type.SimpleTypeInformation;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.StaticArrayDatabase;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.datasource.DatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.filter.FixedDBIDsFilter;
import de.lmu.ifi.dbs.elki.distance.distancevalue.IntegerDistance;
import de.lmu.ifi.dbs.elki.index.IndexFactory;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.ObjectListParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.ObjectParameter;

public class ElkiDBScanAdapter {

	private Database db;
	private Clustering<Model> clustering;
	private Collection<ReviewCluster> clusters;
	
	private Relation payloadRelation;
	private Relation labelRelation;
	
	private Rating rating;
	
	private InputStream timeValueStream;
	private String earliestDate;
	private String latestDate;
	private String earliestTimestamp;
	private String latestTimestamp;
	private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMM dd, yyyy", Locale.US);

	
	public ElkiDBScanAdapter(Rating rating) {
		this.rating = rating;
		createTimeValueStream();
		initDb();
	}

	private void initDb() {

		DatabaseConnection databaseConnection = null;
		Collection<IndexFactory<?, ?>> indexFactories = null;

		final OptionID INDEX_ID = OptionID.getOrCreateOptionID("db.index",
				"Database indexes to add.");

		ListParameterization dbParams = new ListParameterization();
		dbParams.addParameter(ExternalInputStreamDatabaseConnection.INPUT_ID,
				timeValueStream);

		List<Class<?>> filterlist = new ArrayList<Class<?>>();
		filterlist.add(FixedDBIDsFilter.class);

		dbParams.addParameter(ExternalInputStreamDatabaseConnection.FILTERS_ID,
				filterlist);
		dbParams.addParameter(FixedDBIDsFilter.IDSTART_ID, 1);

		final ObjectParameter<DatabaseConnection> dbcP = new ObjectParameter<DatabaseConnection>(
				OptionID.DATABASE_CONNECTION, DatabaseConnection.class,
				ExternalInputStreamDatabaseConnection.class);
		if (dbParams.grab(dbcP)) {
			databaseConnection = dbcP.instantiateClass(dbParams);
		}

		final ObjectListParameter<IndexFactory<?, ?>> indexFactoryP = new ObjectListParameter<IndexFactory<?, ?>>(
				INDEX_ID, IndexFactory.class, true);
		if (dbParams.grab(indexFactoryP)) {
			indexFactories = indexFactoryP.instantiateClasses(dbParams);
		}

		db = new StaticArrayDatabase(databaseConnection, indexFactories);

		db.initialize();

	}
	
	public Collection<ReviewCluster> run(int epsilon, int minpts) {

		// set parameters for dbscan
		ListParameterization params = new ListParameterization();
		// epsilon set to one year (in seconds)
		params.addParameter(DBSCAN.EPSILON_ID, epsilon);
		// minimum number of points to form a cluster
		params.addParameter(DBSCAN.MINPTS_ID, minpts);

		DBSCAN<IntegerVector, IntegerDistance> dbscan = ClassGenericsUtil
				.parameterizeOrAbort(DBSCAN.class, params);

		clustering = dbscan.run(db);

		genClusterObjects();

		return clusters;

	}
	
	private void genClusterObjects() {

		clusters = new ArrayList<ReviewCluster>();
		
		List<Cluster<Model>> elkiClusters = clustering.getToplevelClusters();
		
		Collection<Relation<?>> elkiRelations = db.getRelations();
		for (Relation<?> r : elkiRelations) {
			String relId = r.getLongName();
			if (relId.contains("Vector")) {
				payloadRelation = r;
			}
			if (relId.contains("LabelList")) {
				labelRelation = r;
			}
		}
		
		if (payloadRelation == null || labelRelation == null) {
			throw new RuntimeException("Required relation missing in database.");
		}

		for (Cluster<Model> c : elkiClusters) {
			
			ReviewCluster cluster = new ReviewCluster();
			cluster.setName(c.getNameAutomatic());
			
			Iterator<DBID> iter = c.getIDs().iterator();
			while (iter.hasNext()) {
				DBID dbid = iter.next();
				DoubleVector entry = (DoubleVector) payloadRelation.get(dbid);
				String revId = labelRelation.get(dbid).toString();
				Review rev = rating.reviewForId(revId);
				cluster.put(rev, new Integer(entry.intValue(1)));
			}
			
			clusters.add(cluster);

		}
		
	}
	
	private void createTimeValueStream() {
		
		try {
			Date tempEarliest = null;
			Date tempLatest = null;
			if (rating.getReviews().get(0) != null) {
				tempEarliest = dateTimeFormat.parse(rating.getReviews().get(0)
						.getTimestamp());
				tempLatest = dateTimeFormat.parse(rating.getReviews().get(0)
						.getTimestamp());
			}

			StringBuffer sb = new StringBuffer();

			for (Review rev : rating.getReviews()) {

				Date timestamp = dateTimeFormat.parse(rev.getTimestamp());
				long tsSeconds = timestamp.getTime() / 1000L;
				String tsString = Long.toString(tsSeconds);
				sb.append(tsString + " " + rev.getId() + "\n");
				
				if (timestamp.before(tempEarliest)) {
					tempEarliest = timestamp;
				}
				if (timestamp.after(tempLatest)) {
					tempLatest = timestamp;
				}

			}

			timeValueStream = new ByteArrayInputStream(sb.toString().getBytes(
					"UTF-8"));
			
			earliestDate = dateFormat.format(tempEarliest);
			latestDate = dateFormat.format(tempLatest);
			
			earliestTimestamp = Long.toString(tempEarliest.getTime() / 1000L);
			latestTimestamp = Long.toString(tempLatest.getTime() / 1000L);

		} catch (Exception e) {
			throw new IllegalArgumentException(
					"TimeClusterEvaluator: Input processing error.");
		}

	}
	
	
	public String getEarliestTimestamp() {
		return earliestTimestamp;
	}
	
	
	public String getLatestTimestamp() {
		return latestTimestamp;
	}
	

	public void printClusteringInfo() {

		List<Cluster<Model>> clusters = clustering.getToplevelClusters();

		System.out.println("Detected " + clusters.size() + " clusters:");

		for (Cluster<Model> c : clusters) {
			System.out.println("  - " + c.getNameAutomatic() + "; noise = "
					+ c.isNoise() + "; hierarchical = " + c.isHierarchical()
					+ "; size = " + c.size());
		}

		for (Cluster<Model> c : clusters) {

			if (!c.isNoise()) {
				DBIDs dbids = c.getIDs();
				System.out.println("dbids.size = " + dbids.size());
				Iterator<DBID> iter = c.getIDs().iterator();
				while (iter.hasNext()) {
					DBID dbid = iter.next();
					printDbLine(dbid);
				}
			}
		}
	}

	private void printDbLine(DBID dbid) {
		Collection<Relation<?>> relations = db.getRelations();

		for (Relation<?> r : relations) {
			System.out.print(r.get(dbid) + "\t");
		}
		System.out.println();
	}

	public void printRelationInfo() {
		Collection<Relation<?>> relations = db.getRelations();

		for (Relation<?> r : relations) {
			System.out.println(r.getLongName());
			System.out.println(r.getShortName());
			SimpleTypeInformation<?> typeInfo = r.getDataTypeInformation();
			System.out.println(typeInfo.getLabel());
		}
	}

}