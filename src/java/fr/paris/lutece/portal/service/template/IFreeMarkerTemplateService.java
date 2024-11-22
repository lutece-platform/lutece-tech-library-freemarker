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

import fr.paris.lutece.util.html.HtmlTemplate;
import jakarta.servlet.ServletContext;

import java.util.List;

import java.util.Locale;

/**
 *
 * IFreeMarkerTemplateService
 *
 */
public interface IFreeMarkerTemplateService
{
    /**
     * Get the absolute path from relative path
     * 
     * @param strPath
     *            the path
     * @return the absolute path from relative path
     */
    String getAbsolutePathFromRelativePath( String strPath );

    /**
     * Get the default date pattern
     * 
     * @param locale
     *            the locale
     * @return the default date pattern
     */
    String getDefaultPattern( Locale locale );

    /**
     * Set the template update delay
     * 
     * @param nTemplateUpdateDelay
     *            the template update delay
     */
    void setTemplateUpdateDelay( int nTemplateUpdateDelay );

    /**
     * Adds a macro file (like the main commons.html) brought by a plugin. This file will be included for every template (autoinclude).
     * 
     * @param strFileName
     *            the filename
     */
    void addPluginMacros( String strFileName );

    /**
     * Add a shared variable into every template
     * 
     * @param name
     *            name of the shared variable
     * @param obj
     *            value
     */
    void setSharedVariable( String name, Object obj );

    /**
     * Initializes the service with the templates's path
     * 
     * @param strTemplatePath
     *            The template path
     *@deprected use {@link #init(String, ServletContext)}
     */
    @Deprecated
    void init( String strTemplatePath);
    /**
     * Initializes the service with the templates's path and servletContext
     * @param strTemplatePath  The template path
     * @param context The servlet context
     */
    void init( String strTemplatePath, ServletContext context );

    /**
     * Initializes the service with the templates's path
     * 
     * @param strTemplatePath
     *            The template path
     * @param bAcceptIncompatibleImprovements
     *            Use Freemarker new features or stay backward compatible
     */
    void init( String strTemplatePath, boolean bAcceptIncompatibleImprovements );

    /**
     * Load a template
     * 
     * @param strPath
     *            the root path
     * @param strTemplate
     *            the path of the template from the root path
     * @return the html template
     */
    HtmlTemplate loadTemplate( String strPath, String strTemplate );

    /**
     * Load a template and process a model
     * 
     * @param strPath
     *            the root path
     * @param strTemplate
     *            the path of the template from the root path
     * @param locale
     *            The locale
     * @param rootMap
     *            the model root
     * @return the processed html template
     */
    HtmlTemplate loadTemplate( String strPath, String strTemplate, Locale locale, Object rootMap );

    /**
     * Load a template from a String and process a model.
     * the template data is stored in the StringTemplateLoader of freemarker. 
     * the template key is generate by a hash of the template data.
     *    
     *
     * @param strTemplateData
     *            The template as a string
     * @param locale
     *            The {@link Locale}
     * @param rootMap
     *            the model root
     * @return the processed html template
     */
    HtmlTemplate loadTemplateFromStringFtl( String strTemplateData, Locale locale, Object rootMap );
    
    
    /**
     * Load a template from a String and process a model.
     * the template data is stored in the StringTemplateLoader of freemarker using strTemplateName as key 

     * @param strTemplateName the key of the template put in the StringTemplateLoader. The template name must be a Fully qualified name (skin.plugins.myplugin.manage_my_objects)
     * @param strTemplateData The template as a string
     * @param locale  The {@link Locale}
     * @param rootMap  the model root
     * @param bResetCacheTemplate force the update of the template data stored in the StringTemplateLoader
     * @return the processed html template
     */
    
    HtmlTemplate loadTemplateFromStringFtl(String strTemplateName,String strTemplateData, Locale locale, Object rootMap,boolean bResetCacheTemplate);

    /**
     * Clears the configuration cache
     */
    void resetConfiguration( );

    /**
     * Reset the cache
     */
    void resetCache( );

    /**
     * Get the list of auto includes files
     * 
     * @return The list or null if no configuration is available
     */
    List<String> getAutoIncludes(  );

    /**
     * Add an auto include file
     * 
     * @param strFile
     *            The file to add
     */
    void addAutoInclude( String strFile );

    /**
     * Remove an auto include file
     * 
     * @param strFile
     *            The file to remove
     */
    void removeAutoInclude( String strFile );
}
