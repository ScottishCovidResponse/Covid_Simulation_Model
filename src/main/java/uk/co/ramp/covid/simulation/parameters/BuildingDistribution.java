package uk.co.ramp.covid.simulation.parameters;

// Defines the number of types of building per N people
public class BuildingDistribution {
    public Integer hospitals = null;
    public PlaceSizeDistribution hospitalSizes = null;

    public Integer schools = null;
    public PlaceSizeDistribution schoolSizes = null;

    public Integer shops = null;
    public PlaceSizeDistribution shopSizes = null;

    public Integer offices = null;
    public PlaceSizeDistribution officeSizes = null;

    public Integer constructionSites = null;
    public PlaceSizeDistribution constructionSiteSizes = null;

    public Integer nurseries = null;
    public PlaceSizeDistribution nurserySizes = null;

    public Integer restaurants = null;
    public PlaceSizeDistribution restaurantSizes = null;

    public boolean isValid() {
        return hospitalSizes.isValid()
                && schoolSizes.isValid()
                && shopSizes.isValid()
                && officeSizes.isValid()
                && constructionSiteSizes.isValid()
                && nurserySizes.isValid()
                && restaurantSizes.isValid();
    }
}
