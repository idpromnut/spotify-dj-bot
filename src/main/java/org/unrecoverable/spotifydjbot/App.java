package org.unrecoverable.spotifydjbot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.unrecoverable.spotifydjbot.bot.DjBot;
import org.unrecoverable.spotifydjbot.storage.LocalDiskStore;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

@SpringBootApplication
@ComponentScan( basePackages =
{ "org.unrecoverable.spotifydjbot" } )
public class App implements CommandLineRunner
{

	private static Logger log;

	private static final String ENV_HOME_DIRECTORY = "SPOTIFY_DJ_HOME";
	private static final String SYSTEM_PROPERTY_HOME_DIRECTORY = "home.dir";
	private static final String CONFIG_FILENAME = "config.yaml";
	private static final String LOGBACK_CONFIG_FILENAME = "etc/logback.xml";
	private static final String DEFAULT_CONFIG_FILENAME = "etc/" + CONFIG_FILENAME;
	private static final String BASE_LOG_DIR_SYSTEM_PROPERTY = "BASE_LOG_DIR";

	private static String configFileName = DEFAULT_CONFIG_FILENAME;
	
	private static ClassPathResource CONFIG_TEMPLATE_FILE = new ClassPathResource("templates/config.yaml.template");

	private static File homeDirectory = new File( "." );
	private static Config config = new Config();
	private static HttpClient httpClient;
	
	@Autowired
	private LocalDiskStore store;
	
	public static void main( String[] args )
	{

		OptionParser parser = new OptionParser();
		parser.accepts( "config" ).withRequiredArg().ofType( String.class );
		parser.allowsUnrecognizedOptions();
		OptionSet options = parser.parse( args );

		File newHomeDirectory = null;
		String homeDirSetLog = "default";
		if ( System.getenv().containsKey( ENV_HOME_DIRECTORY ) )
		{
			newHomeDirectory = new File( System.getenv().get( ENV_HOME_DIRECTORY ) );
			homeDirSetLog = String.format( "environment variable %s", ENV_HOME_DIRECTORY );
		} else if ( System.getProperty( SYSTEM_PROPERTY_HOME_DIRECTORY ) != null )
		{
			newHomeDirectory = new File( System.getProperty( SYSTEM_PROPERTY_HOME_DIRECTORY ) );
			homeDirSetLog = String.format( "system property variable %s", SYSTEM_PROPERTY_HOME_DIRECTORY );
		}

		if ( newHomeDirectory != null )
		{
			if ( !newHomeDirectory.isDirectory() )
			{
				log = LoggerFactory.getLogger( App.class );
				log.warn( "used {} to configure home directory to {}, but this directory does not exist", homeDirSetLog,
						newHomeDirectory.getAbsolutePath() );
				homeDirSetLog = "default";
			} else if ( !checkDirWritable( newHomeDirectory ) )
			{
				log = LoggerFactory.getLogger( App.class );
				log.warn( "used {} to configure home directory to {} from {}, but this directory is not writable",
						homeDirSetLog, newHomeDirectory.getAbsolutePath() );
				homeDirSetLog = "default";
			} else
			{
				homeDirectory = newHomeDirectory.getAbsoluteFile();
				String previousLogConf = (String)System.getProperties().put( "logging.config",
						homeDirectory.getAbsolutePath() + File.separator + LOGBACK_CONFIG_FILENAME );
				System.getProperties().put( BASE_LOG_DIR_SYSTEM_PROPERTY, homeDirectory.getAbsolutePath() );
				setCurrentDirectory( homeDirectory );
				log = LoggerFactory.getLogger( App.class );
				log.info( "Previous log configuration was {}, current log configuration is {}", previousLogConf,
						System.getProperties().get( "logging.config" ) );
			}
		} else
		{
			log = LoggerFactory.getLogger( App.class );
		}
		log.info( "setting home directory via {} to {}", homeDirSetLog, homeDirectory );

		if ( options.has( "config" ) )
		{
			configFileName = (String)options.valueOf( "config" );
		}
		// check the expected "home" directory location
		else if ( (new File( homeDirectory, CONFIG_FILENAME )).exists() )
		{
			configFileName = (new File( homeDirectory, CONFIG_FILENAME )).getAbsolutePath();
		}
		// check the "install" directory location
		else if ( (new File( homeDirectory, DEFAULT_CONFIG_FILENAME )).exists() )
		{
			configFileName = (new File( homeDirectory, DEFAULT_CONFIG_FILENAME )).getAbsolutePath();
		} else
		{
			File configFileLocation = new File( homeDirectory, DEFAULT_CONFIG_FILENAME );
			try {
				FileUtils.copyInputStreamToFile(CONFIG_TEMPLATE_FILE.getInputStream(), configFileLocation);
				log.warn( "No configuration file found at {}. Default configfile created.", configFileLocation );
			} catch (IOException e) {
				log.error( "Could not create config fie from template in {}", configFileLocation);
			}
			System.exit( 1 );
		}
		log.info( "using configuration file located at {}", configFileName );

		File tmpDir = new File( homeDirectory, "tmp" );
		if ( !tmpDir.exists() )
		{
			if ( !tmpDir.mkdir() )
			{
				log.error( "cannot create tmp directory {}", tmpDir.getAbsolutePath() );
				System.exit( 1 );
			}
		} else if ( !tmpDir.isDirectory() )
		{
			log.error( "a file with the name {} exists in {}", tmpDir.getName(),
					tmpDir.getParentFile().getAbsolutePath() );
			System.exit( 1 );
		}

		log.info( "set java.io.tmp to {}", tmpDir.getAbsolutePath() );
		System.setProperty( "java.io.tmpdir", tmpDir.getAbsolutePath() );

		try
		{
			File.createTempFile( "test", "tmpdir.tmp" ).delete();
		} catch ( IOException e )
		{
			log.error( "could not create test temp file in {}", tmpDir.getAbsolutePath(), e );
		}

		// clear out temp directory
		try
		{
			FileUtils.cleanDirectory( tmpDir );
		} catch ( IOException e )
		{
			log.warn( "could not clean up temp directory before starting: {}", tmpDir.getAbsolutePath(), e );
		}

		System.setProperty( "spring.config.location", configFileName );

		config = loadConfiguration( configFileName );

		System.setProperty( "home.dir", homeDirectory.getAbsolutePath() );
		createHttpClientInstance();
		SpringApplication.run( App.class, args );
	}

