package com.siddartharao.hifriends;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class FcmNotificationsSender  {

    String userFcmToken;
    String title;
    String body;
    Context mContext;
    Activity mActivity;


    private RequestQueue requestQueue;
    private final String postUrl = "https://fcm.googleapis.com/v1/projects/hi-friends-90ce5/messages:send";
    //private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private final String fcmServerKey = "ya29.c.c0ASRK0GYtgNybWOULOvE4F7nYPB2IhL1qkAEGzCU9PBJDcGpej7QqdjUCZOIQntJIK9G2ud_D1J77YD6053eVc65UZeb9ldntb6Fa8nIyS3wEe_4wNYQYeobRipt_R86LkBJez97P17lLxRbprrHbhsPTTcSlPCzwGqVGHwKhCCE-msapNPSpa_YjTBt-_EetUYwMJza2Fp1VHAlFiSgrcQjuop7MWVr3Vas29QCZBZanNWvImC8K0izuugpKELpVgWoso1eK_uQynnDEgw6aFOpgfinUfIz-UWrxd56JKj2qHq0erdsQoqR_ys2qfV-s9TVgRMDKZBtJOJ3523tZSIcHcQs5APRdadJp-LH9LY8epHM6k_2JmO6bN385P40iSui1f3Qn-nX4eo1mFS6psxdaRh5qhOy0UMmZnrxFFm9rhjyrs7lf-0BSiacgs7SXvdqqqp8kV8gvhMRIo5qfcw8t5sFMVyljz7zankdRxtkerZmwXJhe6p0iqaB5BpFt40W4MIen9nkr-MBfq3m83X2FhmYlekuIR0dSms-ngsxqQ4z9FdlYXn9RJYQt0jsQ9ipmRcF6Q5oY64-IX2Jz_rQkwIS445JJddImMfbiqOkmOosB_yz6yUF-Rp7-yF3kjf-d8-lqiR2wc8b0B87mn1aO4MB7jW4cZSBeaxcq0id8xah-xlhuxyYroniI8keZRSXq1og2WckQIdbdJw_8XReWb41YRWIBJmlXhyygha5Wkmxiv2Fu6qMsYOaaglbIoUXVZhFkntm8f_5SdidoQB5x2-heRYnZ_USuuclz8fY1XseaOp-VUmfiu8BpU3sg7n4uZI86ig0dvBoOS_gBms_8I1k-ipWaz5YU8zRa9aUMfVmBJqSc46qvi2_Qtzofyq83mQxBXRfnZ5MOugXtBQx7VutneJgRp2mukburmRmgRxoFvesJYnsWfcvQq24nIMuodO2irVm64lV1eeJ06xYIWb3nX3OwxBQs_83bJXy1MgV7MXmFSqg";

    public FcmNotificationsSender(String userFcmToken, String title, String body, Context mContext, Activity mActivity) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    public void SendNotifications() {

        requestQueue = Volley.newRequestQueue(mActivity);
        JSONObject mainObj = new JSONObject();
        try {
            JSONObject messageObj = new JSONObject();//new
            messageObj.put("token", userFcmToken);//to mainObj
            JSONObject notiObject = new JSONObject();
            notiObject.put("title", title);
            notiObject.put("body", body);
            //notiObject.put("icon", R.drawable.hi_logo_notification); // enter icon that exists in drawable only

            messageObj.put("notification", notiObject);

            mainObj.put("message", messageObj);//new

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    // code run is got response
                    Log.d("FCM", "Notification Sent Successfully: " + response.toString());

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // code run is got error
                    //Log.e("notify",String.valueOf(error));
                    //Log.e("Volley", "Error Sending Notification: " + error.getMessage());
                    //error.printStackTrace();
                    if (error.networkResponse != null) {
                        Log.e("Volley", "Error Status Code: " + error.networkResponse.statusCode);
                        try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            Log.e("Volley", "Error Response Body: " + responseBody);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("Volley", "Network Error: " + error.getMessage());
                    }

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {

                    Map<String, String> header = new HashMap<>();
                    header.put("Content-Type", "application/json");
                    header.put("Authorization", "Bearer " + fcmServerKey);//Bearer ya29.
                    return header;

                }
            };
            requestQueue.add(request);


        } catch (JSONException e) {
            e.printStackTrace();
        }




    }
}
