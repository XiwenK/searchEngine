import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SearchFiles {

    public static void searchIndex(String indexPath, String queryString, int maxHits) throws IOException, ParseException {
        try {
            DirectoryReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
            IndexSearcher indexSearcher = new IndexSearcher(reader);
            Analyzer analyzer = new StandardAnalyzer();

            QueryParser parser = new QueryParser("reviewText", analyzer);
            Query query = parser.parse(queryString);
            // PhraseQuery query = new PhraseQuery(0, "reviewText", "for", "summer");  // phrase query

            TopDocs topDocs = indexSearcher.search(query, maxHits);
            ScoreDoc[] hits = topDocs.scoreDocs;
            for (int i = 0; i < hits.length; i ++) {
                int docId = hits[i].doc;
                Document doc = indexSearcher.doc(docId);
                System.out.println(i + 1 + ". reviewerID: " + doc.get("reviewerID"));
            }

            reader.close();

        } catch (IOException e) {
            System.out.println("Index path does not exist.");
            throw e;
        } catch (ParseException e) {
            System.out.println("Can not parse query content.");
            throw e;
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        String indexPath = System.getProperty("user.dir") + "/index";
        String queryString = "for summer";
        searchIndex(indexPath, queryString, 100);
    }
}
