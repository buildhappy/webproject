/**
 * $RCSfile: FieldFilter.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:00 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import java.util.BitSet;
import java.io.IOException;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.IndexReader;

/**
 * A Filter that restricts search results to Documents that match a set of
 * specified Field values.
 *
 * For example, suppose you create a search index to make your catalog of widgets
 * searchable. When indexing, you add a field to each Document called "color"
 * that has one of the following values: "blue", "green", "yellow", or "red".
 * Now suppose that a user is executing a query but only wants to see green
 * widgets in the results. The following code snippet yields that behavior:
 * <pre>
 *     //In this example, we assume the Searcher and Query are already defined.
 *     //Define a FieldFilter to only show green colored widgets.
 *     Field myFilter = new FieldFilter("color", "green");
 *     Hits queryResults = mySearcher.execute(myQuery, myFilter);
 * </pre>
 *
 * @author Matt Tucker (matt@coolservlets.com)
 */
public class FieldFilter extends org.apache.lucene.search.Filter {

    private Term [] searchTerms;

    /**
     * Creates a new field filter. The name of the field and the values to filter
     * on are specified. In order for a Document to pass this filter, it must:
     * <ol>
     *      <li>The given field must exist in the document.
     *      <li>The field value in the Document must exactly match one of the
     *          given values.</ol>
     *
     * @param field the name of the field to filter on.
     * @param values the possible values of the field that search results must
     *      match.
     */
    public FieldFilter(String field, String [] values) {
        searchTerms = new Term[values.length];
        for (int i=0; i<values.length; i++) {
            searchTerms[i] = new Term(field, values[i]);
        }
    }

    /**
     * Creates a new field filter. The name of the field and the value to filter
     * on are specified. In order for a Document to pass this filter, it must:
     * <ol>
     *      <li>The given field must exist in the document.
     *      <li>The field value in the Document must exactly match one of the
     *          given values.</ol>
     *
     * @param field the name of the field to filter on.
     * @param value the value of the field that search results must match.
     */
    public FieldFilter(String field, String value) {
        this(field, new String[] { value });
    }

    public BitSet bits(IndexReader reader) throws IOException {
        //Create a new BitSet with a capacity equal to the size of the index.
        BitSet bits = new BitSet(reader.maxDoc());
        //Match all search terms.
        for (int i=0; i < searchTerms.length; i++) {
            //Get an enumeration of all the documents that match the specified
            //field value.
            TermDocs matchingDocs = reader.termDocs(searchTerms[i]);
            try {
                if (matchingDocs != null) {
                    while(matchingDocs.next()) {
                        bits.set(matchingDocs.doc());
                    }
                }
            }
            finally {
                if (matchingDocs != null) {
                    matchingDocs.close();
                }
            }
        }
        return bits;
    }
}