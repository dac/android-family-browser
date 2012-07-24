package ca.chaves.familyBrowser.test.data;

import ca.chaves.android.graph.GraphStorage;
import ca.chaves.familyBrowser.test.util.Utils;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Config;
import android.util.Log;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Return the data to be used inside unit tests.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class TestData
{
    /**
     * Tag for logging.
     */
    private static final String TAG = "Test";

    /**
     * Return "just" the default node, with no parents, siblings or children.
     *
     * @param context the Android {@link Context}
     * @return SampleNode object
     */
    public static TestNode getDefaultNode( final Context context )
    {
        final SampleXmlHandler handler = new SampleXmlHandler();
        TestData.loadXmlSampleData( context, handler );
        return handler.getDefaultNode();
    }

    /**
     * Return the root node of a sample tree.
     *
     * @param context the Android {@link Context}
     * @return SampleNode object
     */
    public static TestNode getSampleTree( final Context context )
    {
        final SampleXmlHandler handler = new SampleXmlHandler();
        TestData.loadXmlSampleData( context, handler );
        return handler.getSampleTree();
    }

    /**
     * Load the sample data from "assets/test_data.xml".
     *
     * @param context
     * @param handler
     */
    private static void loadXmlSampleData( final Context context, final SampleXmlHandler handler )
    {
        try
        {
            final Context ctx = Utils.getTestContext( context );

            final SAXParserFactory factory = SAXParserFactory.newInstance();
            final SAXParser parser = factory.newSAXParser();

            final AssetManager assets = ctx.getAssets();
            final InputSource source = new InputSource( assets.open( "test_data.xml" ) );
            parser.parse( source, handler );
        }
        catch ( final ParserConfigurationException ex )
        {
            Log.e( TestData.TAG, "unable to parse assets/test_data.xml", ex );
        }
        catch ( final SAXException ex )
        {
            Log.e( TestData.TAG, "unable to parse assets/test_data.xml", ex );
        }
        catch ( final IOException ex )
        {
            Log.e( TestData.TAG, "unable to parse assets/test_data.xml", ex );
        }
    }

    /**
     * Helper class to load sample data from an external XML file.
     */
    private static class SampleXmlHandler
        extends DefaultHandler
    {
        private final transient HashMap<String, TestNode> nodes = new HashMap<String, TestNode>();

        private final transient HashMap<String, String> fathers = new HashMap<String, String>();

        private final transient HashMap<String, String> mothers = new HashMap<String, String>();

        private final transient StringBuilder body = new StringBuilder();

        /**
         * XML tag.
         */
        private static final String NODE_TAG = "node";

        /**
         * XML tag.
         */
        private static final String NAME_TAG = "name";

        /**
         * XML tag.
         */
        private static final String FATHER_TAG = "father";

        /**
         * XML tag.
         */
        private static final String MOTHER_TAG = "mother";

        private transient TestNode current;

        private TestNode root;

        /**
         * Return the default node data.
         *
         * @return SampleNode object
         */
        public TestNode getDefaultNode()
        {
            if ( Config.LOGV )
            {
                Log.v( TestData.TAG, "root = " + root.label );
            }

            // setup and return the root node

            root.id = Integer.valueOf( GraphStorage.DEFAULT_NODE_ID );
            return root;
        }

        /**
         * Return the root node of a sample tree.
         *
         * @return SampleNode object
         */
        public TestNode getSampleTree()
        {
            final Collection<TestNode> dataSet = nodes.values();

            // populate parents

            for ( final TestNode node : dataSet )
            {
                final TestNode father = nodes.get( fathers.get( node.label ) );
                final TestNode mother = nodes.get( mothers.get( node.label ) );

                if ( ( father != null ) && ( mother != null ) )
                {
                    node.parents = new TestNode[]{father, mother};
                }
                else if ( father != null )
                {
                    node.parents = new TestNode[]{father};
                }
                else if ( mother != null )
                {
                    node.parents = new TestNode[]{mother};
                }
            }

            // build children lists

            final HashMap<String, HashSet<String>> children = new HashMap<String, HashSet<String>>();

            for ( final TestNode node : dataSet )
            {
                for ( final TestNode parent : node.parents )
                {
                    SampleXmlHandler.at( children, parent.label ).add( node.label );
                }
            }

            // populate children

            for ( final TestNode node : dataSet )
            {
                final HashSet<String> set = SampleXmlHandler.at( children, node.label );

                node.children = new TestNode[set.size()];

                int index = 0;
                for ( final String child : set )
                {
                    node.children[index++] = nodes.get( child );
                }
            }

            // populate siblings

            for ( final TestNode node : dataSet )
            {
                final HashSet<String> set = new HashSet<String>();

                for ( final TestNode parent : node.parents )
                {
                    set.addAll( SampleXmlHandler.at( children, parent.label ) );
                }

                set.remove( node.label );

                node.siblings = new TestNode[set.size()];

                int index = 0;
                for ( final String child : set )
                {
                    node.siblings[index++] = nodes.get( child );
                }
            }

            // setup and return the root node

            return this.getDefaultNode();
        }

        /**
         * Utility function.
         *
         * @param map
         * @param label
         */
        private static HashSet<String> at( final HashMap<String, HashSet<String>> map, final String label )
        {
            HashSet<String> result = map.get( label );
            if ( result == null )
            {
                result = new HashSet<String>();
                map.put( label, result );
            }
            return result;
        }

        /**
         * Call-back.
         */
        @Override
        public void characters( final char[] content, final int start, final int length )
            throws SAXException
        {
            super.characters( content, start, length );
            body.append( content, start, length );
        }

        /**
         * Call-back.
         */
        @Override
        public void endElement( final String uri, final String localName, final String name )
            throws SAXException
        {
            final String content = body.toString().trim();

            if ( Config.LOGV )
            {
                Log.v( TestData.TAG, "endElement " + localName + " " + content );
            }
            super.endElement( uri, localName, name );

            if ( current != null )
            {
                if ( SampleXmlHandler.NAME_TAG.equalsIgnoreCase( localName ) )
                {
                    current.label = content;
                }
                else if ( SampleXmlHandler.FATHER_TAG.equalsIgnoreCase( localName ) )
                {
                    fathers.put( current.label, content );
                }
                else if ( SampleXmlHandler.MOTHER_TAG.equalsIgnoreCase( localName ) )
                {
                    mothers.put( current.label, content );
                }
                else if ( SampleXmlHandler.NODE_TAG.equalsIgnoreCase( localName ) )
                {
                    nodes.put( current.label, current );
                    current = null;
                }
            }

            body.setLength( 0 );
        }

        /**
         * Call-back.
         */
        @Override
        public void startElement( final String uri, final String localName, final String name,
                                  final Attributes attributes )
            throws SAXException
        {
            if ( Config.LOGV )
            {
                Log.v( TestData.TAG, "startElement " + localName );
            }

            super.startElement( uri, localName, name, attributes );

            if ( SampleXmlHandler.NODE_TAG.equalsIgnoreCase( localName ) )
            {
                current = new TestNode();
            }

            // assume the "root" node is always the first one

            if ( root == null )
            {
                root = current;
            }
        }
    }
}
