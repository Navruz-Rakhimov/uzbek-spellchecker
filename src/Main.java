import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;

public class Main {

    public static MultiMap<String, String> dict = new MultiMap<>();
    public static MultiMap<String, String> affixes = new MultiMap<>();

    public static void populateDict(String filename, MultiMap<String, String> dict) throws IOException {
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
            populateDict("lexicon.txt", dict);
            populateDict("oneElementAffix.txt", affixes);
            populateDict("twoElementsAffixes.txt", affixes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String word = "daftaringizmikan";

        MultiMap<Integer, String> sortedWords = new MultiMap();

        boolean isFound = false;
        String temp = word;
        int affixBoundary;

        for (affixBoundary = temp.length() - 1; affixBoundary >= 0 && !isFound; affixBoundary--){
            // search for homophones
            temp = temp.substring(0, affixBoundary);

            Collection<String> collection = dict.get(Soundex.soundex(temp));

            if (collection != null ) {

                // pruning results with Levenshtein Distance
                for (String str : collection) {
                    int distance = Levenshtein.distance(temp, str);

                    if (distance == 0) {
                        sortedWords.put(distance, str);
                        isFound = true;
                    }
                }
            }
        }


        String affixPart = word.substring(affixBoundary + 1);


        Collection<String> affixesSoundex = affixes.get(Soundex.soundex(affixPart));
        MultiMap<Integer, String> sortedAffixes = new MultiMap();

        for (String str: affixesSoundex) {
            int distance = Levenshtein.distance(affixPart, str);

            if (distance < 4) {
                sortedAffixes.put(distance, str);
            }
        }


        System.out.println("Current entry: " + word);
        System.out.println("\nMatching morphemes: ");

        for (Entry<Integer, Collection<String>> entry: sortedAffixes.entrySet()) {
            int value = entry.getKey();

            for (String str: entry.getValue()) {
                System.out.println(temp + str + " - " + value);
            }
        }
    }
}