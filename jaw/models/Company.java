package models;

import Core.*;
import Sql.CortegeRow;
import Sql.Connection;
import Component.Model;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.ResultSet;
import java.util.Vector;

/**
 * Created by Savonin on 2014-11-02
 */
public class Company extends Model<Company.Row> {

	/**
	 * Basic constructor with helper and table's name as arguments
	 *
	 * @param environment - Current environment
	 * @throws Core.InternalError
	 */
	public Company(Environment environment) throws Core.InternalError {
		super(environment, "company");
	}

	/**
	 * @param result
	 * 		- Current cortege from query
	 * @return - Created row from bind
	 */
	@Override
	public Row createFromSet(ResultSet result) throws Exception {
		return new Row(
			result.getInt("id"),
			result.getString("name"),
			result.getString("bundle"),
			result.getInt("director_id")
		);
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
		execute("INSERT INTO company (name, bundle) VALUES(?, ?)",
			row.getName(), row.getBundle());
		row.changeID(last().getID());
//		if (row.getDirectorID() != 0) {
//			((Employee) getModelManager().getModel("employee"))
//				.fetchByID(row.getDirectorID()).changeCompanyID(row.getID());
//		}
		return row;
	}

	/**
	 * Check company for existence by it's name
	 *
	 * @param name - Company's name
	 * @return - Found company's cortege
	 * @throws Exception
	 */
	public boolean exists(String name) throws Exception {
		return fetchByName(name) != null;
	}

	/**
	 * Find company by it's name
	 *
	 * @param name - Company's name
	 * @return - Found company's cortege
	 * @throws Exception
	 */
	public Row fetchByName(String name) throws Exception {
		ResultSet rs = execute("SELECT * FROM company WHERE name = ?",
			name);
		if (rs.next()) {
			return createFromSet(rs);
		}
		return null;
	}

	/**
	 * Fetch vector with company's employee
	 *
	 * @param row - Company row
	 * @param filter - Employee filter
	 * @return - Vector with found employee
	 * @throws Exception
	 */
	public Vector<Employee.Row> fetchEmployee(Row row, Filter<Employee.Row> filter) throws Exception {
		ResultSet resultSet = execute("SELECT e.* FROM employee AS e WHERE e.company_id = ?",
				row.getID());
		Vector<Employee.Row> result = new Vector<Employee.Row>(
				resultSet.getFetchSize());
		while (resultSet.next()) {
//			Employee.Row r = ((Employee) getModelManager().getModel("employee"))
//					.createFromSet(resultSet);
//			if (filter == null || filter.test(r)) {
//				result.add(r);
//			}
		}
		return result;
	}

	/**
	 * Fetch director's cortege
	 *
	 * @param row - Company row
	 * @return - Found director
	 * @throws Exception
	 */
	public Employee.Row fetchDirector(Row row) throws Exception {
		ResultSet resultSet = execute("SELECT e.* FROM employee AS e WHERE e.id = ? LIMIT 1",
				row.getDirectorID());
		if (!resultSet.next()) {
			return null;
		}
		return null;
//		return ((Employee) getModelManager().getModel("employee"))
//				.createFromSet(resultSet);
	}

	public static class Row extends CortegeRow {

		/**
		 * @param name - Company's name
		 * @throws java.lang.Exception
		 */
		public Row(String name, int directorID) throws Exception{
			this(0, name, buildBundle(name), directorID);
		}

		/**
		 * @param name - Company's name
		 * @param bundle - Company's bundle id
		 */
		public Row(String name, String bundle, int directorID) {
			this(0, name, bundle, directorID);
		}

		/**
		 * @param id - Identifier
		 * @param name - Company's name
		 * @param bundle - Company's bundle id
		 */
		public Row(int id, String name, String bundle, int directorID) {
			super(id); this.name = name; this.bundle = bundle; this.directorID = directorID;
		}

		/**
		 * @return - Company bundle identifier
		 */
		public String getBundle() {
			return bundle;
		}

		/**
		 * @return - Company name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return - Company director's identifier
		 */
		public int getDirectorID() {
			return directorID;
		}

		private String bundle;
		private String name;

		private int directorID;
	}

	/**
	 * @param name - Company's name
	 * @return - Company bundle identifier
	 * @throws java.lang.Exception
	 */
	private static String buildBundle(String name) throws Exception {
		MessageDigest m = MessageDigest.getInstance("MD5");
		m.reset();
		m.update((Long.toString(System.currentTimeMillis()) + name).getBytes());
		byte[] digest = m.digest();
		String hashText = new BigInteger(1,digest).toString(16);
		while(hashText.length() < 32 ){
			hashText = "0" + hashText;
		}
		return hashText;
	}
}
