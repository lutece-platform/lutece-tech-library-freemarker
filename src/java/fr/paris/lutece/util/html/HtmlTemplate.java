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

////////////////////////////////////////////////////////////////////////
// HtmlTemplate.java
package fr.paris.lutece.util.html;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;

import java.util.Locale;

/**
 * This class represents an HTML template that may include bookmarks that can be
 * substitute by values.
 *
 * @version 1.2.5
 */
public class HtmlTemplate
{
    private String _strTemplate;

    /**
     * Constructor 1
     */
    public HtmlTemplate( )
    {
    }

    /**
     * Constructor 2
     *
     * @param strTemplate The template as a string
     */
    public HtmlTemplate( String strTemplate )
    {
        _strTemplate = strTemplate;
    }

    /**
     * Constructor 3
     *
     * @param template Copy constructor based on another template.
     */
    public HtmlTemplate( HtmlTemplate template )
    {
        _strTemplate = template.getHtml( );
    }

    /**
     * Load the template from a file
     *
     * @param strFilename The file name to load
     * @throws IOException If an error occured
     */
    public void load( String strFilename ) throws IOException
    {

        try ( BufferedReader in = new BufferedReader( new FileReader( strFilename ) ) )
        {
            String strLine;
            StringBuilder sbContent = new StringBuilder( );

            while ( ( strLine = in.readLine( ) ) != null )
            {
                sbContent.append( strLine ).append( "\r\n" );
            }
            _strTemplate = sbContent.toString( );
        }
    }

    /**
     * Load the template from an InputStream
     *
     * @param is The open InputStream that point on the template
     * @throws IOException If an error occured
     */
    public void load( InputStream is ) throws IOException
    {
        try ( BufferedReader in = new BufferedReader( new InputStreamReader( is ) ) )
        {
            String strLine;
            StringBuilder sbContent = new StringBuilder( );

            while ( ( strLine = in.readLine( ) ) != null )
            {
                sbContent.append( strLine ).append( "\r\n" );
            }
            _strTemplate = sbContent.toString( );

        }
    }

    /**
     * Returns the template.
     *
     * @return The template as a string.
     */
    public String getHtml( )
    {
        return _strTemplate;
    }

    /**
     * Substitute each appearance of a bookmark by a given value.
     *
     * @param strBookmark The bookmark that must be present in the template.
     * @param strValue    The value to substitute as a String.
     */
    public void substitute( String strBookmark, String strValue )
    {
        _strTemplate = substitute( _strTemplate, strValue, strBookmark );
    }

    /**
     * Substitute each appearance of a bookmark by a given value.
     *
     * @param strBookmark The bookmark that must be present in the template.
     * @param nValue      The value to substitute as an integer.
     */
    public void substitute( String strBookmark, int nValue )
    {
        String strValue = String.valueOf( nValue );
        substitute( strBookmark, strValue );
    }

    /**
     * Substitute each appearance of a bookmark by a given value.
     *
     * @param strBookmark The bookmark that must be present in the template.
     * @param date        The value to substitute as a Date.
     */
    public void substitute( String strBookmark, java.sql.Date date )
    {
        String strValue = getDateString( date );
        substitute( strBookmark, strValue );
    }

    /**
     * Substitute each occurence of a bookmark by a given value.
     *
     * @param strBookmark The bookmark that must be present in the template.
     * @param date        The value to substitute as a Timestamp.
     */
    public void substitute( String strBookmark, java.sql.Timestamp date )
    {
        String strValue = getDateString( date );
        substitute( strBookmark, strValue );
    }

    /**
     * Converts a date value to a String date
     * 
     * @param date The date
     * @return The formatted string
     */
    private static String getDateString( java.util.Date date )
    {
        if ( date != null )
        {
            SimpleDateFormat formatter = new SimpleDateFormat( "dd'/'MM'/'yyyy", Locale.FRANCE );
            StringBuffer strDate = new StringBuffer( );
            formatter.format( date, strDate, new FieldPosition( 0 ) );

            return strDate.toString( );
        }

        return "";
    }

    /**
     * This function substitutes all occurences of a given bookmark by a given value
     *
     * @param strSource   The input string that contains bookmarks to replace
     * @param strValue    The value to substitute to the bookmark
     * @param strBookmark The bookmark name
     * @return The output string.
     */
    private static String substitute( String strSource, String strValue, String strBookmark )
    {
        StringBuilder strResult = new StringBuilder( );
        int nPos = strSource.indexOf( strBookmark );
        String strModifySource = strSource;

        while ( nPos != -1 )
        {
            strResult.append( strModifySource.substring( 0, nPos ) );
            strResult.append( strValue );
            strModifySource = strModifySource.substring( nPos + strBookmark.length( ) );
            nPos = strModifySource.indexOf( strBookmark );
        }

        strResult.append( strModifySource );

        return strResult.toString( );
    }
}
