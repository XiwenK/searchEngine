import Entity.Review;
import Utils.dataUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class IndexFiles {

    public static void createIndex(String indexPath, String srcPath) throws IOException {
        List<Review> reviews = dataUtils.readJSON(srcPath);

        try {
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            IndexWriter indexWriter = new IndexWriter(FSDirectory.open(Path.of(indexPath)), iwc);

            for (Review r : reviews) {
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
        createIndex(indexPath, srcPath);
    }
}
