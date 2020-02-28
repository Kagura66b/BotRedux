package GoogleHandlers;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class Authorizer {
    public static HttpRequestInitializer authorize() throws GeneralSecurityException, IOException {
        /*InputStream in = new FileInputStream("C:\\Users\\kingo\\IdeaProjects\\credentials.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), new InputStreamReader(in));

        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), clientSecrets, scopes).setDataStoreFactory(new MemoryDataStoreFactory())
                .setAccessType("offline").build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

         */
        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);
        InputStream in = new FileInputStream("C:\\Users\\kingo\\IdeaProjects\\credentials.json");
        GoogleCredentials serviceCredentials = ServiceAccountCredentials.fromStream(in).createScoped(scopes);

        //GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(serviceCredentials);


        return requestInitializer;
    }
}
