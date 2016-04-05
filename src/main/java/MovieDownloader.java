import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * A class for downloading movie data from the internet.
 * Code adapted from Google.
 *
 * YOUR TASK: Add comments explaining how this code works!
 * 
 * @author Joel Ross & Kyungmin Lee
 */
public class MovieDownloader {

	public static String[] downloadMovieData(String movie) {

		//construct the url for the omdbapi API
		String urlString = "";
		try {
			// Takes the name of the movie we entered, puts it in the URL, however if that doesn't work, 
			// this try/catch will "catch" it and return null for the method.
			urlString = "http://www.omdbapi.com/?s=" + URLEncoder.encode(movie, "UTF-8") + "&type=movie"; 
		}catch(UnsupportedEncodingException uee){
			return null;
		}

		//Creating our objects
		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;

		//Creating the array that will eventually be returned (hopefully)
		String movies[] = null;


		try { //Tries to do the actions in this block.  If ANY of them fail, we move to the catch.

			URL url = new URL(urlString); //Creating our URL object with our custom movie URL. 

			urlConnection = (HttpURLConnection) url.openConnection(); //Creates the connection. 
			urlConnection.setRequestMethod("GET"); //Tells the connection we plan to send a get request
			urlConnection.connect(); //Connects to the web server.

			InputStream inputStream = urlConnection.getInputStream(); //Returns an input stream reading from the current connection.
			StringBuffer buffer = new StringBuffer(); //Creates a new StringBuffer object
			if (inputStream == null) { //If the inputStream is null (aka nothing came from the website), return Null
				return null;
			}
			//...otherwise, initialize a BufferReader with this input stream.
			reader = new BufferedReader(new InputStreamReader(inputStream));

			//Then, we are going to read each line in that BufferedReader
			String line = reader.readLine(); 
			while (line != null) { //It will continue reading until it reaches a line that is NULL (end of file hopefully)
				buffer.append(line + "\n"); //Adding a line break to the end of each line
				line = reader.readLine(); //
			}

			if (buffer.length() == 0) { //AKA if nothing was returned from the webpage, return null
				return null;
			}

			//We are turning this buffer (with each line and added line breaks) into a string and modifying it
			//by replacing some characters
			String results = buffer.toString();
			results = results.replace("{\"Search\":[","");
			results = results.replace("]}","");
			results = results.replace("},", "},\n");

			//Once we cleaned up our data, we are going to split it based on new lines
			//the output of which will be an array.  
			movies = results.split("\n");
		} 
		//Catches any errors that may arise.
		catch (IOException e) {
			return null;
		} 
		finally { //After the try and catch are done executing, we need to "clean up"
			if (urlConnection != null) { //If the connection was valid, we want to close it by disconnecting
				urlConnection.disconnect();
			}
			if (reader != null) { //Similarly, we want to close the reader
				try { //It will try to close the reader, and if there are any problems, we will catch them. 
					reader.close();
				} 
				catch (IOException e) {
				}
			}
		}

		//Return our array of movies!
		return movies;
	}


	public static void main(String[] args) 
	{
		Scanner sc = new Scanner(System.in);

		boolean searching = true;

		while(searching) {					
			System.out.print("Enter a movie name to search for or type 'q' to quit: ");
			String searchTerm = sc.nextLine().trim();
			if(searchTerm.toLowerCase().startsWith("q")){
				searching = false;
			}
			else {
				String[] movies = downloadMovieData(searchTerm);
				for(String movie : movies) {
					System.out.println(movie);
				}
			}
		}
		sc.close();
	}
}
