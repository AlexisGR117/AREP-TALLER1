package edu.escuelaing.arem.ASE.app;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class MovieInfoServerTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MovieInfoServerTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( MovieInfoServerTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

    public void testParseTitleFromRequest() {
        String requestLine = "GET /path?title=Guardians%20Of%20The%20Galaxy HTTP/1.1";
        String titleValue = MovieInfoServer.parseTitleFromRequest(requestLine);
        assertEquals("Guardians%20Of%20The%20Galaxy", titleValue);
    }

    public void testParseParams() {
        String queryString = "title=Guardians%20Of%20The%20Galaxy&year=2010";
        Map<String, String> params = MovieInfoServer.parseParams(queryString);
        assertEquals(2, params.size());
        assertTrue(params.containsKey("title"));
        assertTrue(params.containsKey("year"));
        assertEquals("Guardians%20Of%20The%20Galaxy", params.get("title"));
        assertEquals("2010", params.get("year"));
    }

    public void testFetchMovieData() {
        MovieDataProvider movieDataProvider = new OMDbMovieDataProvider();
        String movieData = movieDataProvider.fetchMovieData("Guardians%20Of%20The%20Galaxy");
        assertEquals("{\"Title\":\"Guardians of the Galaxy\",\"Year\":\"2014\",\"Rated\":\"PG-13\",\"Released\":\"01 Aug 2014\",\"Runtime\":\"121 min\",\"Genre\":\"Action, Adventure, Comedy\",\"Director\":\"James Gunn\",\"Writer\":\"James Gunn, Nicole Perlman, Dan Abnett\",\"Actors\":\"Chris Pratt, Vin Diesel, Bradley Cooper\",\"Plot\":\"A group of intergalactic criminals must pull together to stop a fanatical warrior with plans to purge the universe.\",\"Language\":\"English\",\"Country\":\"United States\",\"Awards\":\"Nominated for 2 Oscars. 52 wins & 103 nominations total\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BNDIzMTk4NDYtMjg5OS00ZGI0LWJhZDYtMzdmZGY1YWU5ZGNkXkEyXkFqcGdeQXVyMTI5NzUyMTIz._V1_SX300.jpg\",\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"8.0/10\"},{\"Source\":\"Rotten Tomatoes\",\"Value\":\"92%\"},{\"Source\":\"Metacritic\",\"Value\":\"76/100\"}],\"Metascore\":\"76\",\"imdbRating\":\"8.0\",\"imdbVotes\":\"1,256,936\",\"imdbID\":\"tt2015381\",\"Type\":\"movie\",\"DVD\":\"15 Nov 2015\",\"BoxOffice\":\"$333,718,600\",\"Production\":\"N/A\",\"Website\":\"N/A\",\"Response\":\"True\"}", movieData);
    }

}
