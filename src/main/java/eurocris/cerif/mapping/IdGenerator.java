package eurocris.cerif.mapping;

import java.util.Date;

/**
 * @author Nuno
 *
 * Generates unique IDs for XML elements
 */
public class IdGenerator {
	long lastId=0;
	
	public synchronized long generate(){
		long ret=new Date().getTime();
		while(ret<=lastId)
			ret++;
		lastId=ret;
		return ret;
	}
	public synchronized String generateString(){
		return String.valueOf(generate());
	}
	
	
}
