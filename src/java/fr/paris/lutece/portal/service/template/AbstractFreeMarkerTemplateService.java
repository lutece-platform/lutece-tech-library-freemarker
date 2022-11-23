/*
 * Copyright (c) 2002-2020, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.portal.service.template;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.exception.LuteceFreemarkerException;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.ext.jakarta.servlet.WebappTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import jakarta.servlet.ServletContext;

/**
 *
 * Template service based on the Freemarker template engine
 *
 */
public abstract class AbstractFreeMarkerTemplateService implements IFreeMarkerTemplateService
{
    private static final String NUMBER_FORMAT_PATTERN = "0.######";
    private static final String SETTING_DATE_FORMAT = "date_format";
    private static final String CONSTANT_HASH_ENCODING = "UTF-8";
    private static final String CONSTANT_HASH_DIGEST = "MD5";
    

    /** the list contains plugins specific macros */
    private List<String> _listPluginsIncludes = new ArrayList<>( );
    private Map<String, String> _listPluginsImports = new HashMap<>( );
    private Map<String, Object> _mapSharedVariables = new HashMap<>( );
    private Map<String, Configuration> _mapConfigurations = new HashMap<>( );
    private String _strDefaultPath;
    private int _nTemplateUpdateDelay;
    private boolean _bAcceptIncompatibleImprovements;
    private ServletContext _context;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTemplateUpdateDelay( int nTemplateUpdateDelay )
    {
        _nTemplateUpdateDelay = nTemplateUpdateDelay;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void addPluginMacros( String strFileName )
    {
        _listPluginsIncludes.add( strFileName );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPluginInclude( String strFileName )
    {
        _listPluginsIncludes.add( strFileName );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPluginImport( String strNamespace, String strFileName )
    {
        _listPluginsImports.put( strNamespace, strFileName );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSharedVariable( String name, Object obj )
    {
        _mapSharedVariables.put( name, obj );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init( String strTemplatePath )
    {
        _strDefaultPath = strTemplatePath;
        _bAcceptIncompatibleImprovements = false;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void init( String strTemplatePath, ServletContext context )
    {
    	_context= context;
        _strDefaultPath = strTemplatePath;
        _bAcceptIncompatibleImprovements = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init( String strTemplatePath, boolean bAcceptIncompatibleImprovements )
    {
        _strDefaultPath = strTemplatePath;
        _bAcceptIncompatibleImprovements = bAcceptIncompatibleImprovements;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HtmlTemplate loadTemplate( String strPath, String strTemplate )
    {
        return loadTemplate( strPath, strTemplate, null, null );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HtmlTemplate loadTemplate( String strPath, String strTemplate, Locale locale, Object rootMap )
    {
        Configuration cfg = _mapConfigurations.get( strPath );

        if ( cfg == null )
        {
            cfg = initConfig( _strDefaultPath, Locale.getDefault( ) );
        }

        return processTemplate( cfg, strTemplate, rootMap, locale );
    }

  
    /**
    * {@inheritDoc}
   */
    @Override
	public HtmlTemplate loadTemplateFromStringFtl(String strTemplateData, Locale locale, Object rootMap) {
		try {
			String strContentKey = getHash(strTemplateData);
			return loadTemplateFromStringFtl(strContentKey, strTemplateData, locale, rootMap, false);
		}
		catch (NoSuchAlgorithmException | UnsupportedEncodingException hashEx) {

			throw new LuteceFreemarkerException(
					"Can not create hash for template content " + strTemplateData + hashEx.getMessage(), hashEx);

		}

	}
    
    

    /**
    * {@inheritDoc}
   */
    @Override
	public HtmlTemplate loadTemplateFromStringFtl(String strTemplateName,String strTemplateData, Locale locale, Object rootMap,boolean bResetCacheTemplate) {
	

			Configuration cfg = _mapConfigurations.get(_strDefaultPath);

			if (cfg == null) {
				cfg = initConfig(_strDefaultPath, Locale.getDefault());
			}

			MultiTemplateLoader mtl = (MultiTemplateLoader) cfg.getTemplateLoader();
			if ( bResetCacheTemplate || ((StringTemplateLoader) mtl.getTemplateLoader(1)).findTemplateSource(strTemplateName) == null) {
				((StringTemplateLoader) mtl.getTemplateLoader(1)).putTemplate(strTemplateName, strTemplateData);
			}

			return processTemplate(cfg, strTemplateName, rootMap, locale);
		

	}
    

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetConfiguration( )
    {
        _mapConfigurations = new HashMap<>( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetCache( )
    {
        for ( Configuration cfg : _mapConfigurations.values( ) )
        {
            cfg.clearTemplateCache( );
        }
    }

    /**
     * Init a configuration using the current default path
     * @param locale The Locale
     */
    public void initConfig( Locale locale )
    {
        Configuration cfg = _mapConfigurations.get( _strDefaultPath );
        if ( cfg == null )
        {
            initConfig( _strDefaultPath, locale );
        }
    }
    
    /**
     * Initialize a configuration
     * 
     * @param strPath
     *            The template's path
     * @param locale
     *            The locale
     * @return A configuration object
     */
    private Configuration initConfig( String strPath, Locale locale )
    {
        try
        {
            Configuration cfg = buildConfiguration( locale );
                 StringTemplateLoader stringLoader = new StringTemplateLoader( );
            TemplateLoader [ ] loaders ;
            if( _context != null ) {
               TemplateLoader ftlWebapp = new WebappTemplateLoader( _context , strPath );
                loaders = new TemplateLoader [ ] {
                		ftlWebapp,  stringLoader
               };
            }else {
            	  // set the root directory for template loading
                   File directory = new File(  this.getAbsolutePathFromRelativePath( strPath )  );
                   FileTemplateLoader ftl1 = new FileTemplateLoader( directory );
          
            	loaders = new TemplateLoader [ ] {
                		  ftl1, stringLoader
                };
            }
           
            MultiTemplateLoader mtl = new MultiTemplateLoader( loaders );
            cfg.setTemplateLoader( mtl );          
            _mapConfigurations.put( strPath, cfg );
            return cfg;
        }
        catch( TemplateException | IOException e )
        {
            throw new LuteceFreemarkerException( e.getMessage( ), e );
        }

    }

    /**
     * Build a configuration with default settings
     * 
     * @param locale
     *            The given locale
     * @return A configuration
     * @throws TemplateException
     *             if an error occurs
     */
    private Configuration buildConfiguration( Locale locale ) throws TemplateException
    {
        Version version = ( _bAcceptIncompatibleImprovements ) ? Configuration.VERSION_2_3_28 : Configuration.VERSION_2_3_0;
        Configuration cfg =  new Configuration( version );

        // add core and plugin auto-includes such as macros
        for ( String strFileName : _listPluginsIncludes )
        {
            cfg.addAutoInclude( strFileName );
        }

        for ( Entry<String, Object> entry : _mapSharedVariables.entrySet( ) )
        {
            cfg.setSharedVariable( entry.getKey( ), entry.getValue( ) );
        }

        // disable the localized look-up process to find a template
        cfg.setLocalizedLookup( false );

        // keep control localized number formating (can cause pb on ids, and we don't want to use the ?c directive all the time)
        cfg.setNumberFormat( NUMBER_FORMAT_PATTERN );

        // Used to set the default format to display a date and datetime
        cfg.setSetting( SETTING_DATE_FORMAT, this.getDefaultPattern( locale ) );

        // Time in seconds that must elapse before checking whether there is a newer version of a template file
        cfg.setTemplateUpdateDelayMilliseconds( ( ( long ) _nTemplateUpdateDelay ) * 1000L );
        return cfg;
    }

    /**
     * Process the template transformation and return the {@link HtmlTemplate}
     * 
     * @param cfg
     *            The Freemarker configuration to use
     * @param strTemplate
     *            The template name to call
     * @param rootMap
     *            The HashMap model
     * @param locale
     *            The {@link Locale}
     * @return The {@link HtmlTemplate}
     */
    private HtmlTemplate processTemplate( Configuration cfg, String strTemplate, Object rootMap, Locale locale )
    {
        HtmlTemplate template = null;

        try
        {
            Template ftl;

            if ( locale == null )
            {
                ftl = cfg.getTemplate( strTemplate );
            }
            else
            {
                ftl = cfg.getTemplate( strTemplate, locale );
            }

            StringWriter writer = new StringWriter( 1024 );
            // Used to set the default format to display a date and datetime
            ftl.setDateFormat( this.getDefaultPattern( locale ) );

            ftl.process( rootMap, writer );
            template = new HtmlTemplate( writer.toString( ) );
        }
        catch( IOException | TemplateException e )
        {
            throw new LuteceFreemarkerException( e.getMessage( ), e );
        }

        return template;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAutoIncludes( )
    {
        Configuration cfg = _mapConfigurations.get( _strDefaultPath );
        if ( cfg == null )
        {
            cfg = initConfig( _strDefaultPath, Locale.getDefault( ) );
        }
        return cfg.getAutoIncludes( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAutoInclude( String strFile )
    {
        Configuration cfg = _mapConfigurations.get( _strDefaultPath );
        if ( cfg != null )
        {
            cfg.addAutoInclude( strFile );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAutoInclude( String strFile )
    {
        Configuration cfg = _mapConfigurations.get( _strDefaultPath );
        if ( cfg != null )
        {
            cfg.removeAutoInclude( strFile );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String,String> getAutoImports( )
    {
        Configuration cfg = _mapConfigurations.get( _strDefaultPath );
        if ( cfg == null )
        {
            cfg = initConfig( _strDefaultPath, Locale.getDefault( ) );
        }
        return cfg.getAutoImports( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAutoImport( String strNamespace, String strFile )
    {
        Configuration cfg = _mapConfigurations.get( _strDefaultPath );
        if ( cfg != null )
        {
            cfg.addAutoImport( strNamespace, strFile );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAutoImport( String strNamespace )
    {
        Configuration cfg = _mapConfigurations.get( _strDefaultPath );
        if ( cfg != null )
        {
            cfg.removeAutoImport( strNamespace );
        }
    }
    
    
    /**
     * get hash
     *
     * @param message
     * @param last
     *            hash
     *
     * @return the hash in String
     * @throws UnsupportedEncodingException 
     * @throws NoSuchAlgorithmException 
     */
    private static String getHash( String message ) throws UnsupportedEncodingException, NoSuchAlgorithmException 
    {

        byte [ ] byteChaine;
        byteChaine = message.getBytes( CONSTANT_HASH_ENCODING );
        MessageDigest md = MessageDigest.getInstance( CONSTANT_HASH_DIGEST );
        byte [ ] hash = md.digest( byteChaine );

        // convert byte array to Hexadecimal String
        StringBuilder sb = new StringBuilder( 2 * hash.length );
        for ( byte b : hash )
        {
            sb.append( String.format( "%02x", b & 0xff ) );
        }

          	return sb.toString( );

      

    }



}
