package coolsquid.properties.config;

public class ConfigException extends RuntimeException {

	private static final long serialVersionUID = -1492616860929730243L;

	public ConfigException(String message, Object... args) {
		super(args.length == 0 ? message : String.format(message, args));
	}
}