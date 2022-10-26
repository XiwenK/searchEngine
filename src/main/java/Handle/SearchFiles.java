package Handle;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class SearchFiles {

    public static void searchIndex(String indexPath, String asin, String queryString, int maxHits)
            throws IOException, ParseException {
        try {
            DirectoryReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
            IndexSearcher indexSearcher = new IndexSearcher(reader);
            BooleanQuery.Builder builder = new BooleanQuery.Builder();

            String stopWordsPath = System.getProperty("user.dir") + "/static/stopwords.txt";
            List<String> self_stop_words = Files.readAllLines(Path.of(stopWordsPath), StandardCharsets.UTF_8);
            CharArraySet stopSet = new CharArraySet(self_stop_words, true);
            Analyzer analyzer = new StandardAnalyzer(stopSet);

            QueryParser parser = new QueryParser("reviewText", analyzer);
            Query textQuery = parser.parse(queryString);
            builder.add(textQuery, BooleanClause.Occur.MUST);
            if (!asin.equals("")) {
                TermQuery idQuery = new TermQuery(new Term("asin", asin));
                builder.add(idQuery, BooleanClause.Occur.MUST);
            }
            BooleanQuery booleanQuery = builder.build();

            TopDocs topDocs = indexSearcher.search(booleanQuery, maxHits);
            ScoreDoc[] hits = topDocs.scoreDocs;
            display(hits, indexSearcher);

            reader.close();

        } catch (IOException e) {
            System.out.println("Index path does not exist.");
            throw e;
        } catch (ParseException e) {
            System.out.println("Can not parse query content.");
            throw e;
        }
    }

    public static void display(ScoreDoc[] hits, IndexSearcher indexSearcher) throws IOException {

        if (hits.length == 0) {
            System.out.println("Empty Result!");
            return;
        }

        for (int i = 0; i < Math.min(5, hits.length); i++) {
            int docId = hits[i].doc;
            float score = hits[i].score;
            Document doc = indexSearcher.doc(docId);

            String[] texts = doc.get("reviewText").split(" ");
            texts = Arrays.copyOfRange(texts, 0, Math.min(10, texts.length));
            String text = String.join(" ", texts);

            System.out.println(i + 1 + ". asin: " + doc.get("asin") + " reviewerID: " + doc.get("reviewerID")
                    + " score: " + score + "\n   Summary: " + doc.get("summary") + "\n   Overall rate: " +
                    doc.get("overall") + "\n   Review: " + text + "....");
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        String indexPath = System.getProperty("user.dir") + "/index";
//        String queryString = "Johnathan Barber";
        String queryString = "for summer";
        String asin = "B00H91I078";
        searchIndex(indexPath, asin, queryString, 100);
        searchIndex(indexPath, "", queryString, 100);
    }
}
