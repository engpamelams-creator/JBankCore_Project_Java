package br.com.jbank.core.infra.response;

/**
 * JSend response for successful operations.
 * <p>
 * Contains the actual data returned by the API.
 * </p>
 * 
 * @param <T> Type of the data being returned
 */
public class JSendSuccessResponse<T> extends JSendResponse {
    
    private final T data;
    
    private JSendSuccessResponse(T data) {
        super(JSendStatus.SUCCESS);
        this.data = data;
    }
    
    /**
     * Factory method to create a success response.
     * 
     * @param data The data to be returned
     * @param <T> Type of the data
     * @return A new JSendSuccessResponse instance
     */
    public static <T> JSendSuccessResponse<T> of(T data) {
        return new JSendSuccessResponse<>(data);
    }
    
    /**
     * Factory method to create a success response without data.
     * 
     * @return A new JSendSuccessResponse instance with null data
     */
    public static JSendSuccessResponse<Void> empty() {
        return new JSendSuccessResponse<>(null);
    }
    
    public T getData() {
        return data;
    }
}
