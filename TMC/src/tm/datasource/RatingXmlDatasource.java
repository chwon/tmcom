package tm.datasource;

import java.io.FileNotFoundException;
import java.io.InputStream;

import tm.rating.Rating;


public class RatingXmlDatasource implements Datasource {
	
	protected Rating rating;
	
	public RatingXmlDatasource(InputStream input) {
		
		rating = RatingXmlIO.INSTANCE.readRatingXml(input);
		
	}

	@Override
	public ReturnCode loadData(String ref) {
		
		try {
			
			rating = RatingXmlIO.INSTANCE.readRatingXml(ref);
			
		} catch (FileNotFoundException e) {
			
			return ReturnCode.FILE_NOT_FOUND;
					
		}
		
		return ReturnCode.OK;
		
	}

	@Override
	public Rating getRating() {
		return rating;
	}
	

}
