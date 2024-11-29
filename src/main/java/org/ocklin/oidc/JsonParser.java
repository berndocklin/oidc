package org.ocklin.oidc;

import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public class JsonParser {

    public static void main(String[] args) {
        // String jwt = "{\"exp\":1730823429,\"iat\":1730823129,\"auth_time\":1730822407,\"jti\":\"a5d21db5-7744-4756-843f-fbca4f6217c7\",\"iss\":\"http://localhost:8080/realms/hcd\",\"aud\":\"account\",\"sub\":\"85daf12b-18fc-4a6d-9b12-a5f5182b8e46\",\"typ\":\"Bearer\",\"azp\":\"myclient\",\"sid\":\"6185f931-8ec8-4201-9639-c601645c5279\",\"acr\":\"0\",\"allowed-origins\":[\"http://localhost:5443/\",\"https://localhost:5443/\"],\"realm_access\":{\"roles\":[\"default-roles-hcd\",\"offline_access\",\"uma_authorization\"]},\"resource_access\":{\"account\":{\"roles\":[\"manage-account\",\"manage-account-links\",\"view-profile\"]}},\"scope\":\"openid address profile phone email\",\"address\":{},\"email_verified\":false,\"name\":\"bernhard ocklin\",\"preferred_username\":\"myuser\",\"given_name\":\"bernhard\",\"family_name\":\"ocklin\",\"email\":\"bernd@ocklin.de\"}";
        String json = "{\"access_token\":\"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI1ZXQ3US01eE5Cd2N6QjkyRlE1bHN2Q0R6bkZ2Vkp1SmVEd3ZzdS04OXZFIn0.eyJleHAiOjE3MzA3NTE2MDgsImlhdCI6MTczMDc1MTMwOCwiYXV0aF90aW1lIjoxNzMwNzUxMzA4LCJqdGkiOiI5MGZlNjNiMi05ZGY5LTQ0N2QtYWE2Ni04ZDZmN2VlYTExODMiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvcmVhbG1zL2hjZCIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI4NWRhZjEyYi0xOGZjLTRhNmQtOWIxMi1hNWY1MTgyYjhlNDYiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJteWNsaWVudCIsInNpZCI6Ijc1ZmY2ZGU2LTQxNTAtNDZhYi1hZjI2LWYzMmIxMDkxYTlkZSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cDovL2xvY2FsaG9zdDo1NDQzLyIsImh0dHBzOi8vbG9jYWxob3N0OjU0NDMvIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkZWZhdWx0LXJvbGVzLWhjZCIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBhZGRyZXNzIHByb2ZpbGUgcGhvbmUgZW1haWwiLCJhZGRyZXNzIjp7fSwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiYmVybmhhcmQgb2NrbGluIiwicHJlZmVycmVkX3VzZXJuYW1lIjoibXl1c2VyIiwiZ2l2ZW5fbmFtZSI6ImJlcm5oYXJkIiwiZmFtaWx5X25hbWUiOiJvY2tsaW4iLCJlbWFpbCI6ImJlcm5kQG9ja2xpbi5kZSJ9.L4IKtG8SxFeCmGUT1Aj8GtbPdgqCIL2UBwC4cU2eGQ6cqiJ1jBeSY5PuG48l8kA0Lj12DtCJk5ukFPTvxG5Vt7Q1UGqUqWDFR40IzxLJKoAONFOhklvH_08_r4FJwN9VYmsZlDprYUgk1JA4vrJNu0vYQFsVMe68MbrM1ylkWkoSBmN54UIAD7hjaBW5kKyHWuYzbQKn6WP9n-SGGE_kKS6a-S66LB60BVRj5fics_kPX5Gy5B0SAAQfQmEQ4ZRxn51a58CZa0Thc7a0GYM39kPnY8UZTsYfyJxML1QUPUvkiSWCgP4gQEZJRXkZe33whnHx-SHy3XVDJJ8LvUV2bQ\",\"expires_in\":300,\"refresh_expires_in\":1800,\"refresh_token\":\"eyJhbGciOiJIUzUxMiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhODJhNjBmMC0yODBiLTQ5MDktODY0Ni04ODkyNGYyYzE4ZjkifQ.eyJleHAiOjE3MzA3NTMxMDgsImlhdCI6MTczMDc1MTMwOCwianRpIjoiZDJiNzM4MWMtYjRhZS00NTI5LWFiZjItYjg5M2NjYTAxMDVkIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9oY2QiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvcmVhbG1zL2hjZCIsInN1YiI6Ijg1ZGFmMTJiLTE4ZmMtNGE2ZC05YjEyLWE1ZjUxODJiOGU0NiIsInR5cCI6IlJlZnJlc2giLCJhenAiOiJteWNsaWVudCIsInNpZCI6Ijc1ZmY2ZGU2LTQxNTAtNDZhYi1hZjI2LWYzMmIxMDkxYTlkZSIsInNjb3BlIjoib3BlbmlkIGFkZHJlc3Mgcm9sZXMgcHJvZmlsZSB3ZWItb3JpZ2lucyBiYXNpYyBwaG9uZSBhY3IgbG9naW4gZW1haWwifQ.b1EDw-deoTJxSh7z1Il1nhAkq1Xi8fUJCVdD7w4jYNcPHr-Nldag5Csl6tjvSDbgOuSwVuG2Thu-KjswpnWYVw\",\"token_type\":\"Bearer\",\"id_token\":\"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI1ZXQ3US01eE5Cd2N6QjkyRlE1bHN2Q0R6bkZ2Vkp1SmVEd3ZzdS04OXZFIn0.eyJleHAiOjE3MzA3NTE2MDgsImlhdCI6MTczMDc1MTMwOCwiYXV0aF90aW1lIjoxNzMwNzUxMzA4LCJqdGkiOiJlMGVjOWNhYi1kNWFmLTQ5YWYtYmNkZi1kZGMxNTEwMWNlMGIiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvcmVhbG1zL2hjZCIsImF1ZCI6Im15Y2xpZW50Iiwic3ViIjoiODVkYWYxMmItMThmYy00YTZkLTliMTItYTVmNTE4MmI4ZTQ2IiwidHlwIjoiSUQiLCJhenAiOiJteWNsaWVudCIsIm5vbmNlIjoiMjRWVzlXNUlUWDJST080WFhEVTUiLCJzaWQiOiI3NWZmNmRlNi00MTUwLTQ2YWItYWYyNi1mMzJiMTA5MWE5ZGUiLCJhdF9oYXNoIjoicG9mQnRHZ3NCNWFpYUZ6UGRtNF9qQSIsImFjciI6IjEiLCJhZGRyZXNzIjp7fSwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiYmVybmhhcmQgb2NrbGluIiwicHJlZmVycmVkX3VzZXJuYW1lIjoibXl1c2VyIiwiZ2l2ZW5fbmFtZSI6ImJlcm5oYXJkIiwiZmFtaWx5X25hbWUiOiJvY2tsaW4iLCJlbWFpbCI6ImJlcm5kQG9ja2xpbi5kZSJ9.gv0xjRsT_RfxkUdQUFhMIRpvGMBOvyufXjYX5lsruh9llaf0ZhfxO0cidoAl-GIpxxXFqRZaI5tFIP0BRB9vBESE1u91IwhbxxyS9O2RSTBVDpBXN7iiZ4IHiDL1NcHCHOmekVpZw9ovXLGki344KRHfiWiPiNGJ4RD3Aaom9Nu6iNAxV4TxUFJeydMRaMfof-riOPYE-8HDeyMJemvDParI7_KpVhrD3ZRoSirpGYFb24eduXNg4r4SGPh_eCAWYh4scQ6CTBNfj-z4JiD9NzARlSKYj7i4RJWj2FQ-hH_U2G5nAeknzklX2bzf8ay5Znaz24hLrLk3A3MIwd1XZA\",\"not-before-policy\":0,\"session_state\":\"75ff6de6-4150-46ab-af26-f32b1091a9de\",\"scope\":\"openid address profile phone email\"}";

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Iterator<Map.Entry<String, JsonNode>> iter = rootNode.fields();
        while (iter.hasNext()) {
            Map.Entry<String, JsonNode> entry = iter.next();
            System.out.println(entry.getKey() + ": " + entry.getValue().asText());
        }

        String name = rootNode.path("iss").asText();
        System.out.println("Name: " + name);
    }
}