package controllers;

import Core.*;
import Core.InternalError;

import java.sql.SQLException;

public class Group extends Controller {

	/**
	 * @param environment - Every core's extension must have environment
	 * with predeclared extensions
	 */
	public Group(Environment environment) {
		super(environment);
	}

	@Override
	public boolean checkAccess(String... privileges) throws InternalError, SQLException {
		return super.checkAccess(privileges);
	}

	@Override
	public void actionView() throws Core.InternalError, SQLException {
		redirect("Index", "View");
	}
}
