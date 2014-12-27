package Sql;

import java.io.Serializable;

public interface CortegeProtocol extends Serializable {

    /**
     * Every collage must have
     * own identifier
     *
     * @return row's id
     */
    public int getID() throws Exception;
}
