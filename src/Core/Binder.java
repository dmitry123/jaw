package Core;

import Sql.CortegeProtocol;

/**
 * Created by Savonin on 2014-11-08
 */
public class Binder <T extends CortegeProtocol> implements CortegeProtocol {

	/**
	 * @param model - Cortege's model, which describes
	 * 		it's table
	 * @param cortege - Table's element, have to implement
	 * 		CortegeProtocol
	 */
	public Binder(Model<T> model, T cortege) {
		this.model = model;
		this.cortege = cortege;
	}

	/**
	 * Every collage must have own identifier
	 * @return - Row's identifier
	 */
	@Override
	public int getID() throws Exception {
		return cortege != null ? cortege.getID() : 0;
	}

	/**
	 * @return - Model
	 */
	public Model<T> getModel() {
		return model;
	}

	/**
	 * @return - Cortege
	 */
	public T getCortege() {
		return cortege;
	}

	private Model<T> model;
	private T cortege;
}
