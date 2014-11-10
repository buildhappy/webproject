/**
 * $RCSfile: DbQuery.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:50:57 $
 *
 * New Jive  from Jdon.com.
 *
 * This software is the proprietary information of CoolServlets, Inc.
 * Use is subject to license terms.
 */

package com.jivesoftware.forum.database;

import java.util.Date;
import java.util.Iterator;
import java.io.IOException;
import java.io.File;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.document.*;
import org.apache.lucene.store.*;
import org.apache.lucene.search.*;
import org.apache.lucene.queryParser.*;
import org.apache.lucene.index.*;

import com.jivesoftware.forum.*;

/**
 * Database implementation of the Query interface using Lucene.
 */
public class DbQuery implements com.jivesoftware.forum.Query {

    /**
     * The query String to use for searching. Set it the empty String by
     * default so that if the user fails to set a query String, there won't
     * be errors.
     */
    private String queryString = "";
    private java.util.Date beforeDate = null;
    private java.util.Date afterDate = null;
    private User user = null;
    private ForumThread thread = null;

    /**
     * The results of the query as an array of message ID's.
     */
    private long [] results = null;

    private Forum [] forums;
    private DbForumFactory factory;

    /**
     * The maximum number of results to return with a search.
     */
    private static final int MAX_RESULTS_SIZE = 1500;

    private static String indexPath = null;
    private static IndexReader reader;
    private static Searcher searcher;
    private static Directory searchDirectory = null;
    private static long indexLastModified;

