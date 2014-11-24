import Component.Model;
import Component.ModelManager;
import Component.Type;
import Core.*;
import Core.InternalError;
import Server.WebServer;
import Sql.Connection;
import Terminal.Adapter;
import Terminal.Instruction;
import Terminal.Machine;
import Terminal.Station;
import Router.*;
import java.sql.ResultSet;

/**
 * Entry point
 */
public class Main {

//	private static void registerJaw(DataBaseHelper dataBaseHelper, String directorLogin) throws Exception {
//
//		Jaw.models.User userModel = new Jaw.models.User(dataBaseHelper);
//		Jaw.models.Employee employeeModel = new Jaw.models.Employee(dataBaseHelper);
//		Jaw.models.Group groupModel = new Jaw.models.Group(dataBaseHelper);
//		Jaw.models.Company companyModel = new Jaw.models.Company(dataBaseHelper);
//
//		Jaw.models.User.Row userJaw;
//		Jaw.models.Employee.Row employeeJaw;
//
//		if (!userModel.exists(directorLogin)) {
//
//			userJaw = Extension.UserValidator.register(directorLogin, "root").getCortege();
//
//			if (!groupModel.exists("Jaw")) {
//				groupModel.add(new Jaw.models.Group.Row("Jaw", 0, userJaw.getID()));
//			}
//
//			employeeJaw = employeeModel.add(new Jaw.models.Employee.Row(
//					"Dmitry", "Savonin", userJaw.getID(), 0, 0, 0));
//		} else {
//			userJaw = userModel.fetchByLogin("Jaw");
//
//			if ((employeeJaw = employeeModel.fetchByUserID(userJaw.getID())) == null) {
//				employeeJaw = employeeModel.add(new Jaw.models.Employee.Row(
//						"Dmitry", "Savonin", userJaw.getID(), 0, 0, 0));
//			}
//		}
//		if (!companyModel.exists("Jaw")) {
//			companyModel.add(new Jaw.models.Company.Row("Jaw", employeeJaw.getID()));
//		}
//	}

	/**
	 * @param args - List with application arguments
	 */
    public static void main(String[] args) throws Exception {

//		try {
//			DataBaseHelper dataBaseHelper
//				= new DataBaseHelper();
//
//			Jaw.models.User userModel = new Jaw.models.User(dataBaseHelper);
//
//			registerJaw(dataBaseHelper, "Jaw");
//
//			int companyID = new Jaw.models.Company(dataBaseHelper)
//				.fetchByName("Jaw").getID();
//
//			ProjectManager projectManager = new ProjectManager(
//				dataBaseHelper, companyID);
//
//			projectManager.createProject("Jaw", userModel.fetchByLogin("Jaw").getID());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		Environment environment = new Environment(new Connection(
			Config.DBMS_HOST,
			Config.DBMS_USER,
			Config.DBMS_PASSWORD
		), "jaw");

		ModelManager modelManager = new ModelManager(environment);
		ProjectManager projectManager = new ProjectManager(environment);

		projectManager.getCompiler().compile();

//		ClassSeeker classSeeker = new ClassSeeker(
//			environment
//		);
//
//		classSeeker.findByAnnotation(Annotation.Controller.class);

//		Machine terminal = new Machine(
//			environment
//		);
//
//		WebServer.run();

//		ResultSet r;
//
//		r = environment.getConnection().command()
//			.select("*")
//			.from("users")
//			.where("id = 6")
//			.execute()
//			.select();
//
//		while (r.next()) {
//			System.out.println(r.getInt("id") + " : " + r.getString("login"));
//		}

//		ModelManager modelManager = new ModelManager(null);
//
//		DataBaseHelper dataBaseHelper = new DataBaseHelper(
//			Config.DBMS_HOST,
//			Config.DBMS_USER,
//			Config.DBMS_PASSWORD
//		);
//
//		Jaw.models.Company.Row company = new Jaw.models.Company(
//			dataBaseHelper
//		).fetchByName("Jaw");
//
//		ProjectManager projectManager = new ProjectManager(
//			dataBaseHelper, company.getID()
//		);
//
//		projectManager.compile();
//
//		Model m = modelManager.get("jaw/company");

//		c.add(new Company.Row());
//		ClassLoader classLoader = getClassLoader();
//		try {
//			Compiler.compile("123.java");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}