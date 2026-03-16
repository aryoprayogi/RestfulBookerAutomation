package api.endpoints;

public class Routes {
    //URL list of Restful-Booker API

    // 1. Base URL:
    public static String base_url = "https://restful-booker.herokuapp.com";

    // 2. Create Token Endpoint (Auth)
    public static String auth_url = base_url + "/auth";

    // 3. CRUD Endpoint
    public static String post_url = base_url + "/booking";
    public static String get_url = base_url + "/booking/{id}";
    public static String update_url = base_url + "/booking/{id}";
    public static String delete_url = base_url + "/booking/{id}";
}
