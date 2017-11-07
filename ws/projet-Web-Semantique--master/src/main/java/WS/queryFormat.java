package WS;


public class queryFormat {
    /**
     * Transforms the String to fit the URL format
     * @param Query
     * @return
     */
    public static String queryToURL(String Query){
        return Query.replaceAll("\\s+","+");
    }

    public static void main(String[] args){
        System.out.println(queryToURL("space odyssey"));
        System.out.println(queryToURL("space  odyssey"));
    }
}
