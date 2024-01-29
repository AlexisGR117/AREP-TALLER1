package edu.escuelaing.arem.ASE.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application to consult information about movies.
 *
 * @author Jefer Alexis Gonzalez Romero
 * @version 1.0
 * @since 2024-01-28
 */
public class MovieInfoServer {
    private static final Logger LOGGER = Logger.getLogger(MovieInfoServer.class.getName());
    private static final ConcurrentHashMap<String, String> CACHE = new ConcurrentHashMap<>();
    private static final MovieDataProvider movieDataProvider = new OMDbMovieDataProvider();

    /**
     * Start the server and start listening to client requests.
     *
     * @param args They are not used.
     */
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(35000)) {
            while (true) {
                LOGGER.info("Listo para recibir ...");
                handleClientRequest(serverSocket.accept());
            }
        } catch (IOException e) {
            LOGGER.info("Could not listen on port: 35000.");
            System.exit(1);
        }
    }

    /**
     * Handles a single client request.
     *
     * @param clientSocket The Socket object representing the client connection.
     */
    public static void handleClientRequest(Socket clientSocket) {
        try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String requestLine = in.readLine();
            LOGGER.log(Level.INFO, "Received:{0}", requestLine);
            String titleValue = parseTitleFromRequest(requestLine);
            String outputLine = handleTitleValue(titleValue);
            out.println(outputLine);
            clientSocket.close();
        } catch (IOException e) {
            LOGGER.info("Accept failed.");
            System.exit(1);
        }
    }

    /**
     * Handles the provided title value, either fetching movie data or generating default HTML.
     *
     * @param titleValue The title value extracted from the client request.
     * @return The response to send back to the client, either the fetched movie data in HTML format or default HTML content.
     */
    public static String handleTitleValue(String titleValue) {
        if (titleValue != null) {
            return CACHE.computeIfAbsent(titleValue, movieDataProvider::fetchMovieData);
        } else {
            return generateDefaultHtml();
        }
    }

    /**
     * Parses the title value from the given request line.
     *
     * @param requestLine The request line from the client.
     * @return The title value extracted from the query string, or null if not found.
     */
    public static String parseTitleFromRequest(String requestLine) {
        if ((requestLine.startsWith("GET") || requestLine.startsWith("POST")) && requestLine.contains("?")) {
            String queryString = requestLine.split(" ")[1].split("\\?")[1];
            Map<String, String> params = parseParams(queryString);
            return params.get("title");
        } else {
            return null;
        }
    }

    /**
     * Parses query parameters from the given query string.
     *
     * @param queryString The query string containing key-value pairs.
     * @return A Map containing the parsed parameters, where keys are parameter names and values are parameter values.
     */
    public static Map<String, String> parseParams(String queryString) {
        Map<String, String> params = new HashMap<>();
        for (String param : queryString.split("&")) {
            String[] nameValue = param.split("=");
            params.put(nameValue[0], nameValue[1]);
        }
        return params;
    }

    /**
     * Generates default HTML content to be displayed when a title value is not provided.
     *
     * @return The generated HTML content as a String.
     */
    public static String generateDefaultHtml() {
        return generateHtmlHeader() +
                generateHtmlBody();
    }

    /**
     * Generates the CSS styles for the HTML content.
     *
     * @return The generated CSS styles as a String.
     */
    public static String generateCSS() {
        return "\n" +
                "           body {\n" +
                "               color: #fff;\n" +
                "               background-color: #000;\n" +
                "               font-family: Roboto,Helvetica,Arial,sans-serif;\n" +
                "           }\n" +
                "\n" +
                "           #title {\n" +
                "               font: bold 30px Roboto, Helvetica, Arial, sans-serif;\n" +
                "           }\n" +
                "\n" +
                "           ul {\n" +
                "               list-style-type: none;\n" +
                "           }\n" +
                "\n" +
                "           li {\n" +
                "               border-color: #484848;\n" +
                "               border-top-width: 2px;\n" +
                "               border-top-style: solid;\n" +
                "               padding: 6px;\n" +
                "           }\n" +
                "\n" +
                "           #genres {\n" +
                "               margin-top: 20px;\n" +
                "               margin-bottom: 20px;\n" +
                "           }\n" +
                "\n" +
                "           span {\n" +
                "               border: 1px solid white;\n" +
                "               border-radius: 1rem;\n" +
                "               margin: 5px;\n" +
                "               padding: 6px;\n" +
                "           }\n" +
                "\n" +
                "           .centered-text {\n" +
                "               text-align: center;\n" +
                "           }\n" +
                "\n" +
                "           .fixed-width {\n" +
                "               width: 50%;\n" +
                "           }\n" +
                "\n" +
                "           .centered {\n" +
                "               margin-left: auto;\n" +
                "               margin-right: auto;\n" +
                "           }\n" +
                "\n" +
                "          #movie-information {\n" +
                "              display: none;\n" +
                "          }\n";
    }

    /**
     * Generates the HTML header content, including HTTP status, content type, charset,
     * DOCTYPE declaration, basic HTML structure, and CSS styles.
     *
     * @return The generated HTML header as a String.
     */
    public static String generateHtmlHeader() {
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type:text/html; charset=ISO-8859-1\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <title>Search movies</title>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "        <style>\n" +
                generateCSS() +
                "        </style>\n" +
                "    </head>\n";
    }

    /**
     * Generates a JavaScript function that fetches movie data from the server using a fetch request.
     *
     * @return The generated JavaScript function as a String.
     */
    public static String functionFetchMovieData() {
        return "async function fetchMovieData(movieTitle) {\n" +
                "    try {\n" +
                "        const response = await fetch(`/movies?title=${movieTitle}`, {\n" +
                "            responseType: 'json'\n" +
                "        });\n" +
                "        const movieData = await response.json();\n" +
                "        return movieData;\n" +
                "    } catch (error) {\n" +
                "        document.getElementById(\"title\").innerHTML = \"There was an error, the query could not be performed\";\n" +
                "        document.getElementById(\"movie-information\").style.display = \"none\";\n" +
                "        return null;\n" +
                "    }\n" +
                "}\n";
    }

    /**
     * Generates a JavaScript function that displays movie information on the HTML page,
     * handling both successful and error responses from the server.
     *
     * @return The generated JavaScript function as a String.
     */
    public static String functionDisplayMovieInformation() {
        return "function displayMovieInformation(movie) {\n" +
                "    if (movie.Response == \"True\") {\n" +
                "      displayGeneralInformation(movie);\n" +
                "      displayGenres(movie);\n" +
                "      displayCredits(movie);\n" +
                "      displayPlotAndDetails(movie);\n" +
                "      document.getElementById(\"movie-information\").style.display = \"block\";\n" +
                "    } else {\n" +
                "      document.getElementById(\"title\").innerHTML = movie.Error;\n" +
                "      document.getElementById(\"movie-information\").style.display = \"none\";\n" +
                "    }\n" +
                "}\n";
    }

    /**
     * Generates a JavaScript function that displays general movie information,
     * including title, year, poster, rating, and release date.
     *
     * @return The generated JavaScript function as a String.
     */
    public static String functionDisplayGeneralInformation() {
        return "function displayGeneralInformation(movie) {\n" +
                "    document.getElementById(\"title\").innerHTML = `${movie.Title} (${movie.Year})`;\n" +
                "    document.getElementById(\"poster\").src = movie.Poster;\n" +
                "    document.getElementById(\"rated\").innerHTML = `<b>Rated: </b>${movie.Rated}`;\n" +
                "    document.getElementById(\"released\").innerHTML = `<b>Released: </b>${movie.Released}`;\n" +
                "}\n";
    }

    /**
     * Generates a JavaScript function that displays movie genres as individual spans.
     *
     * @return The generated JavaScript function as a String.
     */
    public static String functionDisplayGenres() {
        return "function displayGenres(movie) {\n" +
                "    const genresDiv = document.getElementById(\"genres\");\n" +
                "    genresDiv.innerHTML = \"\";\n" +
                "    movie.Genre.split(\", \").forEach((genre) => {\n" +
                "      genresDiv.appendChild(document.createElement(\"span\")).textContent = genre;\n" +
                "    });\n" +
                "}\n";
    }

    /**
     * Generates a JavaScript function that displays movie credits,
     * including director, writer, and actors.
     *
     * @return The generated JavaScript function as a String.
     */
    public static String functionDisplayCredits() {
        return "function displayCredits(movie) {\n" +
                "    document.getElementById(\"director\").innerHTML = `<b>Director: </b>${movie.Director}`;\n" +
                "    document.getElementById(\"writer\").innerHTML = `<b>Writer: </b>${movie.Writer}`;\n" +
                "    document.getElementById(\"actors\").innerHTML = `<b>Actors: </b>${movie.Actors}`;\n" +
                "}\n";
    }

    /**
     * Generates a JavaScript function that displays additional movie details,
     * including plot, language, country, awards, ratings, Metascore, IMDB information,
     * type, DVD, box office, production, and website.
     *
     * @return The generated JavaScript function as a String.
     */
    public static String functionDisplayPlotAndDetails() {
        return "function displayPlotAndDetails(movie) {\n" +
                "    document.getElementById(\"plot\").innerHTML = movie.Plot;\n" +
                "    document.getElementById(\"language\").innerHTML = `<b>Language: </b>${movie.Language}`;\n" +
                "    document.getElementById(\"country\").innerHTML = `<b>Country: </b>${movie.Country}`;\n" +
                "    document.getElementById(\"awards\").innerHTML = `<b>Awards: </b>${movie.Awards}`;\n" +
                "    displayRatings(movie.Ratings);\n" +
                "    document.getElementById(\"metascore\").innerHTML = `<b>Metascore: </b>${movie.Metascore}`;\n" +
                "    document.getElementById(\"imdb-rating\").innerHTML = `<b>IMDB Rating: </b>${movie.imdbRating}`;\n" +
                "    document.getElementById(\"imdb-votes\").innerHTML = `<b>IMDB Votes: </b>${movie.imdbVotes}`;\n" +
                "    document.getElementById(\"imdb-id\").innerHTML = `<b>IMDB ID: </b>${movie.imdbID}`;\n" +
                "    document.getElementById(\"type\").innerHTML = `<b>Type: </b>${movie.Type}`;\n" +
                "    document.getElementById(\"dvd\").innerHTML = `<b>DVD: </b>${movie.DVD}`;\n" +
                "    document.getElementById(\"box-office\").innerHTML = `<b>Box Office: </b>${movie.BoxOffice}`;\n" +
                "    document.getElementById(\"production\").innerHTML = `<b>Production: </b>${movie.Production}`;\n" +
                "    document.getElementById(\"website\").innerHTML = `<b>Website: </b>${movie.Website}`;\n" +
                "}\n";
    }

    /**
     * Generates a JavaScript function that displays movie ratings from different sources.
     *
     * @return The generated JavaScript function as a String.
     */
    public static String functionDisplayRatings() {
        return "function displayRatings(ratings) {\n" +
                "    const ratingsLi = document.getElementById(\"ratings\");\n" +
                "    ratingsLi.innerHTML = \"<b>Ratings: </b>\";\n" +
                "    ratings.forEach((rating) => {\n" +
                "      ratingsLi.insertAdjacentText(\"beforeend\", \"[\" + rating.Source + \" - \" + rating.Value + \"] \");\n" +
                "    });\n" +
                "}\n";
    }

    /**
     * Generates a JavaScript function that handles the "Search" button click,
     * fetching movie data for the entered title and displaying it on the page.
     *
     * @return The generated JavaScript function as a String.
     */
    public static String functionLoadGetMsg() {
        return "async function loadGetMsg() {\n" +
                "    const movieTitle = document.getElementById(\"movie-title\").value;\n" +
                "    const movieData = await fetchMovieData(movieTitle);\n" +
                "    displayMovieInformation(movieData);\n" +
                "}\n";
    }

    /**
     * Generates the JavaScript code for the HTML page, including all the movie-related functions.
     *
     * @return The generated JavaScript code as a String.
     */
    public static String generateScript() {
        return functionFetchMovieData() +
                "\n" +
                functionDisplayMovieInformation() +
                "\n" +
                functionDisplayGeneralInformation() +
                "\n" +
                functionDisplayGenres() +
                "\n" +
                functionDisplayCredits() +
                "\n" +
                functionDisplayPlotAndDetails() +
                "\n" +
                functionDisplayRatings() +
                "\n" +
                functionLoadGetMsg();
    }

    /**
     * Generates the HTML body content, including the search form, movie information container,
     * and JavaScript code.
     *
     * @return The generated HTML body as a String.
     */
    public static String generateHtmlBody() {
        return "    <body>\n" +
                "        <h1>Search movies</h1>\n" +
                "        <form action=\"/hello\">\n" +
                "            <label for=\"name\">Movie title:</label><br>\n" +
                "            <input type=\"text\" id=\"movie-title\" name=\"name\" value=\"Guardians of the galaxy\"><br><br>\n" +
                "            <input type=\"button\" value=\"Search\" onclick=\"loadGetMsg()\">\n" +
                "        </form> \n" +
                "        <div id=\"title\" class=\"centered-text\"></div>\n" +
                "        <div id=\"movie-information\">\n" +
                "        <div id=\"img-movie\" class=\"centered-text\"><img id=\"poster\" src=\"\"/></div>\n" +
                "        <div id=\"genres\" class=\"centered-text fixed-width centered\"></div>\n" +
                "        <div id=\"plot\" class=\"centered-text fixed-width centered\"></div>\n" +
                generateUnorderedList() +
                "        </div>\n" +
                "\n" +
                "        <script>\n" +
                generateScript() +
                "        </script>\n" +
                "\n" +
                "    </body>\n" +
                "</html>";
    }

    /**
     * Generates an unordered list containing elements for various movie details.
     *
     * @return The generated unordered list as a String.
     */
    public static String generateUnorderedList() {
        return "        <ul class=\"fixed-width centered\">\n" +
                "            <li id=\"rated\"></li>\n" +
                "            <li id=\"released\"></li>\n" +
                "            <li id=\"director\"></li>\n" +
                "            <li id=\"writer\"></li>\n" +
                "            <li id=\"actors\"></li>\n" +
                "            <li id=\"language\"></li>\n" +
                "            <li id=\"country\"></li>\n" +
                "            <li id=\"awards\"></li>\n" +
                "            <li id=\"ratings\"></li>\n" +
                "            <li id=\"metascore\"></li>\n" +
                "            <li id=\"imdb-rating\"></li>\n" +
                "            <li id=\"imdb-votes\"></li>\n" +
                "            <li id=\"imdb-id\"></li>\n" +
                "            <li id=\"type\"></li>\n" +
                "            <li id=\"dvd\"></li>\n" +
                "            <li id=\"box-office\"></li>\n" +
                "            <li id=\"production\"></li>\n" +
                "            <li id=\"website\"></li>\n" +
                "        </ul>\n";
    }
}

