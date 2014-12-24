package controllers;

import Core.*;
import Core.InternalError;

import java.sql.SQLException;

public class Employee extends Controller {

	/**
	 * @param environment - Every core's extension must have environment
	 * with predeclared extensions
	 */
	public Employee(Environment environment) {
		super(environment);
	}

	@Override
	public void actionGetTable() throws InternalError, SQLException {
		super.actionGetTable();
	}

	@Override
	public void actionView() throws Core.InternalError, SQLException {

	}
}
