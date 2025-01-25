package ma.ensaj.reservationretrofit_kotlin.network

import ma.ensaj.reservationretrofit_kotlin.service.ApiInterface
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.google.gson.Gson

object RetrofitInstance {

    private var retrofit: Retrofit? = null
    private const val BASE_URL = "http://192.168.0.217:8082/"

    // Configuration du client HTTP avec des délais personnalisés
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // Gson instance for custom usage
    private val gson: Gson = Gson()

    // Méthode pour obtenir une instance de Retrofit
    @JvmStatic
    fun getRetrofitInstance(): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client) // Ajout du client OkHttp avec les délais configurés
                .addConverterFactory(GsonConverterFactory.create(gson)) // Pass custom Gson instance
                .build()
        }
        return retrofit!!
    }

    // Méthode pour obtenir une instance de l'API Interface
    @JvmStatic
    fun getApi(): ApiInterface {
        return getRetrofitInstance().create(ApiInterface::class.java)
    }

    // Méthode pour obtenir l'instance Gson utilisée dans Retrofit
    @JvmStatic
    fun getGson(): Gson {
        return gson
    }
}
