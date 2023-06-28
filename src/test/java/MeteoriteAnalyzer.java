import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeteoriteAnalyzer {
    public static void main(String[] args) {

        String relativeFolderPath = "src/test/resources";

        String absoluteFolderPath = new File(relativeFolderPath).getAbsolutePath();

        File folder = new File(absoluteFolderPath);

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, YearStatistics> yearStatisticsMap = new HashMap<>();

        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".json")) {
                    try {
                        List<Meteorite> meteorites = objectMapper.readValue(file,
                                objectMapper.getTypeFactory().constructCollectionType(List.class, Meteorite.class));

                        for (Meteorite meteorite : meteorites) {
                            String year=null;
                            try {
                                 year = meteorite.getYear().substring(0, 4);
                            }catch (Exception e){continue;}
                            double mass=0.0;
                            try{
                             mass = Double.parseDouble(meteorite.getMass());
                        }catch (Exception e){continue;}
                            YearStatistics yearStatistics = yearStatisticsMap.getOrDefault(year, new YearStatistics());
                            yearStatistics.addMass(mass);
                            yearStatistics.incrementCount();

                            yearStatisticsMap.put(year, yearStatistics);
                        }
                    } catch (IOException e) {
                        continue;

                    }
                }
            }
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<String, YearStatistics> entry : yearStatisticsMap.entrySet()) {
            String year = entry.getKey();
            YearStatistics yearStatistics = entry.getValue();
            double averageMass = yearStatistics.getAverageMass();
            dataset.addValue(averageMass, "Average Mass", year);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Average Grams per Year",
                "Year",
                "Average Mass (grams)",
                dataset
        );

        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.BLACK);
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(0, 128, 0));
        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setTickLabelFont(xAxis.getTickLabelFont().deriveFont(10.0f));
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setTickUnit(new NumberTickUnit(1000));
        yAxis.setRange(0, 3000);
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        try {
            String outputFilePath = "graphic.png";
            ChartUtils.saveChartAsPNG(new File(outputFilePath), chart, 800, 600);
            System.out.println("Chart saved successfully as image (graphic.png) in: "+ System.getProperty("user.dir") +"/"+ outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Meteorite {
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNametype() {
            return nametype;
        }

        public void setNametype(String nametype) {
            this.nametype = nametype;
        }

        public String getRecclass() {
            return recclass;
        }

        public void setRecclass(String recclass) {
            this.recclass = recclass;
        }

        public String getMass() {
            if(null==mass){return "0.0";}
            return mass
                    ;
        }

        public void setMass(String mass) {
            this.mass = mass;
        }

        public String getFall() {
            return fall;
        }

        public void setFall(String fall) {
            this.fall = fall;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getReclat() {
            return reclat;
        }

        public void setReclat(String reclat) {
            this.reclat = reclat;
        }

        public String getReclong() {
            return reclong;
        }

        public void setReclong(String reclong) {
            this.reclong = reclong;
        }

        public GeoLocation getGeolocation() {
            return geolocation;
        }

        public void setGeolocation(GeoLocation geolocation) {
            this.geolocation = geolocation;
        }

        @JsonProperty("name")
        private String name;

        @JsonProperty("id")
        private String id;

        @JsonProperty("nametype")
        private String nametype;

        @JsonProperty("recclass")
        private String recclass;

        @JsonProperty("mass")
        private String mass;

        @JsonProperty("fall")
        private String fall;

        @JsonProperty("year")
        private String year;

        @JsonProperty("reclat")
        private String reclat;

        @JsonProperty("reclong")
        private String reclong;

        @JsonProperty("geolocation")
        private GeoLocation geolocation;

        private static class GeoLocation {
            @JsonProperty("latitude")
            private String latitude;

            @JsonProperty("longitude")
            private String longitude;

        }
    }

    private static class YearStatistics {
        private double totalMass;
        private int count;

        public YearStatistics() {
            totalMass = 0;
            count = 0;
        }

        public void addMass(double mass) {
            totalMass += mass;
        }

        public void incrementCount() {
            count++;
        }

        public double getAverageMass() {
            if (count > 0) {
                return totalMass / count;
            }
            return 0;
        }
    }
}