    public DbQuery(Forum [] forums, DbForumFactory factory) {
        this.forums = forums;
        this.factory = factory;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
        //reset results
        results = null;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setBeforeDate(java.util.Date beforeDate) {
        this.beforeDate = beforeDate;
        //reset results
        results = null;
    }

    public java.util.Date getBeforeDate() {
        return beforeDate;
    }

    public void setAfterDate(java.util.Date afterDate){
        this.afterDate = afterDate;
        //reset results
        results = null;
    }

    public java.util.Date getAfterDate(){
        return afterDate;
    }

    public User getFilteredUser() {
        return user;
    }

    public void filterOnUser(User user) {
        this.user = user;
        results = null;
    }

    public ForumThread getFilteredThread() {
        return thread;
    }

    public void filterOnThread(ForumThread thread) {
        this.thread = thread;
        results = null;
    }

    public int resultCount() {
        if (results == null) {
            executeQuery();
        }
        return results.length;
    }

    public Iterator results() {
        if (results == null) {
            executeQuery();
        }
        return new DatabaseObjectIterator(JiveGlobals.MESSAGE, results, factory);
    }

    public Iterator results(int startIndex, int numResults){
        if (results == null) {
            executeQuery();
        }

        int endIndex = startIndex + numResults - 1 ;
        if (endIndex > results.length - 1) {
            endIndex = results.length - 1;
        }

        int length = endIndex - startIndex + 1;
        if (length < 0) {
            return new DatabaseObjectIterator(JiveGlobals.MESSAGE, new long[0], factory);
        }
        else {
            long [] resultsSubset = new long[length];
            for (int i=0; i<length; i++) {
                resultsSubset[i] = results[startIndex + i];
            }
            return new DatabaseObjectIterator(JiveGlobals.MESSAGE, resultsSubset, factory);
        }
    }

    /**
     * Execute the query and store the results in the results array.
     */
    private void executeQuery() {
        try {
            Searcher searcher = getSearcher();
            if (searcher == null) {
                // Searcher can be null if the index doesn't exist.
                results = new long[0];
                return;
            }

            org.apache.lucene.search.Query bodyQuery =
                QueryParser.parse(queryString, "body", DbSearchManager.analyzer);

            org.apache.lucene.search.Query subjectQuery =
                QueryParser.parse(queryString, "subject", DbSearchManager.analyzer);

            BooleanQuery comboQuery = new BooleanQuery();
            comboQuery.add(subjectQuery,false,false);
            comboQuery.add(bodyQuery,false,false);

            MultiFilter multiFilter = new MultiFilter(3);
            int filterCount = 0;

            // Forum filter -- we can ignore filtering if we are searching all
            // forums in the system.
            if (factory.getForumCount() != forums.length) {
                String[] forumIDs = new String[forums.length];
                for (int i=0; i<forumIDs.length; i++) {
                    forumIDs[i] = Long.toString(forums[i].getID());
                }
                multiFilter.add(new FieldFilter("forumID", forumIDs));
                filterCount++;
            }

            //Date filter
            if (beforeDate != null || afterDate != null) {
                if (beforeDate != null && afterDate != null) {
                    multiFilter.add(new DateFilter("creationDate", beforeDate, afterDate));
                    filterCount++;
                }
                else if (beforeDate == null) {
                    multiFilter.add(DateFilter.After("creationDate", afterDate));
                    filterCount++;
                }
                else {
                    multiFilter.add(DateFilter.Before("creationDate", beforeDate));
                    filterCount++;
                }
            }

            // User filter
            if (user != null) {
                String userID = Long.toString(user.getID());
                multiFilter.add(new FieldFilter("userID", userID));
                filterCount++;
            }

            // Thread filter
            if (thread != null) {
                String threadID = Long.toString(thread.getID());
                multiFilter.add(new FieldFilter("threadID", threadID));
                filterCount++;
            }

            Hits hits;
            // Only apply filters if any are defined.
            if (filterCount > 0) {
                hits = searcher.search(comboQuery, multiFilter);
            }
            else {
                hits = searcher.search(comboQuery);
            }
            // Don't return more search results than the maximum number allowed.
            int numResults = hits.length() < MAX_RESULTS_SIZE ?
                    hits.length() : MAX_RESULTS_SIZE;
            long [] messages = new long[numResults];
            for (int i=0; i<numResults; i++) {
                messages[i] = Long.parseLong( ((Document)hits.doc(i)).get("messageID") );
            }
            results = messages;
        }
        catch (Exception e) {
            e.printStackTrace();
            results = new long[0];
        }
    }

    /**
     * Returns a Lucene Searcher that can be used to execute queries. Lucene
     * can handle index reading even while updates occur. However, in order
     * for index changes to be reflected in search results, the reader must
     * be re-opened whenever the modifiedDate changes.<p>
     *
     * The location of the index is the "search" subdirectory in [jiveHome]. If
     * jiveHome is not set correctly, this method will fail.
     *
     * @return a Searcher that can be used to execute queries.
     */
    private static Searcher getSearcher() throws IOException {
        if (indexPath == null) {
            //Get path of where search index should be. It should be
            //the search subdirectory of [jiveHome].
            String jiveHome = JiveGlobals.getJiveHome();
            if (jiveHome == null) {
                System.err.println("ERROR: the jiveHome property is not set.");
                throw new IOException("Unable to open index for searching " +
                        "because jiveHome was not set.");
            }
            indexPath =  jiveHome + File.separator + "search";
        }

        if (searcher == null) {
            //Acquire a lock -- analyzer is a convenient object to do this on.
            synchronized(DbSearchManager.analyzer) {
                if (searcher == null) {
                    if (indexExists(indexPath)) {
                        searchDirectory = FSDirectory.getDirectory(indexPath, false);
                        reader = IndexReader.open(searchDirectory);
                        indexLastModified = reader.lastModified(searchDirectory);
                        searcher = new IndexSearcher(reader);
                    }
                    //Otherwise, the index doesn't exist, so return null.
                    else {
                        return null;
                    }
                }
            }
        }
        if (reader.lastModified(searchDirectory) > indexLastModified) {
            synchronized (DbSearchManager.analyzer) {
                if (reader.lastModified(searchDirectory) > indexLastModified) {
                    if (indexExists(indexPath)) {
                        indexLastModified = reader.lastModified(searchDirectory);
                        //We need to close the indexReader because it has changed.
                        //Re-opening it will make changes visible.
                        reader.close();

                        searchDirectory = FSDirectory.getDirectory(indexPath, false);
                        reader = IndexReader.open(searchDirectory);
                        searcher = new IndexSearcher(reader);
                    }
                    //Otherwise, the index doesn't exist, so return null.
                    else {
                        return null;
                    }
                }
            }
        }
        return searcher;
    }

    /**
     * Returns true if the search index exists at the specified path.
     *
     * @param indexPath the path to check for the search index at.
     */
    private static boolean indexExists(String indexPath) {
        //Lucene always creates a file called "segments" -- if it exists, we
        //assume that the search index exists.
        File segments = new File(indexPath + File.separator + "segments");
        return segments.exists();
    }
}