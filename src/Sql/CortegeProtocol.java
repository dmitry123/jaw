package Sql;

public interface CortegeProtocol {

    /**
     * Every collage must have
     * own identifier
     *
     * @return row's id
     */
    public int getID() throws Core.InternalError;
}
