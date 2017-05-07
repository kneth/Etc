package net.zigzak.etc;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface TextService {
    @GET("etc{etcid}.xml")
    Call<Text> getStrip(
        @Path("etcid") String etcid
    );
}
