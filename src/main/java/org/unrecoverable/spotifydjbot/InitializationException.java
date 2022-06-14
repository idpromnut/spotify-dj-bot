package org.unrecoverable.spotifydjbot;

public class InitializationException extends RuntimeException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InitializationException()
	{
		super();
	}

	public InitializationException( String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace )
	{
		super( message, cause, enableSuppression, writableStackTrace );
	}

	public InitializationException( String message, Throwable cause )
	{
		super( message, cause );
	}

	public InitializationException( String message )
	{
		super( message );
	}

	public InitializationException( Throwable cause )
	{
		super( cause );
	}

}
