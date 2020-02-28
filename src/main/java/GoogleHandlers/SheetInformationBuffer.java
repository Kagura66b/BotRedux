package GoogleHandlers;

import Handlers.CraftHandler;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SheetInformationBuffer {
    public static final String ID = "1U4ilMqpE7rINHN3P5EEUmXr_f0hZY-geWLuPReTRWNg";
    public static final String itemsID = "12Y06S_7WHCj6gYXVZLcph0PSpe2sdiudErWamr7z0Ck";
    private static List<String[]> itemArray;
    private static Sheets sheets;

    public static List<List<String>> retrieveSheetData() throws IOException, GeneralSecurityException {
        sheets = SheetsBuilder.getSheets();
        String range = "Bot Friendly";
        Sheets.Spreadsheets.Values.Get request =
                sheets.spreadsheets().values().get(itemsID, range);

        List<List<Object>> response = request.execute().getValues();
        List<List<String>> sheetData = new ArrayList<>(new ArrayList<>());
        for(List<Object> row:response){
            List<String> cellText = new ArrayList<>();
            for(Object cell:row){
                cellText.add(cell.toString());
            }
            sheetData.add(cellText);
        }
        return sheetData;
    }

    public static void processCommand(String command, CraftHandler importCrafts) throws GeneralSecurityException, IOException {
        sheets = SheetsBuilder.getSheets();
        itemArray = importCrafts.getCraftBacklog();
        command = command.toLowerCase();
        String[] parsing = command.split(" ");
        switch(parsing[1]){
            case "write":
                writeToSheet();
                break;
            case "read":
                readFromSheet();
                break;
        }
    }

    private static void readFromSheet() throws IOException {
        String range = "Bot Friendly";
        Sheets.Spreadsheets.Values.Get request =
                sheets.spreadsheets().values().get(ID, range);

        List<List<Object>> response = request.execute().getValues();
        StringBuilder build = new StringBuilder();
        for(List<Object> row:response){
            StringBuilder out = new StringBuilder();
            for(Object cell:row){
                out.append(cell).append(", ");
            }
            build.append(out).append("\n");
        }
        String output = build.toString();
        System.out.println(output);
    }

    private static void writeToSheet() throws IOException {
        List<List<Object>> outputToBody = new ArrayList<>();
        for(String[] entryList:itemArray){
            List<Object> builder = new ArrayList<>(Arrays.asList(entryList));
            outputToBody.add(builder);
        }

        ValueRange body = new ValueRange().setValues(outputToBody);

        sheets.spreadsheets().values()
                .append(ID, "A1", body)
                .setValueInputOption("RAW")
                .setInsertDataOption("INSERT_ROWS")
                .setIncludeValuesInResponse(true)
                .execute();

        //sheets.spreadsheets().values().
    }

}
