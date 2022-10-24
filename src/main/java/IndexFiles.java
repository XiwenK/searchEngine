import Entity.Review;
import Utils.dataUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.lang.System;

public class IndexFiles {

    public static void createIndex(String indexPath, String srcPath, String stopWordsPath) throws IOException {
        List<Review> reviews = dataUtils.readJSON(srcPath);
        List<String> self_stop_words = Files.readAllLines(Path.of(stopWordsPath),
                StandardCharsets.UTF_8);
        System.out.println(self_stop_words.size());

        try {
            // stopwords reference in NLP: https://github.com/lighting66ban/stop-word

            CharArraySet stopSet = new CharArraySet(self_stop_words, true);
            Analyzer analyzer = new StandardAnalyzer(stopSet);
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            IndexWriter indexWriter = new IndexWriter(FSDirectory.open(Path.of(indexPath)), iwc);

            int document_num = reviews.size();
            int document_cnt = 1;
            long startTime = System.currentTimeMillis();

            for (Review r : reviews) {
                if(document_cnt%(document_num/10)==0)
                {
                    long endTime   = System.currentTimeMillis();
                    System.out.println("Processing this 10% documents cost " + (endTime - startTime) + " milliseconds");
                    startTime = System.currentTimeMillis();
                }
                document_cnt+=1;
                Document doc = new Document();
                doc.add(new StoredField("reviewerID", r.getReviewerID()));
                doc.add(new StoredField("reviewerName", r.getReviewerName()));
                doc.add(new StoredField("helpful", Arrays.toString(r.getHelpful())));
                doc.add(new StoredField("overall", r.getOverall()));
                doc.add(new StoredField("unixReviewTime", r.getUnixReviewTime()));
                doc.add(new StoredField("reviewTime", r.getReviewTime()));
                // doc.add(new StoredField("source", r.getSource()));

                doc.add(new StringField("asin", r.getAsin(), Field.Store.YES));

                doc.add(new TextField("reviewText", r.getReviewText(), Field.Store.YES));
                doc.add(new TextField("summary", r.getSummary(), Field.Store.YES));

                indexWriter.addDocument(doc);
            }

            indexWriter.close();

        } catch (IOException e) {
            System.out.println("Index path does not exist.");
            throw e;
        }
    }

    public static void main(String[] args) throws IOException {
        String indexPath = System.getProperty("user.dir") + "/index";
        String srcPath = System.getProperty("user.dir") + "/static/samples_no_index.json";
        String stopWordsPath = System.getProperty("user.dir") + "/static/stopwords.txt";
        createIndex(indexPath, srcPath, stopWordsPath);
    }
}
