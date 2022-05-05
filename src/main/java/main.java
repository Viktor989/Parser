import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class main {
    public static void main(String[] args) {
        //задача 1
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "new-data.json");

        //задача 2
        List<Employee> listXml = parseXML("data.xml");
        String jsonXml = listToJson(list);
        writeString(jsonXml, "new-data2.json");
        
    }
    private static List<Employee> parseXML(String fileName) {
        List<Employee> staff = new ArrayList<>();
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(fileName);
            Node root = document.getDocumentElement();
            staff = read(root);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return staff;
    }
    private static List<Employee> read(Node root) {
        List<Employee> list = new ArrayList<>();
        NodeList nodeList = root.getChildNodes();
        int age = 0;
        long id = 0;
        String firstName = null, lastName = null, country = null;
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            NodeList nodeList_ = node_.getChildNodes();
            if (Node.ELEMENT_NODE == node_.getNodeType()) {
                for (int j = 0; j < nodeList_.getLength(); j++) {
                    Node node_1 = nodeList_.item(j);
                    if (Node.ELEMENT_NODE == node_1.getNodeType()) {
                        switch (nodeList_.item(j).getNodeName()) {
                            case "id":
                                id = Long.parseLong(nodeList_.item(j).getTextContent());
                                break;
                            case "firstName":
                                firstName = nodeList_.item(j).getTextContent();
                                break;
                            case "lastName":
                                lastName = nodeList_.item(j).getTextContent();
                                break;
                            case "country":
                                country = nodeList_.item(j).getTextContent();
                                break;
                            case "age":
                                age = Integer.parseInt(nodeList_.item(j).getTextContent());
                                break;
                        }
                        list.add(new Employee(id, firstName, lastName, country, age));
                    }
                }
                read(node_);
            }
        }
        return list;
    }
    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> staff = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }
    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }
                .getType();
        String json = gson.toJson(list, listType);
        return json;
    }
    private static void writeString(String json, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
