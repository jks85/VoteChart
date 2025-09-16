package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.stream.Stream;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;



public class PartiesApplication extends Application {

    public static void main(String[] args) {
        System.out.println("Displaying chart...");
        launch(PartiesApplication.class);
    }

    public void start(Stage window){
        // create axes
        NumberAxis xAxis = new NumberAxis(1968, 2010,4);
        NumberAxis yAxis = new NumberAxis(0, 30, 5);
        xAxis.setLabel("Year");
        yAxis.setLabel("Vote Share %");

        // create line chart
        LineChart<Number, Number> votesLineChart = new LineChart<>(xAxis, yAxis);

        // read data from file into a list of arrays using stream
            
        List<String[]> fileLines = new ArrayList<>();

        Path filePath = Path.of("partiesdata.tsv");
        // System.out.println("Printing fileLines...");
        // System.out.println("fileLines");
        try(Stream<String> voteDataStream = Files.lines(filePath)){
            // read lines into a list
            voteDataStream.forEach((line)-> {
                String[] lineData = line.split("\t");
                fileLines.add(lineData);
            });
        }
        catch(Exception e){
            System.out.println("File does not exist");
        }

        // create hashmap of data from list
        Map<String, Map <Integer, Double>> partyInfoMap = createVoteMap(fileLines);

        
        for (String party : partyInfoMap.keySet()){
            System.out.println("Party: " + party + " " + partyInfoMap.get(party));
        }

        // create series and data points for each party
        
        for (String party : partyInfoMap.keySet()){
            XYChart.Series<Number,Number> partyVoteSeries = new XYChart.Series<>();
            partyVoteSeries.setName(party);
            Set<Map.Entry<Integer,Double>> partyVotesEachYear = partyInfoMap.get(party).entrySet();
            for (Map.Entry<Integer,Double> voteInfo : partyVotesEachYear){
                Integer year = voteInfo.getKey();
                Double votes = voteInfo.getValue();
                partyVoteSeries.getData().add(new XYChart.Data<Number, Number>(year, votes));
            }
            votesLineChart.getData().add(partyVoteSeries);
        }

        
        // add styling?
        votesLineChart.setTitle("Finland Political Party Vote Share by Year");

        // set scene
        Scene chartScene = new Scene(votesLineChart);
        window.setScene(chartScene);

        // set stage
        window.show();

        // auto-exit during testing
        // System.exit(0);
    }

    public Map<String, Map<Integer,Double>> createVoteMap(List<String[]> fileLines){
        // uses .txt file of voting data to create a hashmap
        // map is constructed based on the format of this particular tab separated .txt doc
        // year, party, and vote data is mixed across lines but systematically formatted
        Map<String, Map <Integer, Double>> partyInfoMap = new HashMap<>();
        String[] years = fileLines.get(0);
        for (int lineNum=1; lineNum < fileLines.size(); lineNum++){
            String[] currentLine = fileLines.get(lineNum);
            String currentParty = currentLine[0];
            Map<Integer,Double> voteInfoMap = new HashMap<>();
            for (int entryIndex = 1; entryIndex < currentLine.length; entryIndex++){
                if (!currentLine[entryIndex].equals("-")){
                    Integer year = Integer.valueOf(years[entryIndex]);
                    Double voteData = Double.valueOf(currentLine[entryIndex]);
                    voteInfoMap.put(year, voteData);
                }
            }
            partyInfoMap.put(currentParty,voteInfoMap);
        }

        return partyInfoMap;
    }

}
