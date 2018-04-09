package it.teamdigitale.postprocessing;

import it.teamdigitale.datastructure.StorableLotto;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Several lines in the output tsv have more than 17 fields due the presence of tabs in the json fields (i.e. jsonPartecipanti, jsonAggiudicatari).
 */
public class TabPostProcessor {

    private static String processLine(String line){
        //this remove tabs in json columns
        String newString = line.replaceAll("\\\\t", "");

        String[] tokens = newString.split("\t");
        int num_tokens = tokens.length;
        //this remove tabs in
        if(num_tokens >= 18){
            System.out.println("Line length "+ num_tokens);
            String firstPart = String.join("\t", Arrays.copyOfRange(tokens, 0, 16));
            String secondPart = String.join(" ", Arrays.copyOfRange(tokens, 16, num_tokens));
            String newLine =  firstPart.trim() + "\t" + secondPart.trim();

            return newLine;
        } else {
            return newString;
        }
    }

    public static void run(String inputFile, String outputFile) throws IOException {

        File fileOutputFilename = new File(outputFile);

        try(BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            for(String line; (line = br.readLine()) != null; ) {
                String newLine = processLine(line);
                FileUtils.writeStringToFile(fileOutputFilename, newLine.trim() + "\n", Charset.defaultCharset(), true);
            }
        }
    }

    public static void main(String[] args) throws IOException {
       // String input = "notebooks/data/anac/anacDataset_1522836870094.csv";
       String output = "notebooks/data/anac/anacDataset_1522836870094_postprocessed2.tsv";

       // run(input,output);

        String input = "notebooks/data/anac/anacDataset_1522836870094_postprocessed.tsv";
        File fileOutputFilename = new File(output);

        int count = 0;
        int wrong_size = 0;
        try(BufferedReader br = new BufferedReader(new FileReader(input))) {

            for(String line; (line = br.readLine()) != null; ) {
                String newString = line.replaceAll("\\\\t", "");
                count ++;

                String[] tokens = newString.split("\t");
                int num_tokens = tokens.length;
                if(num_tokens == 17){
                    FileUtils.writeStringToFile(fileOutputFilename, newString + "\n", Charset.defaultCharset(), true);
                    wrong_size ++;
                } else {

                }
            }
        }

        System.out.println("Total wrong size " + wrong_size);


    }
}
