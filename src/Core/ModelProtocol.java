package Core;

import Sql.CortegeProtocol;

import java.sql.ResultSet;

/**
 * Created by Savonin on 2014-11-22
 */
public interface ModelProtocol {

	/**
	 * That method will adapt just fetched from table
	 * result bind to it's basic cortege protocol
	 *
	 * @return - Adapted to CortegeProtocol row
	 */
	public CortegeProtocol createFromSet(ResultSet resultSet) throws Exception, Exception;
}
