package it.teamdigitale.datastructure;

public class StringHandler {

    public static String removeSpecialSymbols(String string) {
        String res = "";
        try {
            res = string.replaceAll("[^\\w\\s]","");
        } catch (Exception e){
            System.out.print("");
        }
        return res;
    }
}
