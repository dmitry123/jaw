package models;

import Core.Environment;
import Core.Model;
import Sql.CortegeProtocol;

import java.sql.ResultSet;

/**
 * Created by Savonin on 2014-11-24
 */
public class Index extends Model {

	/**
	 * Basic constructor with helper and table's name as arguments
	 * @param environment - Current environment
	 */
	public Index(Environment environment) {
		super(environment, null);
	}

	/**
	 * @param result - Current cortege from query
	 * @return - Created row from bind
	 * @throws Core.InternalError
	 */
	@Override
	public CortegeProtocol createFromSet(ResultSet result) throws Exception {
		return null;
	}

	/**
	 * Insert someone in database, where 't' is future object
	 * @param cortegeProtocol Some object, which implements CollageProtocol
	 * @throws Exception
	 */
	@Override
	public CortegeProtocol add(CortegeProtocol cortegeProtocol) throws Exception {
		return null;
	}
}
