package Sql;

/**
 * CollageRow
 */
public class CortegeRow implements CortegeProtocol {

	/**
	 * @param id Identifier
	 */
	public CortegeRow(int id) {
		this.id = id;
	}

	/**
	 * @param id - New cortege identifier
	 */
	public void changeID(int id) {
		this.id = id;
	}

    /**
     * Every collage must have
     * own identifier
     *
     * @return row's id
     */
    @Override
    public int getID() {
        return id;
    }

	/**
	 * Identifier
	 */
	private int id;
}
