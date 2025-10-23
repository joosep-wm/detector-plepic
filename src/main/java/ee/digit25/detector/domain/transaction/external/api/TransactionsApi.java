package ee.digit25.detector.domain.transaction.external.api;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface TransactionsApi {

    @GET("/transactions/unverified")
    Call<List<TransactionModel>> getUnverified(@Header("Authorization") String token, @Query("amount") int amount);

    @POST("/transactions/{id}/verify")
    Call<Void> verify(@Header("Authorization") String token, @Path("id") String transactionId);

    @POST("/transactions/{id}/reject")
    Call<Void> reject(@Header("Authorization") String token, @Path("id") String transactionId);

    @POST("/transactions/verify")
    Call<Void> verify(@Header("Authorization") String token, @Body List<String> ids);

    @POST("/transactions/reject")
    Call<Void> reject(@Header("Authorization") String token, @Body List<String> ids);
}
