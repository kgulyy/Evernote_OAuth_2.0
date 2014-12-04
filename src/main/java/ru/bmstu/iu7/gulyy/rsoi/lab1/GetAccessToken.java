package ru.bmstu.iu7.gulyy.rsoi.lab1;

import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteStore;
import com.evernote.edam.type.Notebook;
import com.evernote.thrift.TException;
import com.evernote.thrift.protocol.TBinaryProtocol;
import com.evernote.thrift.transport.THttpClient;
import com.evernote.thrift.transport.TTransportException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Константин on 04.12.2014.
 */
public class GetAccessToken extends HttpServlet {
    private static final Pattern NOTE_STORE_URL = Pattern
            .compile("edam_noteStoreUrl=([^&]+)");
    private static final Pattern OAUTH_TOKEN = Pattern
            .compile("oauth_token=([^&]+)");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Get oauth_token and oauth_verifier
        String oauthToken = req.getParameter("oauth_token");
        String oauthVerifier = req.getParameter("oauth_verifier");

        System.out.println("Got the OAuth Verifies:");
        System.out.print("oauth_verifier=");
        System.out.println(oauthVerifier);
        System.out.println();

        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://www.evernote.com/oauth");

        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(7);
        params.add(new BasicNameValuePair("oauth_consumer_key", "lokochemp2010"));
        params.add(new BasicNameValuePair("oauth_signature", "b1d2a159e7ced274%26"));
        params.add(new BasicNameValuePair("oauth_signature_method", "PLAINTEXT"));
        params.add(new BasicNameValuePair("oauth_timestamp", "1288364923"));
        params.add(new BasicNameValuePair("oauth_nonce", "755d38e6d163e820"));
        params.add(new BasicNameValuePair("oauth_token", oauthToken));
        params.add(new BasicNameValuePair("oauth_verifier", oauthVerifier));
        httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        //Execute and get the response.
        HttpResponse postResponse = httpclient.execute(httpPost);

        BufferedReader rd = new BufferedReader(new InputStreamReader(postResponse.getEntity().getContent()));
        StringBuffer stringPostResponse = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            stringPostResponse.append(line);
        }

        System.out.println("The response:");
        System.out.println(stringPostResponse);
        System.out.println();

        // Get the oauth token
        String authToken = URLDecoder.decode(getOAuthToken(stringPostResponse.toString()), "UTF-8");
        System.out.println("Got the OAuth Token:");
        System.out.println(authToken);
        System.out.println();

        // Get the noteStore URL
        String noteStoreUrl = URLDecoder.decode(getNoteStoreUrl(stringPostResponse.toString()),"UTF-8");
        System.out.println("Got the noteStore URL:");
        System.out.println(noteStoreUrl);
        System.out.println();

        // Output private data
        THttpClient noteStoreTrans = null;
        try {
            noteStoreTrans = new THttpClient(noteStoreUrl);
        } catch (TTransportException e) {
            e.printStackTrace();
        }

        TBinaryProtocol noteStoreProt = new TBinaryProtocol(noteStoreTrans);
        NoteStore.Client noteStore = new NoteStore.Client(noteStoreProt, noteStoreProt);

        List<Notebook> notebooks = null;
        try {
            notebooks = noteStore.listNotebooks(authToken);
        } catch (EDAMUserException e) {
            e.printStackTrace();
        } catch (EDAMSystemException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }

        resp.setContentType("text/html; charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);


        PrintWriter out = resp.getWriter();
        out.println("<html><body>");
        out.println("<h2>My Evernote notebooks</h2><ul>");
        for (Notebook notebook : notebooks) {
            out.println("<li>Notebook: " + notebook.getName() + "</li>");
        }
        out.println("</ul></body></html>");
    }

    private String getNoteStoreUrl(String response) {
        Matcher matcher = NOTE_STORE_URL.matcher(response);
        if (matcher.find() && matcher.groupCount() >= 1 && matcher.group(1) != null) {
            return matcher.group(1);
        } else {
            throw new RuntimeException("Response body is incorrect. "
                    + "Can't extract token and secret from this: '" + response + "'");
        }
    }

    private String getOAuthToken(String response) {
        Matcher matcher = OAUTH_TOKEN.matcher(response);
        if (matcher.find() && matcher.groupCount() >= 1 && matcher.group(1) != null) {
            return matcher.group(1);
        } else {
            throw new RuntimeException("Response body is incorrect. "
                    + "Can't extract token and secret from this: '" + response + "'");
        }
    }
}

