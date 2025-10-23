package ee.digit25.detector.domain.account.external.api;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface AccountApi {

    @GET("/accounts/{number}")
    Call<AccountModel> get(@Header("Authorization") String token, @Path("number") String accountNumber);

    @POST("/accounts/by-numbers")
    Call<List<AccountModel>> get(@Header("Authorization") String token, @Body List<String> numbers);

    @GET("/accounts")
    Call<List<AccountModel>> get(@Header("Authorization") String token, @Query("pageNumber") int pageNumber, @Query("pageSize") int pageSize);
}
