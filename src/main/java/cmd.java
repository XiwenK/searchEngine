import java.io.IOException;
import org.apache.lucene.queryparser.classic.ParseException;

public class cmd {
    public static void main(String[] args) throws IOException, ParseException {
        Object[] cmd_args = Utils.cli.getArgs(args);
        String asin = (String) cmd_args[0];
        String reviewText = (String) cmd_args[1];
        int repeat = (int) cmd_args[2];
        int maxHits = (int) cmd_args[3];
//        System.out.println(asin + " " + reviewText + " " + repeat + " " + maxHits);
        String indexPath = System.getProperty("user.dir") + "/index";

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < repeat; i++) {
            Handle.SearchFiles.searchIndex(indexPath, asin, reviewText, maxHits);
        }
        long endTime   = System.currentTimeMillis();
        System.out.println("Query Search Time:" + (endTime - startTime)/repeat + " milliseconds");
    }
}
