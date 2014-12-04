package ru.bmstu.iu7.gulyy.rsoi.lab1;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Константин on 02.12.2014.
 */
public class RequestLoginPage extends HttpServlet {
    private static final Pattern TOKEN_REGEX = Pattern
            .compile("oauth_token=([^&]+)");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String url = "https://www.evernote.com/oauth"   +
                "?oauth_consumer_key=lokochemp2010"         +
                "&oauth_signature=b1d2a159e7ced274%26"      +
                "&oauth_signature_method=PLAINTEXT"         +
                "&oauth_timestamp=1288364369"               +
                "&oauth_nonce=d3d9446802a44259"             +
                "&oauth_callback=http://localhost:8071/GetAccessToken?action=oauthCallback";

        System.out.println("=== Evernote's OAuth Workflow ===");
        System.out.println();

        // Obtain the Request Token via GET request
        HttpClient client = new DefaultHttpClient();
        HttpGet getRequest = new HttpGet(url);

        System.out.println("Fetching the Request Token...");
        HttpResponse getResponse = client.execute(getRequest);

        // Get the response
        BufferedReader rd = new BufferedReader
                (new InputStreamReader(getResponse.getEntity().getContent()));

        StringBuffer stringGetResponse = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            stringGetResponse.append(line);
        }

        System.out.println("The response:");
        System.out.println(stringGetResponse);
        System.out.println();

        // Get the request token
        String requestToken = getRequestToken(stringGetResponse.toString());
        System.out.println("Got the Request Token:");
        System.out.println(requestToken);
        System.out.println();

        // Show link to authorization in Evernote
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = resp.getWriter();

        String linkToAuth = "<h3><a href=\"https://www.evernote.com/OAuth.action?oauth_token=" + requestToken  + "\">Go to the Evernote login/authorization page</a></h3>";

        out.println("<html><body>");
        out.println(linkToAuth);
        out.println("</body></html>");
    }

    private String getRequestToken(String response) {
        Matcher matcher = TOKEN_REGEX.matcher(response);
        if (matcher.find() && matcher.groupCount() >= 1 && matcher.group(1) != null) {
            return matcher.group(1);
        } else {
            throw new RuntimeException("Response body is incorrect. "
                    + "Can't extract token and secret from this: '" + response + "'");
        }
    }
}

