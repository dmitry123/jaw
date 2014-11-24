package models;

import Core.Environment;
import Sql.CortegeRow;
import Core.Model;

import java.sql.ResultSet;

/**
 * Created by Savonin on 2014-11-02
 */
public class Widget extends Model<Widget.Row> {

	/**
	 * Basic constructor with helper and table's name as arguments
	 *
	 * @param environment
	 * 		- MySql's helper object
	 */
	public Widget(Environment environment) {
		super(environment, "widget");
	}

	/**
	 * @param result
	 * 		- Current cortege from query
	 * @return - Created row from bind
	 */
	@Override
	public Row createFromSet(ResultSet result) throws Exception {
		return null;
	}

	/**
	 * Insert someone in database, where 't' is future object
	 *
	 * @param row
	 * 		Some object, which implements CollageProtocol
	 * @throws Exception
	 */
	@Override
	public Row add(Row row) throws Exception {
		return row;
	}

	class Row extends CortegeRow {

		/**
		 * @param id
		 * 		Identifier
		 */
		public Row(int id) {
			super(id);
		}
	}
}
