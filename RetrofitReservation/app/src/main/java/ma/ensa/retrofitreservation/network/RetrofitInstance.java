package ma.ensa.retrofitreservation.network;

import com.google.gson.Gson;
import java.util.concurrent.TimeUnit;
import ma.ensa.retrofitreservation.service.ApiInterface;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static Retrofit retrofit = null;
    private static final String BASE_URL = "http://192.168.0.217:8082/";

    // Configuration of the HTTP client with custom timeouts
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    // Gson instance for custom usage
    private static final Gson gson = new Gson();

    // Method to get a Retrofit instance
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client) // Adding the OkHttp client with configured timeouts
                    .addConverterFactory(GsonConverterFactory.create(gson)) // Pass custom Gson instance
                    .build();
        }
        return retrofit;
    }

    // Method to get an instance of the API Interface
    public static ApiInterface getApi() {
        return getRetrofitInstance().create(ApiInterface.class);
    }

    // Method to get the Gson instance used in Retrofit
    public static Gson getGson() {
        return gson;
    }
}
