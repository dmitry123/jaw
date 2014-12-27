package models;

import Core.*;

import Sql.CortegeProtocol;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Savonin on 2014-12-04
 */
public class System extends Model {

	/**
	 * Basic constructor with helper and table's name as arguments
	 * @param environment - Current environment
	 */
	public System(Environment environment) {
		super(environment, null);
	}

	/**
	 * @param result - Current cortege from query
	 * @return - Created row from bind
	 * @throws Exception
	 */
	@Override
	public CortegeProtocol createFromSet(ResultSet result) throws Exception {
		return null;
	}
}
