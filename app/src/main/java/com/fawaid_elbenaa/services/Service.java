package com.fawaid_elbenaa.services;


import com.fawaid_elbenaa.adapters.SingleCommentDataModel;
import com.fawaid_elbenaa.models.AdminMessageDataModel;
import com.fawaid_elbenaa.models.AdminRoomDataModel;
import com.fawaid_elbenaa.models.AllCatogryModel;
import com.fawaid_elbenaa.models.CommentDataModel;
import com.fawaid_elbenaa.models.CouponDataModel;
import com.fawaid_elbenaa.models.DepartmentDataModel;
import com.fawaid_elbenaa.models.GovernorateDataModel;
import com.fawaid_elbenaa.models.InvoiceModel;
import com.fawaid_elbenaa.models.ItemAddAdsDataModel;
import com.fawaid_elbenaa.models.MessageDataModel;
import com.fawaid_elbenaa.models.NewsModel;
import com.fawaid_elbenaa.models.NotificationDataModel;
import com.fawaid_elbenaa.models.OtherProfileDataModel;
import com.fawaid_elbenaa.models.PackageDataModel;
import com.fawaid_elbenaa.models.PlaceDataModel;
import com.fawaid_elbenaa.models.PlaceGeocodeData;
import com.fawaid_elbenaa.models.PlaceMapDetailsData;
import com.fawaid_elbenaa.models.ProductsDataModel;
import com.fawaid_elbenaa.models.RoomDataModel;
import com.fawaid_elbenaa.models.RoomDataModel2;
import com.fawaid_elbenaa.models.SettingDataModel;
import com.fawaid_elbenaa.models.SingleAdminMessageDataModel;
import com.fawaid_elbenaa.models.SingleCouponModel;
import com.fawaid_elbenaa.models.SingleMessageDataModel;
import com.fawaid_elbenaa.models.SingleProductDataModel;
import com.fawaid_elbenaa.models.SliderModel;
import com.fawaid_elbenaa.models.SponsorsModel;
import com.fawaid_elbenaa.models.StatusResponse;
import com.fawaid_elbenaa.models.TypeDataModel;
import com.fawaid_elbenaa.models.UserModel;
import com.fawaid_elbenaa.models.google_models.CategoryDataModel;
import com.fawaid_elbenaa.models.google_models.NearbyModel;
import com.fawaid_elbenaa.models.google_models.PlaceDetailsModel;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface Service {

    @GET("place/findplacefromtext/json")
    Call<PlaceMapDetailsData> searchOnMap(@Query(value = "inputtype") String inputtype,
                                          @Query(value = "input") String input,
                                          @Query(value = "fields") String fields,
                                          @Query(value = "language") String language,
                                          @Query(value = "key") String key
    );

    @GET("geocode/json")
    Call<PlaceGeocodeData> getGeoData(@Query(value = "latlng") String latlng,
                                      @Query(value = "language") String language,
                                      @Query(value = "key") String key);

    @FormUrlEncoded
    @POST("api/login")
    Call<UserModel> login(@Field("phone_code") String phone_code,
                          @Field("phone") String phone

    );

    @FormUrlEncoded
    @POST("api/register")
    Call<UserModel> signUpWithoutImage(@Field("name") String name,
                                       @Field("phone_code") String phone_code,
                                       @Field("phone") String phone,
                                       @Field("email") String email,

                                       @Field("address") String address,
                                       @Field("latitude") double latitude,
                                       @Field("longitude") double longitude,
                                       @Field("software_type") String software_type
    );

    @Multipart
    @POST("api/register")
    Call<UserModel> signUpWithImage(@Part("name") RequestBody name,
                                    @Part("phone_code") RequestBody phone_code,
                                    @Part("phone") RequestBody phone,
                                    @Part("email") RequestBody email,
                                    @Part("address") RequestBody address,
                                    @Part("latitude") RequestBody latitude,
                                    @Part("longitude") RequestBody longitude,
                                    @Part("software_type") RequestBody software_type,
                                    @Part MultipartBody.Part logo


    );


    @FormUrlEncoded
    @POST("api/updateProfile")
    Call<UserModel> updateProfileWithoutImage(@Header("Authorization") String bearer_token,
                                              @Field("name") String name,
                                              @Field("phone_code") String phone_code,
                                              @Field("phone") String phone,
                                              @Field("email") String email,

                                              @Field("address") String address,
                                              @Field("latitude") double latitude,
                                              @Field("longitude") double longitude,
                                              @Field("software_type") String software_type
    );

    @Multipart
    @POST("api/updateProfile")
    Call<UserModel> updateProfileWithImage(@Header("Authorization") String bearer_token,
                                           @Part("name") RequestBody name,
                                           @Part("phone_code") RequestBody phone_code,
                                           @Part("phone") RequestBody phone,
                                           @Part("email") RequestBody email,

                                           @Part("address") RequestBody address,
                                           @Part("latitude") RequestBody latitude,
                                           @Part("longitude") RequestBody longitude,
                                           @Part("software_type") RequestBody software_type,
                                           @Part MultipartBody.Part logo


    );

    @GET("api/home-link-filter")
    Call<ProductsDataModel> getProducts(
            @Query(value = "user_id") String user_id,
            @Query(value = "search_key") String search_key
    );

    @GET("api/home-sliders")
    Call<SliderModel> getSlider();

    @GET("api/offerSlider")
    Call<ProductsDataModel> getLatestOffer();

    @GET("api/allOffers")
    Call<ProductsDataModel> getOffer();

    @GET("api/categories")
    Call<DepartmentDataModel> getDepartment();

    @FormUrlEncoded
    @POST("api/my-chat-rooms")
    Call<RoomDataModel> getRooms(@Header("Authorization") String user_token,
                                 @Field("user_id") int user_id
    );

    @GET("api/productFilterByCategory")
    Call<ProductsDataModel> getFilteredProducts(@Query(value = "category_id") int category_id,
                                                @Query(value = "sub_category_id") int sub_category_id,
                                                @Query(value = "using_user_location") String using_my_location,
                                                @Query(value = "using_price") String using_price,
                                                @Query(value = "type_id") int type_id

    );

    @FormUrlEncoded
    @POST("api/getTypesByCatId")
    Call<TypeDataModel> getTypes(@Field("category_id") int category_id

    );

    @FormUrlEncoded
    @POST("api/singleProduct")
    Call<SingleProductDataModel> getProductById(@Field("product_id") int product_id,
                                                @Field("user_id") String user_id

    );

    @FormUrlEncoded
    @POST("api/favouriteProductToggle")
    Call<StatusResponse> like_disliked(@Header("Authorization") String user_token,
                                       @Field("product_id") int product_id
    );

    @FormUrlEncoded
    @POST("api/makeReport")
    Call<StatusResponse> report(@Header("Authorization") String user_token,
                                @Field("product_id") int product_id,
                                @Field("title") String title
    );

    @FormUrlEncoded
    @POST("api/allAttribuitesByCategoryId")
    Call<ItemAddAdsDataModel> getItemsAds(@Field("category_id") int category_id

    );

    @GET("api/myCoupons")
    Call<CouponDataModel> getMyCoupon(@Header("Authorization") String user_token);


    @FormUrlEncoded
    @POST("api/singleCoupon")
    Call<SingleCouponModel> getSingleCoupon(@Field("coupon_id") int coupon_id,
                                            @Field("user_id") int user_id
    );

    @Multipart
    @POST("api/makeNewCoupon")
    Call<StatusResponse> addCoupon(@Header("Authorization") String user_token,
                                   @Part("title") RequestBody title,
                                   @Part("brand_title") RequestBody brand_title,
                                   @Part("coupon_code") RequestBody coupon_code,
                                   @Part("offer_value") RequestBody offer_value,
                                   @Part("from_date") RequestBody from_date,
                                   @Part MultipartBody.Part image

    );


    @Multipart
    @POST("api/addProduct")
    Call<StatusResponse> addAdsWithVideoWithList(@Header("Authorization") String user_token,
                                                 @Part("category_id") RequestBody category_id,
                                                 @Part("title") RequestBody title,
                                                 @Part("price") RequestBody price,
                                                 @Part("address") RequestBody address,
                                                 @Part("latitude") RequestBody latitude,
                                                 @Part("longitude") RequestBody longitude,
                                                 @Part("desc") RequestBody desc,

                                                 @Part MultipartBody.Part main_image,
                                                 @Part MultipartBody.Part vedio,
                                                 @Part List<MultipartBody.Part> images,
                                                 @PartMap() Map<String, RequestBody> map
    );


    @Multipart
    @POST("api/addProduct")
    Call<StatusResponse> addAdsWithoutVideoWithoutList(@Header("Authorization") String user_token,
                                                       @Part("category_id") RequestBody category_id,
                                                       @Part("title") RequestBody title,
                                                       @Part("price") RequestBody price,
                                                       @Part("address") RequestBody address,
                                                       @Part("latitude") RequestBody latitude,
                                                       @Part("longitude") RequestBody longitude,
                                                       @Part("desc") RequestBody desc,
                                                       @Part MultipartBody.Part main_image,
                                                       @Part List<MultipartBody.Part> images

    );


    @Multipart
    @POST("api/addProduct")
    Call<StatusResponse> addAdsWithoutVideoWithList(@Header("Authorization") String user_token,
                                                    @Part("category_id") RequestBody category_id,
                                                    @Part("title") RequestBody title,
                                                    @Part("price") RequestBody price,
                                                    @Part("address") RequestBody address,
                                                    @Part("latitude") RequestBody latitude,
                                                    @Part("longitude") RequestBody longitude,
                                                    @Part("desc") RequestBody desc,
                                                    @Part MultipartBody.Part main_image,
                                                    @Part List<MultipartBody.Part> images,
                                                    @PartMap() Map<String, RequestBody> map

    );


    @Multipart
    @POST("api/addProduct")
    Call<StatusResponse> addAdsWithVideoWithoutList(@Header("Authorization") String user_token,
                                                    @Part("category_id") RequestBody category_id,
                                                    @Part("title") RequestBody title,
                                                    @Part("price") RequestBody price,
                                                    @Part("address") RequestBody address,
                                                    @Part("latitude") RequestBody latitude,
                                                    @Part("longitude") RequestBody longitude,
                                                    @Part("desc") RequestBody desc,
                                                    @Part MultipartBody.Part main_image,
                                                    @Part MultipartBody.Part vedio,
                                                    @Part List<MultipartBody.Part> images

    );


    @FormUrlEncoded
    @POST("api/makeActionOnCoupon")
    Call<StatusResponse> couponAction(@Header("Authorization") String user_token,
                                      @Field("coupon_id") int coupon_id,
                                      @Field("like_kind") String like_kind
    );

    @FormUrlEncoded
    @POST("api/deleteCoupon")
    Call<StatusResponse> deleteCoupon(@Header("Authorization") String user_token,
                                      @Field("coupon_id") int coupon_id);


    @FormUrlEncoded
    @POST("api/firebase-tokens")
    Call<StatusResponse> updateFirebaseToken(@Field("firebase_token") String firebase_token,
                                             @Field("user_id") int user_id,
                                             @Field("software_type") String software_type

    );

    @FormUrlEncoded
    @POST("api/firebase/token/delete")
    Call<StatusResponse> deleteFirebaseToken(@Field("firebase_token") String firebase_token,
                                             @Field("user_id") int user_id

    );

    @POST("api/logout")
    Call<StatusResponse> logout(@Header("Authorization") String user_token);


    @GET("api/myProducts")
    Call<ProductsDataModel> getMyProducts(@Header("Authorization") String user_token);

    @FormUrlEncoded
    @POST("api/deleteProduct")
    Call<StatusResponse> deleteProduct(@Header("Authorization") String user_token,
                                       @Field("product_id") int product_id

    );

    @GET("api/myFavouriteProducts")
    Call<ProductsDataModel> getMyFavorite(@Header("Authorization") String user_token);


    @GET("api/my-notifications")
    Call<NotificationDataModel> getNotification(@Header("Authorization") String user_token);

    @FormUrlEncoded
    @POST("api/notification/remove")
    Call<StatusResponse> deleteNotification(@Header("Authorization") String user_token,
                                            @Field("notification_id") int notification_id
    );

    @FormUrlEncoded
    @POST("api/contactUs")
    Call<StatusResponse> contactUs(@Field("name") String name,
                                   @Field("email") String email,
                                   @Field("phone") String phone,
                                   @Field("message") String message
    );

    @FormUrlEncoded
    @POST("api/single-chat-room")
    Call<MessageDataModel> getChatMessages(@Header("Authorization") String bearer_token,
                                           @Field("room_id") int room_id

    );

    @FormUrlEncoded
    @POST("api/message/send")
    Call<SingleMessageDataModel> sendChatMessage(@Header("Authorization") String bearer_token,
                                                 @Field("room_id") int room_id,
                                                 @Field("from_user_id") int from_user_id,
                                                 @Field("to_user_id") int to_user_id,
                                                 @Field("message_kind") String message_kind,
                                                 @Field("date") long date,
                                                 @Field("message") String message


    );

    @Multipart
    @POST("api/message/send")
    Call<SingleMessageDataModel> sendChatAttachment(@Header("Authorization") String bearer_token,
                                                    @Part("room_id") RequestBody room_id,
                                                    @Part("from_user_id") RequestBody from_user_id,
                                                    @Part("to_user_id") RequestBody to_user_id,
                                                    @Part("message_kind") RequestBody message_kind,
                                                    @Part("date") RequestBody date,
                                                    @Part MultipartBody.Part attachment
    );


    @FormUrlEncoded
    @POST("api/chatRoom/get")
    Call<RoomDataModel2> createRoom(@Header("Authorization") String user_token,
                                    @Field("from_user_id") int from_user_id,
                                    @Field("to_user_id") int to_user_id
    );


    @GET("api/adminChatRoomGet")
    Call<AdminRoomDataModel> createAdminRoom(@Header("Authorization") String user_token

    );

    @GET("api/allChatRoomData")
    Call<AdminMessageDataModel> getAdminChatMessage(@Header("Authorization") String user_token,
                                                    @Query("ad_id") String ad_id);


    @FormUrlEncoded
    @POST("api/sendMessageToAdmin")
    Call<SingleAdminMessageDataModel> sendAdminChatMessage(@Header("Authorization") String bearer_token,
                                                           @Field("room_id") int room_id,
                                                           @Field("message_kind") String message_kind,
                                                           @Field("date") long date,
                                                           @Field("message") String message


    );

    @Multipart
    @POST("api/sendMessageToAdmin")
    Call<SingleAdminMessageDataModel> sendAdminChatAttachment(@Header("Authorization") String bearer_token,
                                                              @Part("room_id") RequestBody room_id,
                                                              @Part("message_kind") RequestBody message_kind,
                                                              @Part("date") RequestBody date,
                                                              @Part MultipartBody.Part attachment
    );


    @GET("api/app/info")
    Call<SettingDataModel> getSettings();


    @FormUrlEncoded
    @POST("api/profileOfOther")
    Call<OtherProfileDataModel> getOtherProfile(@Header("Authorization") String bearer_token,
                                                @Field("user_id") int user_id,
                                                @Field("other_user_id") int other_user_id


    );

    @FormUrlEncoded
    @POST("api/followUserToggle")
    Call<StatusResponse> follow_un_follow(@Header("Authorization") String bearer_token,
                                          @Field("other_user_id") int other_user_id


    );

    @GET("api/categories")
    Call<AllCatogryModel> getcategories(

    );

    @GET("api/googleCategories")
    Call<CategoryDataModel> getGoogleCategory();

    @GET("place/nearbysearch/json")
    Call<NearbyModel> nearbyPlaceRankBy(@Query(value = "location") String location,
                                        @Query(value = "keyword") String keyword,
                                        @Query(value = "rankby") String rankby,
                                        @Query(value = "language") String language,
                                        @Query(value = "pagetoken") String pagetoken,
                                        @Query(value = "key") String key
    );

    @GET("place/details/json")
    Call<PlaceDetailsModel> getPlaceDetails(@Query(value = "placeid") String placeid,
                                            @Query(value = "fields") String fields,
                                            @Query(value = "language") String language,
                                            @Query(value = "key") String key
    );

    @GET("api/all-governorates")
    Call<GovernorateDataModel> getGovernorate();

    @GET("api/sponsors")
    Call<SponsorsModel> getSponsor();

    @GET("api/news")
    Call<NewsModel> getNews();

    @FormUrlEncoded
    @POST("api/productFilterByCategory")
    Call<ProductsDataModel> filterProducts(
            @Field(value = "user_id") String user_id,
            @Field(value = "search_title") String search_title,
            @Field(value = "governorate_id") String governorate_id,
            @Field(value = "type_id") String type_id,
            @Field(value = "category_id") String category_id
    );

    @FormUrlEncoded
    @POST("api/favouriteProductToggle")
    Call<ProductsDataModel> likeDislike(@Header("Authorization") String token,
                                        @Field("product_id") int product_id);


    @FormUrlEncoded
    @POST("api/getCommentsByproductIDOrGuideID")
    Call<CommentDataModel> getComments(@Field("product_id") String product_id,
                                       @Field("country_guide_id") String country_guide_id,
                                       @Field("is_limited") String limit);


    @FormUrlEncoded
    @POST("api/addNewComment")
    Call<SingleCommentDataModel> sendComments(@Header("Authorization") String token,
                                              @Field("product_id") String product_id,
                                              @Field("country_guide_id") String country_guide_id,
                                              @Field("desc") String desc);


    @FormUrlEncoded
    @POST("api/getGuidesByGoogleCat")
    Call<PlaceDataModel> getPlaceByCategory(@Header("Authorization") String token,
                                            @Field("google_category_id") String google_category_id);

    @Multipart
    @POST("api/addNewGuide")
    Call<StatusResponse> addGuide(@Header("Authorization") String user_token,
                                  @Part("google_category_id") RequestBody google_category_id,
                                  @Part("governorate_id") RequestBody governorate_id,
                                  @Part("whatsapp_number") RequestBody whatsapp_number,
                                  @Part("address") RequestBody address,
                                  @Part("latitude") RequestBody latitude,
                                  @Part("longitude") RequestBody longitude,
                                  @Part("desc") RequestBody desc,
                                  @Part("title") RequestBody title,

                                  @Part MultipartBody.Part main_image,
                                  @Part List<MultipartBody.Part> images

    );

    @GET("api/packages")
    Call<PackageDataModel> getPackages();

    @FormUrlEncoded
    @POST("api/payment")
    Call<InvoiceModel> payPackge(@Header("Authorization") String token,
                                 @Field(value = "user_id") String user_id,
                                 @Field(value = "package_id") String package_id
    );

    @GET("api/getProfile")
    Call<UserModel> getProfile(@Header("Authorization") String bearer_token,
                               @Query("user_id") int user_id
    );

    @FormUrlEncoded
    @POST("api/activeProduct")
    Call<StatusResponse> changeAdStatus(@Header("Authorization") String token,
                                        @Field(value = "product_id") String product_id
    );

    @Multipart
    @POST("api/editProduct")
    Observable<Response<StatusResponse>> editAdd(@Header("Authorization") String user_token,
                                                 @Part("product_id") RequestBody product_id,
                                                 @Part("category_id") RequestBody category_id,
                                                 @Part("title") RequestBody title,
                                                 @Part("price") RequestBody price,
                                                 @Part("address") RequestBody address,
                                                 @Part("latitude") RequestBody latitude,
                                                 @Part("longitude") RequestBody longitude,
                                                 @Part("desc") RequestBody desc,
                                                 @Part MultipartBody.Part main_image,
                                                 @Part MultipartBody.Part vedio,
                                                 @Part List<MultipartBody.Part> images

    );

    @FormUrlEncoded
    @POST("api/editProduct")
    Observable<Response<StatusResponse>> deleteImages(@Header("Authorization") String user_token,
                                                 @Field("product_id") String product_id,
                                                 @Field("ids[]") List<Integer> ids

    );
}

