package com.scleroid.nemai.network;

import com.scleroid.nemai.data.models.Courier;
import com.scleroid.nemai.data.models.Parcel;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Copyright (C) 2018
 *
 * @author Ganesh Kaple
 * @since 7/23/18
 */
public interface ApiService {

	@FormUrlEncoded
	@POST("/alreadyexisteduser")
	Single<Boolean> isAUser(@Query("email_id") String userName);


	Completable subMitCourier(@Body Courier courier);

	//TODO Need to test this call first

	Completable subMitCouriers(@Body List<Courier> couriers);


	@POST("/insert")
	Flowable<List<Courier>> getCouriers(@Body Parcel header);

	@FormUrlEncoded
	Completable registerUser(@Query("fname") String first_name, @Query("lname") String  last_name,
	                         @Query("email") String email, @Query("phone")String phone,
	                         @Query("gender") String gender, @Query("password") String password,
	                         @Query("login_method") String loginMethod);
	@FormUrlEncoded
	Single<String> loginUser(@Query("email_id") String email, @Query("password") String password);



}
