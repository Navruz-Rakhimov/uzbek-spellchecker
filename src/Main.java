import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Map.Entry;

public class Main {

    public static MultiMap<String, String> dict = new MultiMap<>();

    public static void populateDict(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = null;

            while ((line = br.readLine()) != null) {
                String soundex = Soundex.soundex(line);
                dict.put(soundex, line);
            }
        }
    }

    public static void main(String[] args) {
        try {
            populateDict("lexicon.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String word = "erkak";

        // search for homophones
        Collection<String> collection = dict.get(Soundex.soundex(word));
        MultiMap<Integer, String> sortedWords = new MultiMap();

        // pruning results with Levenshtein Distance
        for (String str: collection) {
            int distance = Levenshtein.distance(word, str);

            if (distance<3) {
                sortedWords.put(distance, str);
            }
        }

        System.out.println("Word mispelled: " + word);
        System.out.println("Matching words: ");

        for (Entry<Integer, Collection<String>> entry: sortedWords.entrySet()) {
            int value = entry.getKey();

            for (String str: entry.getValue()) {
                System.out.println(str + " - " + value);
            }
        }
    }
}