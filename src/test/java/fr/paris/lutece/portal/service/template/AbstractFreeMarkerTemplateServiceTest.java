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
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * AbstractFreeMarkerTemplateService Test
 */
public class AbstractFreeMarkerTemplateServiceTest
{
    private static final int UPDATE_DELAY = 30;
    private static final String FILE_TEMPLATE_1 = "template1.html";
    private static final String FILE_TEMPLATE_2 = "template2.html";
    private static final String FILE_TEMPLATE_3 = "template3.html";
    private static final String FILE_AUTO_INCLUDE = "auto_include.html";
    private static final String EXPECTED_1 = "expected1.html";
    private static final String EXPECTED_2 = "expected2.html";
    private static final String EXPECTED_3 = "expected3.html";
    
    private static final String MARK_VALUE = "value";
    private static final String VALUE_TEST = "test";
    
    private static final String PATH_TEMPLATES = "target/test-classes/";
    
    /**
     * Test of loadTemplate method, of class AbstractFreeMarkerTemplateService.
     * @throws java.io.IOException
     */
    @Test
    public void testLoadTemplate_String_String() throws IOException
    {
        System.out.println( "loadTemplate" );
        
        AbstractFreeMarkerTemplateService instance = getInstance( false );
        HtmlTemplate result = instance.loadTemplate( PATH_TEMPLATES , FILE_TEMPLATE_1 );
        String strExpected = FileUtils.readFileToString( new File(PATH_TEMPLATES + EXPECTED_1 ));
        assertEquals( strExpected, result.getHtml() );
               
        instance = getInstance( true );
        result = instance.loadTemplate( PATH_TEMPLATES, FILE_TEMPLATE_1 );
        assertEquals( strExpected, result.getHtml() );
    }

    /**
     * Test of loadTemplate method, of class AbstractFreeMarkerTemplateService.
     * @throws java.io.IOException
     */
    @Test
    public void testLoadTemplate_4args() throws IOException
    {
        System.out.println( "loadTemplate" );
        AbstractFreeMarkerTemplateService instance = getInstance( false );
        Locale locale = Locale.US;
        Map<String, Object> model = new HashMap<>();
        model.put( MARK_VALUE , VALUE_TEST );
        HtmlTemplate result = instance.loadTemplate( PATH_TEMPLATES, FILE_TEMPLATE_2 , locale , model );
        String strExpected = FileUtils.readFileToString( new File(PATH_TEMPLATES + EXPECTED_2 ));
        assertEquals( strExpected, result.getHtml() );
                
        instance = getInstance( true );
        result = instance.loadTemplate( PATH_TEMPLATES, FILE_TEMPLATE_2 , locale , model );
        assertEquals( strExpected, result.getHtml() );

    }

    /**
     * Test of loadTemplate method, of class AbstractFreeMarkerTemplateService.
     * @throws java.io.IOException
     */
    @Test
    public void testLoadTemplate_3args() throws IOException
    {
        System.out.println( "loadTemplate" );
        AbstractFreeMarkerTemplateService instance = getInstance( false );
        Locale locale = Locale.US;
        Map<String, Object> model = new HashMap<>();
        model.put( MARK_VALUE , VALUE_TEST );
        String strTemplate = FileUtils.readFileToString( new File(PATH_TEMPLATES + FILE_TEMPLATE_2 ));
        HtmlTemplate result = instance.loadTemplate( strTemplate , locale , model );
        String strExpected = FileUtils.readFileToString( new File(PATH_TEMPLATES + EXPECTED_2 ));
        assertEquals( strExpected, result.getHtml() );
        
        instance = getInstance( true );
        result = instance.loadTemplate( strTemplate , locale , model );
        assertEquals( strExpected, result.getHtml() );
    }

    /**
     * Test of resetConfiguration method, of class AbstractFreeMarkerTemplateService.
     */
    @Test
    public void testResetConfiguration()
    {
        System.out.println( "resetConfiguration" );
        AbstractFreeMarkerTemplateService instance = getInstance( true );
        instance.resetConfiguration();
    }

    /**
     * Test of resetCache method, of class AbstractFreeMarkerTemplateService.
     */
    @Test
    public void testResetCache()
    {
        System.out.println( "resetCache" );
        AbstractFreeMarkerTemplateService instance = getInstance( true );
        instance.resetCache();
    }

    /**
     * Test of getAutoIncludes method, of class AbstractFreeMarkerTemplateService.
     * @throws java.io.IOException
     */
    @Test
    public void testAutoIncludes() throws IOException
    {
        System.out.println( "AutoIncludes" );
        AbstractFreeMarkerTemplateService instance = getInstance( true );

        instance.initConfig( Locale.US );

        instance.addAutoInclude( FILE_AUTO_INCLUDE );
        
        List list = instance.getAutoIncludes();
        assertTrue( list.size() == 1 );

        HtmlTemplate result = instance.loadTemplate( PATH_TEMPLATES, FILE_TEMPLATE_3 );
        String strExpected = FileUtils.readFileToString( new File( PATH_TEMPLATES + EXPECTED_3 ));
        assertEquals( strExpected, result.getHtml() );
        
        instance.removeAutoInclude( FILE_AUTO_INCLUDE );
        
        list = instance.getAutoIncludes();
        assertTrue( list.isEmpty() );

    }

    
    
    /**
     * An implementation of the abstract class
     */
    public class AbstractFreeMarkerTemplateServiceImpl extends AbstractFreeMarkerTemplateService
    {

        @Override
        public String getAbsolutePathFromRelativePath( String strPath )
        {
            File fPath = new File( strPath );
            return fPath.getAbsolutePath();
        }

        @Override
        public String getDefaultPattern( Locale locale )
        {
            return "dd/mm/yyyy";
        }
    }
    

    /**
     * Test of setSharedVariable method, of class AbstractFreeMarkerTemplateService.
     */
    @Test
    public void testSetSharedVariable()
    {
        System.out.println( "setSharedVariable" );
        String name = "";
        Object obj = null;
        AbstractFreeMarkerTemplateService instance = getInstance( true );
        instance.setSharedVariable( name, obj );
    }
    
    
    
    /**
     * Initialize an instance of the service
     * @param bAcceptIncompatibleImprovements
     * @return 
     */
    private AbstractFreeMarkerTemplateService getInstance( boolean bAcceptIncompatibleImprovements)
    {
        AbstractFreeMarkerTemplateService instance = new AbstractFreeMarkerTemplateServiceImpl();
        instance.init( PATH_TEMPLATES, bAcceptIncompatibleImprovements );
        instance.setTemplateUpdateDelay( UPDATE_DELAY );
        return instance;
    }
    
}
