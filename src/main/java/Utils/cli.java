package Utils;

import org.apache.commons.cli.*;

public class cli {

    public static void main(String[] args) throws ParseException {
        String[] Args = { "-help" };
        Object[] a = getArgs(Args);
        System.out.print(a[4]);
    }

    public static Object[] getArgs(String[] args) {
        Options options = new Options();
        options.addOption("help", false, "list help");
        options.addOption("asin", true, "asin value");
        options.addOption("Query", true, "Query Content");
        options.addOption("repeat", true, "repeat times");
        options.addOption("maxHits", true, "max hits");
        String asin = "";
        String reviewText = "";
        int repeat = 1;
        int maxHits = 10;
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("help")) {
                String formatstr = "cil";
                HelpFormatter hf = new HelpFormatter();
                hf.printHelp(formatstr, "", options, "", true);
            } else {
                if (cmd.hasOption("asin")) {
                    asin = cmd.getOptionValue("asin");
                }
                if (cmd.hasOption("Query")) {
                    reviewText = cmd.getOptionValue("Query");
                }
                if (cmd.hasOption("repeat")) {
                    repeat = Integer.parseInt(cmd.getOptionValue("repeat"));
                }
                if (cmd.hasOption("maxHits")) {
                    maxHits = Integer.parseInt(cmd.getOptionValue("maxHits"));
                }
            }
        } catch (ParseException e) {
            HelpFormatter hf = new HelpFormatter();
            System.out.println(e.getMessage());
            hf.printHelp("Usage:", options);
            System.exit(0);
        }
        return new Object[] { asin, reviewText, repeat, maxHits };
    }
}
