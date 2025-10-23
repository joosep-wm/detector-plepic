package ee.digit25.detector.domain.device.external.api;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface DeviceApi {

    @GET("/devices/{mac}")
    Call<DeviceModel> get(@Header("Authorization") String token, @Path("mac") String mac);

    @POST("/devices/by-macs")
    Call<List<DeviceModel>> get(@Header("Authorization") String token, @Body List<String> macs);

    @GET("/devices")
    Call<List<DeviceModel>> get(@Header("Authorization") String token, @Query("pageNumber") int pageNumber, @Query("pageSize") int pageSize);
}
