package org.musicbrainz.search.servlet;

import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.musicbrainz.search.index.DatabaseIndex;
import org.musicbrainz.search.index.RecordingIndexField;
import org.musicbrainz.search.servlet.mmd1.TrackMmd1XmlWriter;
import org.musicbrainz.search.servlet.mmd2.RecordingWriter;

import java.util.ArrayList;


public class RecordingSearch extends SearchServer {

    public RecordingSearch() throws Exception {
    
        resultsWriter = new RecordingWriter();
        mmd1XmlWriter = new TrackMmd1XmlWriter();
        defaultFields = new ArrayList<String>();
        defaultFields.add(RecordingIndexField.RECORDING.getName());
        analyzer = DatabaseIndex.getAnalyzer(RecordingIndexField.class);
    }

    public RecordingSearch(IndexSearcher searcher) throws Exception {
        this();
        indexSearcher = searcher;
    }

    public RecordingSearch(IndexSearcher searcher, String query, int offset, int limit) throws Exception {
        this(searcher);
        this.query=query;
        this.offset=offset;
        this.limit=limit;
    }

    @Override
    protected QueryParser getParser() {
       return new RecordingQueryParser(defaultFields.get(0), analyzer);
    }


}