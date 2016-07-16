package eurocris.oaipmh;

/**
 * A typed exception thrown during OAI-PMH harvesting
 * 
 * @author Nuno Freire (nfreire@gmail.com)
 */
public class HarvestException extends Exception {

    /**
     * For inheritance reasons, pipes through to the super constructor.
     * 
     * @param message
     *            description of the error
     */
    public HarvestException(String message) {
        super(message);
    }

    /**
     * For inheritance reasons, pipes through to the super constructor.
     * 
     * @param message
     *            description of the error
     * @param cause
     *            root cause of the error
     */
    public HarvestException(String message, Throwable cause) {
        super(message, cause);
    }
}