	public void run( String... args ) throws Exception
	{
	    log.info("Version: {}", Version.getVersion());
	    
		// this.authPlugin.getAuthManagers().add(new LocalFileAuthenticationProvider());
		// this.authPlugin.getAuthManagers().add(new PAMAuthenticationProvider());
		startBot();
		log.info("Started");
	}

	private void startBot() {
	    new Thread(() -> {
	        DjBot bot = new DjBot( config.getBotConfig(), store );
	        bot.run();
	        System.exit(0);
	    },"BotMainThread").start();
	}
	
	@Bean( name = "homeDirectory" )
	public File homeDirectory()
	{
		return homeDirectory;
	}

	@Bean( name = "config" )
	public Config config()
	{
		return config;
	}

	@Bean( name = "httpClient" )
	public HttpClient httpClient()
	{
		return httpClient;
	}

	private static boolean checkDirWritable( final File dir )
	{
		try
		{
			File testFile = File.createTempFile( "test", "tmpdir.tmp", dir );
			return testFile.delete();
		} catch ( IOException e )
		{
			log.debug( "directory {} is not writable", dir.getAbsolutePath(), e );
		}
		return false;
	}

	public static boolean setCurrentDirectory( File directory )
	{
		return System.setProperty( "user.dir", directory.getAbsolutePath() ) != null;
	}

	private static Config loadConfiguration( final String configurationFilename )
	{
		// load in configuration
		Config configuration = new Config();
		Representer representer = new Representer();
		representer.getPropertyUtils().setSkipMissingProperties( true );
		Yaml readerYaml = new Yaml( new Constructor( Config.class ), representer );
		File configFile = new File( configurationFilename );
		try ( Reader configReader = new FileReader( configFile ) )
		{
			configuration = (Config)readerYaml.load( configReader );
			log.info( "Loaded configuration from {}: {}", configFile, configuration );
		} catch ( FileNotFoundException e1 )
		{
			log.error( "could not load configuration from {}", configFile, e1 );
			log.info( "Using default configuration: {}", configuration );
		} catch ( IOException e )
		{
			log.error( "could not load configuration from {}", configFile, e );
		}

		return configuration;
	}

	private static void createHttpClientInstance()
	{
		try {
			// Turn off certificate host name verification. Might want to turn these on for "public" sites and off for Plex servers.
			SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory( SSLContext.getDefault(), new NoopHostnameVerifier() );
//            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory( SSLContext.getDefault(), new NoopHostnameVerifier() );

			Registry<ConnectionSocketFactory> connectionSocketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
	            .register("http", PlainConnectionSocketFactory.getSocketFactory())
	            .register("https", sslConnectionSocketFactory)
	            .build();
			PoolingHttpClientConnectionManager pool = new PoolingHttpClientConnectionManager(connectionSocketFactoryRegistry);
			pool.setMaxTotal( 10 );
			
			httpClient = HttpClientBuilder.create()
					.setConnectionManager( pool ).build();
		}
		catch(NoSuchAlgorithmException e) {
			throw new InitializationException("Could not create HttpClient instance", e);
		}
	}
}
