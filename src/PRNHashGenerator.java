import org.json.*;
import java.io.*;
import java.security.*;
import java.util.*;

public class PRNHashGenerator {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        
        // Get PRN and file path
        System.out.print("PRN Number: ");
        String prn = input.nextLine().toLowerCase().replace(" ", "");
        System.out.print("JSON file path: ");
        String filePath = input.nextLine();
        
        try {
            // Read JSON file
            JSONObject json = new JSONObject(new JSONTokener(new FileInputStream(filePath)));
            String dest = findDest(json);
            if (dest == null) {
                System.out.println("No destination in JSON.");
                return;
            }
            
            // Generate hash
            String randStr = genRandStr(8);
            String concatVal = prn + dest + randStr;
            String hash = getMD5(concatVal);
            System.out.println("Hash: " + hash + ";" + randStr);
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }
    
    // Find destination value in JSON
    private static String findDest(JSONObject json) {
        for (String key : json.keySet()) {
            Object val = json.get(key);
            if (key.equals("destination")) {
                return val.toString();
            } else if (val instanceof JSONObject) {
                String result = findDest((JSONObject) val);
                if (result != null) return result;
            } else if (val instanceof JSONArray) {
                JSONArray arr = (JSONArray) val;
                for (int i = 0; i < arr.length(); i++) {
                    if (arr.get(i) instanceof JSONObject) {
                        String result = findDest(arr.getJSONObject(i));
                        if (result != null) return result;
                    }
                }
            }
        }
        return null;
    }
    
    // Generate random string
    private static String genRandStr(int len) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    // Generate MD5 hash
    private static String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}