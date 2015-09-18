/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/
package org.apache.cordova.filetransfer;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Encapsulates the result and/or status of uploading a file to a remote server.
 */
public class FileUploadResult {

    private long bytesSent = 0;         // bytes sent
    private int responseCode = -1;      // HTTP response code
    private String response = null;     // HTTP response
    private String objectId = null;     // FileTransfer object id
    private String headers = null;      // MOD russa: android-response-headers

    public long getBytesSent() {
        return bytesSent;
    }

    public void setBytesSent(long bytes) {
        this.bytesSent = bytes;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
    
    /* MOD russa: android-response-headers */
    public void setResponseHeaders(Map<String, List<String>> headerFields) {
        StringBuilder sb = new StringBuilder();
        
        HashSet<String> valueSet = new HashSet<String>();
        
        int written = 0;
        for(Map.Entry<String, List<String>> headerField : headerFields.entrySet()){
            
            String name = headerField.getKey();
            //omit status-line (i.e. header-field with key null)
            if(name == null || name.length() < 1){
                continue;
            }
            
            if(++written > 1){
                sb.append("\n");
            }
            
            sb.append(name);
            sb.append(": ");
            
            //reset Set for removing duplicate values
            //Note: cannot simply add headerField.getValue() to the Set, since we need the original ordering, see
            //      http://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2
            //... The order in which header fields with the same field-name are received is therefore significant to the interpretation...
            valueSet.clear();
            
            int count = 0;
            for(String value : headerField.getValue()){
                
                //skip values that we already have
                if(valueSet.contains(value)){
                    continue;
                }
                
                valueSet.add(value);
                
                //multiple values may occur in header fields where each value is separated via a comma
                // see http://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2:
                // ... Multiple message-header fields with the same field-name MAY be present in a message if and only if...
                if(++count > 1){
                    sb.append(", ");
                }
                sb.append(value);
            }
            
        }
        
        this.headers = sb.toString();
        
    }

    public JSONObject toJSONObject() throws JSONException {
        return new JSONObject(
                "{bytesSent:" + bytesSent +
                ",responseCode:" + responseCode +
                ",headers:" + JSONObject.quote(headers) +   //MOD russa: android-response-headers
                ",response:" + JSONObject.quote(response) +
                ",objectId:" + JSONObject.quote(objectId) + "}");
    }
}
