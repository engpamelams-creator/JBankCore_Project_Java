package br.com.jbank.core.infra.response;

/**
 * Enum representing the three possible JSend response statuses.
 * <p>
 * JSend is a specification for a simple, no-frills, JSON based format for application-level communication.
 * </p>
 * 
 * @see <a href="https://github.com/omniti-labs/jsend">JSend Specification</a>
 */
public enum JSendStatus {
    /**
     * All went well, and (usually) some data was returned.
     */
    SUCCESS,
    
    /**
     * There was a problem with the data submitted, or some pre-condition of the API call wasn't satisfied.
     */
    FAIL,
    
    /**
     * An error occurred in processing the request, i.e. an exception was thrown.
     */
    ERROR
}
