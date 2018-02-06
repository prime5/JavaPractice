package sdet.test.read;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.zip.GZIPInputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class ReadText {

	public static void main(String[] args) throws IOException {
		String testURL = "https://news.ycombinator.com/";
		URL urlTest = new URL(testURL);
		HttpURLConnection newsConnection = (HttpURLConnection)urlTest.openConnection();
		try{
			newsConnection.connect();
		}
		catch(UnknownHostException e){
			System.out.println("Bad URL");
		}
		if(newsConnection.getResponseCode()!=200){
			System.out.println("Incorrect URL for testing. Exiting");
			System.exit(0);
		}
		String html = getWebPageSource(testURL);
		Document doc = Jsoup.parse(html);
		String text = doc.body().text();

		String[] usernamesStart = text.split("points by");
		
		for(int i=1; i< 11; i++){
			String usernameLine = usernamesStart[i];
			String username = (usernameLine.split(" "))[1];
			
			//Getting karma points
			
			String userURL = "https://news.ycombinator.com/user?id="+username;
			URL urlObj = new URL(userURL);
			HttpURLConnection userConnection = (HttpURLConnection)urlObj.openConnection();
			userConnection.connect();
			if(userConnection.getResponseCode()!=200){
				System.out.println("Incorrect User URL generated. Exiting");
				break;
			}
			String userhtml = getWebPageSource(userURL);
			Document userdoc = Jsoup.parse(userhtml);
			String userlinktext = userdoc.body().text();
			String karmaPoints = ((userlinktext.split("karma:")[1]).substring(1).split(" ")[0]);
			System.out.println("# "+i+"\tUsername: "+username+"\n\tKarma Points: "+karmaPoints);
		}
	}
	
	private static String getWebPageSource(String sURL) throws IOException {
        URL url = new URL(sURL);
        URLConnection urlCon = url.openConnection();
        BufferedReader in = null;

        if (urlCon.getHeaderField("Content-Encoding") != null
                && urlCon.getHeaderField("Content-Encoding").equals("gzip")) {
            in = new BufferedReader(new InputStreamReader(new GZIPInputStream(urlCon.getInputStream())));
        } else {
            in = new BufferedReader(new InputStreamReader(
                    urlCon.getInputStream()));
        }

        String inputLine;
        StringBuilder sb = new StringBuilder();

        while ((inputLine = in.readLine()) != null)
            sb.append(inputLine);
        in.close();

        return sb.toString();
}

}
