# Exception Handling

## Using `@ControllerAdvice` and `@ExceptionHandler`
Example:
```java
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGenericException(Exception ex) {
		log.error(ex.getMessage(), ex);
		return new ResponseEntity<>("Unexpected error happened", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
```